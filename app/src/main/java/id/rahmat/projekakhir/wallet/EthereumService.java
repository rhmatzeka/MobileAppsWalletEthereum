package id.rahmat.projekakhir.wallet;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class EthereumService {

    private final Map<EthereumNetwork, Web3j> clients = new EnumMap<>(EthereumNetwork.class);

    public BigDecimal getBalance(String address, EthereumNetwork network) throws IOException {
        BigInteger wei = getClient(network)
                .ethGetBalance(address, DefaultBlockParameterName.LATEST)
                .send()
                .getBalance();
        return Convert.fromWei(new BigDecimal(wei), Convert.Unit.ETHER);
    }

    public GasEstimation estimateNativeTransfer(String from, String to, BigDecimal amountEth,
                                                EthereumNetwork network) throws IOException {
        Web3j web3j = getClient(network);
        BigInteger value = Convert.toWei(amountEth, Convert.Unit.ETHER).toBigInteger();
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();

        Transaction transaction = Transaction.createEtherTransaction(from, null, gasPrice, null, to, value);
        BigInteger gasLimit = web3j.ethEstimateGas(transaction).send().getAmountUsed();
        if (gasLimit == null || BigInteger.ZERO.equals(gasLimit)) {
            gasLimit = BigInteger.valueOf(21000L);
        }

        BigDecimal gasFeeEth = Convert.fromWei(new BigDecimal(gasPrice.multiply(gasLimit)), Convert.Unit.ETHER);
        return new GasEstimation(gasLimit, gasPrice, gasFeeEth);
    }

    public String sendNativeTransfer(Credentials credentials, String to, BigDecimal amountEth,
                                     EthereumNetwork network) throws Exception {
        return Transfer.sendFunds(getClient(network), credentials, to, amountEth, Convert.Unit.ETHER)
                .send()
                .getTransactionHash();
    }

    public BigDecimal getErc20Balance(String walletAddress, String contractAddress, int decimals,
                                      EthereumNetwork network) throws IOException {
        Function function = new Function(
                "balanceOf",
                Arrays.asList(new Address(walletAddress)),
                Arrays.asList(new TypeReference<Uint256>() {})
        );
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(walletAddress, contractAddress, data);
        EthCall response = getClient(network).ethCall(transaction, DefaultBlockParameterName.LATEST).send();
        List<Type> outputs = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        if (outputs == null || outputs.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigInteger rawBalance = (BigInteger) outputs.get(0).getValue();
        return new BigDecimal(rawBalance).movePointLeft(decimals);
    }

    private Web3j getClient(EthereumNetwork network) {
        Web3j existing = clients.get(network);
        if (existing != null) {
            return existing;
        }
        Web3j client = Web3j.build(new HttpService(network.getRpcUrl()));
        clients.put(network, client);
        return client;
    }
}

package id.rahmat.projekakhir.data.repository;

import androidx.lifecycle.LiveData;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import id.rahmat.projekakhir.BuildConfig;
import id.rahmat.projekakhir.data.local.TransactionDao;
import id.rahmat.projekakhir.data.local.TransactionEntity;
import id.rahmat.projekakhir.data.remote.EtherscanApi;
import id.rahmat.projekakhir.data.remote.model.EtherscanTransactionItem;
import id.rahmat.projekakhir.data.remote.model.EtherscanTransactionsResponse;
import id.rahmat.projekakhir.utils.AppPreferences;
import id.rahmat.projekakhir.wallet.EthereumNetwork;
import id.rahmat.projekakhir.wallet.EthereumNetworkRegistry;
import retrofit2.Response;

public class TransactionRepository {

    public static final String DIRECTION_SENT = "sent";
    public static final String DIRECTION_RECEIVED = "received";

    private final TransactionDao transactionDao;
    private final EtherscanApi mainnetApi;
    private final EtherscanApi sepoliaApi;
    private final AppPreferences appPreferences;

    public TransactionRepository(TransactionDao transactionDao, EtherscanApi mainnetApi,
                                 EtherscanApi sepoliaApi, AppPreferences appPreferences) {
        this.transactionDao = transactionDao;
        this.mainnetApi = mainnetApi;
        this.sepoliaApi = sepoliaApi;
        this.appPreferences = appPreferences;
    }

    public LiveData<List<TransactionEntity>> observeAll(String walletAddress) {
        return transactionDao.observeAll(walletAddress);
    }

    public LiveData<List<TransactionEntity>> observeByDirection(String walletAddress, String direction) {
        return transactionDao.observeByDirection(walletAddress, direction);
    }

    public void insert(TransactionEntity transactionEntity) {
        transactionDao.insert(transactionEntity);
    }

    public void syncFromEtherscan(String walletAddress) throws Exception {
        if (walletAddress == null || walletAddress.isEmpty()) {
            return;
        }

        EthereumNetwork network = EthereumNetworkRegistry.resolve(appPreferences.getSelectedNetwork(), appPreferences);
        if (!network.supportsExplorerSync()) {
            return;
        }
        EtherscanApi api = EthereumNetwork.MAINNET.isSameNetwork(network) ? mainnetApi : sepoliaApi;
        Response<EtherscanTransactionsResponse> response = api
                .getTransactions("account", "txlist", walletAddress, "desc", BuildConfig.ETHERSCAN_API_KEY)
                .execute();

        EtherscanTransactionsResponse body = response.body();
        if (body == null || body.result == null) {
            return;
        }

        List<TransactionEntity> transactionEntities = new ArrayList<>();
        for (EtherscanTransactionItem item : body.result) {
            transactionEntities.add(mapToEntity(walletAddress, item, network));
        }
        transactionDao.insertAll(transactionEntities);
    }

    private TransactionEntity mapToEntity(String walletAddress, EtherscanTransactionItem item, EthereumNetwork network) {
        TransactionEntity entity = new TransactionEntity();
        entity.hash = item.hash;
        entity.walletAddress = walletAddress;
        entity.fromAddress = item.from;
        entity.toAddress = item.to;
        entity.amountEth = Convert.fromWei(new BigDecimal(new BigInteger(item.value)), Convert.Unit.ETHER).toPlainString();

        BigDecimal gasFee = BigDecimal.ZERO;
        if (item.gasPrice != null && item.gasUsed != null) {
            gasFee = Convert.fromWei(
                    new BigDecimal(new BigInteger(item.gasPrice).multiply(new BigInteger(item.gasUsed))),
                    Convert.Unit.ETHER
            );
        }
        entity.gasFeeEth = gasFee.toPlainString();
        entity.timestamp = Long.parseLong(item.timeStamp) * 1000L;
        entity.status = "0".equals(item.isError) ? "Success" : "Failed";
        entity.direction = walletAddress.equalsIgnoreCase(item.from) ? DIRECTION_SENT : DIRECTION_RECEIVED;
        entity.network = network.getKey();
        entity.source = "etherscan";
        return entity;
    }
}

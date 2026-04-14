package id.rahmat.projekakhir.wallet;

import java.math.BigDecimal;
import java.math.BigInteger;

public class GasEstimation {

    private final BigInteger gasLimit;
    private final BigInteger gasPriceWei;
    private final BigDecimal gasFeeEth;

    public GasEstimation(BigInteger gasLimit, BigInteger gasPriceWei, BigDecimal gasFeeEth) {
        this.gasLimit = gasLimit;
        this.gasPriceWei = gasPriceWei;
        this.gasFeeEth = gasFeeEth;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public BigInteger getGasPriceWei() {
        return gasPriceWei;
    }

    public BigDecimal getGasFeeEth() {
        return gasFeeEth;
    }
}

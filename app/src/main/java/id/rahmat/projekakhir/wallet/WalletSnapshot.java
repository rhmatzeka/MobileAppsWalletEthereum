package id.rahmat.projekakhir.wallet;

import java.math.BigDecimal;

public class WalletSnapshot {

    private final String address;
    private final String networkName;
    private final BigDecimal ethBalance;
    private final BigDecimal ethPriceUsd;
    private final BigDecimal ethPriceIdr;

    public WalletSnapshot(String address, String networkName, BigDecimal ethBalance,
                          BigDecimal ethPriceUsd, BigDecimal ethPriceIdr) {
        this.address = address;
        this.networkName = networkName;
        this.ethBalance = ethBalance;
        this.ethPriceUsd = ethPriceUsd;
        this.ethPriceIdr = ethPriceIdr;
    }

    public String getAddress() {
        return address;
    }

    public String getNetworkName() {
        return networkName;
    }

    public BigDecimal getEthBalance() {
        return ethBalance;
    }

    public BigDecimal getEthPriceUsd() {
        return ethPriceUsd;
    }

    public BigDecimal getEthPriceIdr() {
        return ethPriceIdr;
    }
}

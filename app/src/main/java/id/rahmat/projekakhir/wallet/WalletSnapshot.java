package id.rahmat.projekakhir.wallet;

import java.math.BigDecimal;

public class WalletSnapshot {

    private final String address;
    private final String networkName;
    private final String nativeAssetName;
    private final String nativeAssetSymbol;
    private final BigDecimal nativeBalance;
    private final BigDecimal nativePriceUsd;
    private final BigDecimal nativePriceIdr;

    public WalletSnapshot(String address, String networkName, String nativeAssetName,
                          String nativeAssetSymbol, BigDecimal nativeBalance,
                          BigDecimal nativePriceUsd, BigDecimal nativePriceIdr) {
        this.address = address;
        this.networkName = networkName;
        this.nativeAssetName = nativeAssetName;
        this.nativeAssetSymbol = nativeAssetSymbol;
        this.nativeBalance = nativeBalance;
        this.nativePriceUsd = nativePriceUsd;
        this.nativePriceIdr = nativePriceIdr;
    }

    public String getAddress() {
        return address;
    }

    public String getNetworkName() {
        return networkName;
    }

    public String getNativeAssetName() {
        return nativeAssetName;
    }

    public String getNativeAssetSymbol() {
        return nativeAssetSymbol;
    }

    public BigDecimal getNativeBalance() {
        return nativeBalance;
    }

    public BigDecimal getNativePriceUsd() {
        return nativePriceUsd;
    }

    public BigDecimal getNativePriceIdr() {
        return nativePriceIdr;
    }

    public BigDecimal getEthBalance() {
        return nativeBalance;
    }

    public BigDecimal getEthPriceUsd() {
        return nativePriceUsd;
    }

    public BigDecimal getEthPriceIdr() {
        return nativePriceIdr;
    }
}

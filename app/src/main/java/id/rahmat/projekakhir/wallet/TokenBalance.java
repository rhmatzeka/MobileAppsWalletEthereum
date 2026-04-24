package id.rahmat.projekakhir.wallet;

import java.math.BigDecimal;

public class TokenBalance {

    public final String name;
    public final String networkName;
    public final String symbol;
    public final BigDecimal balance;
    public final String imageUrl;
    public final BigDecimal unitPriceIdr;
    public final BigDecimal unitPriceUsd;
    public final boolean nativeAsset;

    public TokenBalance(String name, String networkName, String symbol, BigDecimal balance, String imageUrl,
                        BigDecimal unitPriceIdr, BigDecimal unitPriceUsd, boolean nativeAsset) {
        this.name = name;
        this.networkName = networkName;
        this.symbol = symbol;
        this.balance = balance;
        this.imageUrl = imageUrl;
        this.unitPriceIdr = unitPriceIdr;
        this.unitPriceUsd = unitPriceUsd;
        this.nativeAsset = nativeAsset;
    }
}

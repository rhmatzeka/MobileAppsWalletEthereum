package id.rahmat.projekakhir.ui.home;

public class NftItem {

    private final String collectionName;
    private final String tokenIdLabel;
    private final String networkName;
    private final String shortContract;
    private final String symbol;
    private final String imageUrl;

    public NftItem(String collectionName, String tokenIdLabel, String networkName,
                   String shortContract, String symbol, String imageUrl) {
        this.collectionName = collectionName;
        this.tokenIdLabel = tokenIdLabel;
        this.networkName = networkName;
        this.shortContract = shortContract;
        this.symbol = symbol;
        this.imageUrl = imageUrl;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getTokenIdLabel() {
        return tokenIdLabel;
    }

    public String getNetworkName() {
        return networkName;
    }

    public String getShortContract() {
        return shortContract;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

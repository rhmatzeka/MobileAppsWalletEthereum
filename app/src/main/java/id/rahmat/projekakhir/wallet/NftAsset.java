package id.rahmat.projekakhir.wallet;

public class NftAsset {

    public final String collectionName;
    public final String tokenSymbol;
    public final String tokenId;
    public final String contractAddress;
    public final String networkName;
    public final String imageUrl;

    public NftAsset(String collectionName, String tokenSymbol, String tokenId,
                    String contractAddress, String networkName, String imageUrl) {
        this.collectionName = collectionName;
        this.tokenSymbol = tokenSymbol;
        this.tokenId = tokenId;
        this.contractAddress = contractAddress;
        this.networkName = networkName;
        this.imageUrl = imageUrl;
    }
}

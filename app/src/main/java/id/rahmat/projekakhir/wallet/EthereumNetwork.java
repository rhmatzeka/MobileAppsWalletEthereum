package id.rahmat.projekakhir.wallet;

import id.rahmat.projekakhir.BuildConfig;

public enum EthereumNetwork {
    MAINNET("mainnet", "Ethereum Mainnet", "https://mainnet.infura.io/v3/%s", 1L),
    SEPOLIA("sepolia", "Sepolia Testnet", "https://sepolia.infura.io/v3/%s", 11155111L);

    private final String key;
    private final String displayName;
    private final String infuraPattern;
    private final long chainId;

    EthereumNetwork(String key, String displayName, String infuraPattern, long chainId) {
        this.key = key;
        this.displayName = displayName;
        this.infuraPattern = infuraPattern;
        this.chainId = chainId;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getChainId() {
        return chainId;
    }

    public String getRpcUrl() {
        return String.format(infuraPattern, BuildConfig.INFURA_PROJECT_ID);
    }

    public static EthereumNetwork fromKey(String key) {
        for (EthereumNetwork network : values()) {
            if (network.key.equalsIgnoreCase(key)) {
                return network;
            }
        }
        return SEPOLIA;
    }
}

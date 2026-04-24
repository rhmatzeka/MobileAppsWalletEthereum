package id.rahmat.projekakhir.data.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import id.rahmat.projekakhir.BuildConfig;
import id.rahmat.projekakhir.utils.FormatUtils;
import id.rahmat.projekakhir.utils.AppPreferences;
import id.rahmat.projekakhir.wallet.TokenBalance;
import id.rahmat.projekakhir.wallet.EthereumNetwork;
import id.rahmat.projekakhir.wallet.EthereumNetworkRegistry;
import id.rahmat.projekakhir.wallet.EthereumService;
import id.rahmat.projekakhir.wallet.WalletManager;
import id.rahmat.projekakhir.wallet.WalletSnapshot;

public class WalletRepository {

    private final WalletManager walletManager;
    private final EthereumService ethereumService;
    private final PriceRepository priceRepository;
    private final AppPreferences appPreferences;

    public WalletRepository(WalletManager walletManager, EthereumService ethereumService,
                            PriceRepository priceRepository, AppPreferences appPreferences) {
        this.walletManager = walletManager;
        this.ethereumService = ethereumService;
        this.priceRepository = priceRepository;
        this.appPreferences = appPreferences;
    }

    public boolean hasWallet() {
        return walletManager.hasWallet();
    }

    public String getWalletAddress() {
        return walletManager.getWalletAddress();
    }

    public String getMnemonic() {
        return walletManager.getMnemonic();
    }

    public WalletManager getWalletManager() {
        return walletManager;
    }

    public EthereumNetwork getSelectedNetwork() {
        return EthereumNetworkRegistry.resolve(appPreferences.getSelectedNetwork(), appPreferences);
    }

    public void clearSession() {
        walletManager.clearWallet();
        appPreferences.clearSession();
    }

    public WalletSnapshot loadWalletSnapshot() throws Exception {
        if (!walletManager.hasWallet()) {
            EthereumNetwork network = getSelectedNetwork();
            return new WalletSnapshot(
                    "",
                    network.getDisplayName(),
                    network.getNativeAssetName(),
                    network.getNativeSymbol(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        }

        EthereumNetwork network = getSelectedNetwork();
        BigDecimal balance = ethereumService.getBalance(walletManager.getWalletAddress(), network);
        PriceRepository.PriceSnapshot priceSnapshot = priceRepository.getLatestPrice(network);
        return new WalletSnapshot(
                walletManager.getWalletAddress(),
                network.getDisplayName(),
                network.getNativeAssetName(),
                network.getNativeSymbol(),
                balance,
                priceSnapshot.usd,
                priceSnapshot.idr
        );
    }

    public List<TokenBalance> loadTokenBalances() throws Exception {
        if (!walletManager.hasWallet()) {
            return Collections.emptyList();
        }

        String walletAddress = walletManager.getWalletAddress();
        if (walletAddress == null || walletAddress.isEmpty()) {
            return Collections.emptyList();
        }

        List<TokenBalance> tokens = loadNativeAssetBalances(walletAddress);
        tokens.addAll(loadTrackedTokenBalances(walletAddress, getSelectedNetwork()));
        return tokens;
    }

    private List<TokenBalance> loadNativeAssetBalances(String walletAddress) {
        List<TokenBalance> balances = new ArrayList<>();
        List<EthereumNetwork> orderedNetworks = new ArrayList<>();
        EthereumNetwork selectedNetwork = getSelectedNetwork();
        orderedNetworks.add(selectedNetwork);
        for (EthereumNetwork network : EthereumNetworkRegistry.getAll(appPreferences)) {
            if (selectedNetwork.isSameNetwork(network)) {
                continue;
            }
            orderedNetworks.add(network);
        }

        for (EthereumNetwork network : orderedNetworks) {
            if (!network.hasRpcUrl()) {
                continue;
            }
            try {
                BigDecimal nativeBalance = ethereumService.getBalance(walletAddress, network);
                PriceRepository.PriceSnapshot priceSnapshot = priceRepository.getLatestPrice(network);
                balances.add(new TokenBalance(
                        network.getNativeAssetName(),
                        network.getDisplayName(),
                        network.getNativeSymbol(),
                        nativeBalance,
                        null,
                        priceSnapshot.idr,
                        priceSnapshot.usd,
                        true
                ));
            } catch (Exception ignored) {
                // Skip broken RPCs so one bad endpoint does not block the full home portfolio.
            }
        }
        return balances;
    }

    private List<TokenBalance> loadTrackedTokenBalances(String walletAddress, EthereumNetwork network) throws Exception {
        if (!EthereumNetwork.SEPOLIA.isSameNetwork(network)) {
            return Collections.emptyList();
        }
        List<TokenBalance> tokens = new ArrayList<>();
        PriceRepository.PriceSnapshot nativePrice = priceRepository.getLatestPrice(network);

        if (BuildConfig.MATS_TOKEN_ADDRESS != null && !BuildConfig.MATS_TOKEN_ADDRESS.isEmpty()) {
            int matsDecimals = 18;
            BigDecimal matsBalance = ethereumService.getErc20Balance(walletAddress, BuildConfig.MATS_TOKEN_ADDRESS, matsDecimals, network);
            BigDecimal matsUnitPriceEth = BigDecimal.ZERO;
            if (BuildConfig.MATS_SWAP_POOL_ADDRESS != null && !BuildConfig.MATS_SWAP_POOL_ADDRESS.isEmpty()) {
                matsUnitPriceEth = ethereumService.quoteMatsToEth(BigDecimal.ONE, network);
            }
            tokens.add(new TokenBalance(
                    "Mats Token",
                    network.getDisplayName(),
                    "MATS",
                    matsBalance,
                    "https://assets.coingecko.com/coins/images/279/small/ethereum.png",
                    FormatUtils.safeMultiply(matsUnitPriceEth, nativePrice.idr),
                    FormatUtils.safeMultiply(matsUnitPriceEth, nativePrice.usd),
                    false
            ));
        }

        if (BuildConfig.IDRX_TOKEN_ADDRESS != null && !BuildConfig.IDRX_TOKEN_ADDRESS.isEmpty()) {
            int idrxDecimals = ethereumService.getErc20Decimals(BuildConfig.IDRX_TOKEN_ADDRESS, network);
            String idrxSymbol = ethereumService.getErc20Symbol(BuildConfig.IDRX_TOKEN_ADDRESS, network);
            BigDecimal idrxBalance = ethereumService.getErc20Balance(walletAddress, BuildConfig.IDRX_TOKEN_ADDRESS, idrxDecimals, network);
            BigDecimal idrxUnitPriceIdr = "IDRX".equalsIgnoreCase(idrxSymbol) ? BigDecimal.ONE : BigDecimal.ZERO;
            tokens.add(new TokenBalance(
                    "IDRX Token",
                    network.getDisplayName(),
                    idrxSymbol == null || idrxSymbol.isEmpty() ? "IDRX" : idrxSymbol,
                    idrxBalance,
                    null,
                    idrxUnitPriceIdr,
                    BigDecimal.ZERO,
                    false
            ));
        }
        return tokens;
    }
}

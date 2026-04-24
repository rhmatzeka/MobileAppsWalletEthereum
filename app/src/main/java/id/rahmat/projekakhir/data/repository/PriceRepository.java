package id.rahmat.projekakhir.data.repository;

import com.github.mikephil.charting.data.CandleEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rahmat.projekakhir.data.remote.CoinGeckoApi;
import id.rahmat.projekakhir.wallet.EthereumNetwork;
import retrofit2.Response;

public class PriceRepository {

    public static class PriceSnapshot {
        public final BigDecimal usd;
        public final BigDecimal idr;

        public PriceSnapshot(BigDecimal usd, BigDecimal idr) {
            this.usd = usd;
            this.idr = idr;
        }
    }

    private final CoinGeckoApi coinGeckoApi;
    private final Map<String, PriceSnapshot> lastSnapshots = new HashMap<>();

    public PriceRepository(CoinGeckoApi coinGeckoApi) {
        this.coinGeckoApi = coinGeckoApi;
    }

    public PriceSnapshot getLatestPrice(EthereumNetwork network) throws IOException {
        String assetId = network.getCoinGeckoAssetId();
        if (assetId == null || assetId.trim().isEmpty()) {
            return new PriceSnapshot(BigDecimal.ZERO, BigDecimal.ZERO);
        }
        PriceSnapshot lastSnapshot = getLastSnapshot(assetId);
        Response<JsonObject> response = coinGeckoApi.getSimplePrice(assetId, "usd,idr").execute();
        if (response == null || !response.isSuccessful() || response.body() == null) {
            return lastSnapshot;
        }
        JsonObject asset = response.body().getAsJsonObject(assetId);
        if (asset == null || asset.get("usd") == null || asset.get("idr") == null) {
            return lastSnapshot;
        }
        PriceSnapshot snapshot = new PriceSnapshot(
                asset.get("usd").getAsBigDecimal(),
                asset.get("idr").getAsBigDecimal()
        );
        lastSnapshots.put(assetId, snapshot);
        return snapshot;
    }

    public List<CandleEntry> getSevenDayCandleEntries(EthereumNetwork network) throws IOException {
        if (network.getCoinGeckoAssetId() == null || network.getCoinGeckoAssetId().trim().isEmpty()) {
            return new ArrayList<>();
        }
        Response<JsonArray> response = coinGeckoApi.getOhlc(network.getCoinGeckoAssetId(), "usd", 7).execute();
        JsonArray candles = response.body();
        List<CandleEntry> entries = new ArrayList<>();
        if (candles == null) {
            return entries;
        }

        for (int index = 0; index < candles.size(); index++) {
            JsonArray point = candles.get(index).getAsJsonArray();
            if (point.size() < 5) {
                continue;
            }
            float open = point.get(1).getAsFloat();
            float high = point.get(2).getAsFloat();
            float low = point.get(3).getAsFloat();
            float close = point.get(4).getAsFloat();
            entries.add(new CandleEntry(index, high, low, open, close));
        }
        return entries;
    }

    private PriceSnapshot getLastSnapshot(String assetId) {
        PriceSnapshot snapshot = lastSnapshots.get(assetId);
        if (snapshot != null) {
            return snapshot;
        }
        return new PriceSnapshot(BigDecimal.ZERO, BigDecimal.ZERO);
    }
}

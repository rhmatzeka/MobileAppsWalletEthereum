package id.rahmat.projekakhir.data.repository;

import com.github.mikephil.charting.data.CandleEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import id.rahmat.projekakhir.data.remote.CoinGeckoApi;
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
    private PriceSnapshot lastSnapshot = new PriceSnapshot(BigDecimal.ZERO, BigDecimal.ZERO);

    public PriceRepository(CoinGeckoApi coinGeckoApi) {
        this.coinGeckoApi = coinGeckoApi;
    }

    public PriceSnapshot getLatestEthPrice() throws IOException {
        Response<JsonObject> response = coinGeckoApi.getSimplePrice("ethereum", "usd,idr").execute();
        if (response == null || !response.isSuccessful() || response.body() == null) {
            return lastSnapshot;
        }
        JsonObject ethereum = response.body().getAsJsonObject("ethereum");
        if (ethereum == null || ethereum.get("usd") == null || ethereum.get("idr") == null) {
            return lastSnapshot;
        }
        lastSnapshot = new PriceSnapshot(
                ethereum.get("usd").getAsBigDecimal(),
                ethereum.get("idr").getAsBigDecimal()
        );
        return lastSnapshot;
    }

    public List<CandleEntry> getSevenDayCandleEntries() throws IOException {
        Response<JsonArray> response = coinGeckoApi.getOhlc("usd", 7).execute();
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
}

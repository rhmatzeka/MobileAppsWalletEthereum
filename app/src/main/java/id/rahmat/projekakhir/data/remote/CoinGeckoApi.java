package id.rahmat.projekakhir.data.remote;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CoinGeckoApi {

    @GET("simple/price")
    Call<JsonObject> getSimplePrice(@Query("ids") String ids, @Query("vs_currencies") String vsCurrencies);

    @GET("coins/ethereum/market_chart")
    Call<JsonObject> getMarketChart(@Query("vs_currency") String vsCurrency,
                                    @Query("days") int days,
                                    @Query("interval") String interval);

    @GET("coins/ethereum/ohlc")
    Call<JsonArray> getOhlc(@Query("vs_currency") String vsCurrency,
                            @Query("days") int days);
}

package id.rahmat.projekakhir.data.remote;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CoinGeckoApi {

    @GET("simple/price")
    Call<JsonObject> getSimplePrice(@Query("ids") String ids, @Query("vs_currencies") String vsCurrencies);

    @GET("coins/{id}/market_chart")
    Call<JsonObject> getMarketChart(@Path("id") String id,
                                    @Query("vs_currency") String vsCurrency,
                                    @Query("days") int days,
                                    @Query("interval") String interval);

    @GET("coins/{id}/ohlc")
    Call<JsonArray> getOhlc(@Path("id") String id,
                            @Query("vs_currency") String vsCurrency,
                            @Query("days") int days);
}

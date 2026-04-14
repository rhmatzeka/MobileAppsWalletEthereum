package id.rahmat.projekakhir.data.remote;

import id.rahmat.projekakhir.data.remote.model.EtherscanTransactionsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EtherscanApi {

    @GET(".")
    Call<EtherscanTransactionsResponse> getTransactions(@Query("module") String module,
                                                        @Query("action") String action,
                                                        @Query("address") String address,
                                                        @Query("sort") String sort,
                                                        @Query("apikey") String apiKey);
}

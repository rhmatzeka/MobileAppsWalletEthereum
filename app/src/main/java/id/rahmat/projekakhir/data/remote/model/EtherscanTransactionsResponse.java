package id.rahmat.projekakhir.data.remote.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EtherscanTransactionsResponse {

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public List<EtherscanTransactionItem> result;
}

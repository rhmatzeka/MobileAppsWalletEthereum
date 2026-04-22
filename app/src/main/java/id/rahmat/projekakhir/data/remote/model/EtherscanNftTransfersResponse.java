package id.rahmat.projekakhir.data.remote.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class EtherscanNftTransfersResponse {

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public JsonElement result;
}

package id.rahmat.projekakhir.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class EtherscanTransactionItem {

    @SerializedName("hash")
    public String hash;

    @SerializedName("from")
    public String from;

    @SerializedName("to")
    public String to;

    @SerializedName("value")
    public String value;

    @SerializedName("gasPrice")
    public String gasPrice;

    @SerializedName("gasUsed")
    public String gasUsed;

    @SerializedName("timeStamp")
    public String timeStamp;

    @SerializedName("blockNumber")
    public String blockNumber;

    @SerializedName("isError")
    public String isError;

    @SerializedName("txreceipt_status")
    public String receiptStatus;
}

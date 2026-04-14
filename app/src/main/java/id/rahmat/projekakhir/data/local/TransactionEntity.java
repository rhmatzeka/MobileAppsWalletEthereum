package id.rahmat.projekakhir.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class TransactionEntity {

    @PrimaryKey
    @NonNull
    public String hash;

    public String walletAddress;
    public String fromAddress;
    public String toAddress;
    public String amountEth;
    public String gasFeeEth;
    public long timestamp;
    public String status;
    public String direction;
    public String network;
    public String source;
}

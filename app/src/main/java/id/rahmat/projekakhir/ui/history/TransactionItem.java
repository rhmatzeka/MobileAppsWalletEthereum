package id.rahmat.projekakhir.ui.history;

public class TransactionItem {

    private final String hash;
    private final String title;
    private final String time;
    private final String amount;
    private final String gasFee;
    private final String status;
    private final String networkKey;
    private final boolean outgoing;

    public TransactionItem(String hash, String title, String time, String amount,
                           String gasFee, String status, String networkKey, boolean outgoing) {
        this.hash = hash;
        this.title = title;
        this.time = time;
        this.amount = amount;
        this.gasFee = gasFee;
        this.status = status;
        this.networkKey = networkKey;
        this.outgoing = outgoing;
    }

    public String getHash() {
        return hash;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getAmount() {
        return amount;
    }

    public String getGasFee() {
        return gasFee;
    }

    public String getStatus() {
        return status;
    }

    public String getNetworkKey() {
        return networkKey;
    }

    public boolean isOutgoing() {
        return outgoing;
    }
}

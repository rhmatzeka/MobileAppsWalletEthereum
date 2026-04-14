package id.rahmat.projekakhir.ui.history;

public class TransactionItem {

    private final String title;
    private final String time;
    private final String amount;
    private final String status;
    private final boolean outgoing;

    public TransactionItem(String title, String time, String amount, String status, boolean outgoing) {
        this.title = title;
        this.time = time;
        this.amount = amount;
        this.status = status;
        this.outgoing = outgoing;
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

    public String getStatus() {
        return status;
    }

    public boolean isOutgoing() {
        return outgoing;
    }
}

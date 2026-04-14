package id.rahmat.projekakhir.ui.home;

public class TokenItem {

    private final String name;
    private final String subtitle;
    private final String balance;
    private final String fiatValue;
    private final String imageUrl;
    private final int imageResId;

    public TokenItem(String name, String subtitle, String balance, String fiatValue, String imageUrl, int imageResId) {
        this.name = name;
        this.subtitle = subtitle;
        this.balance = balance;
        this.fiatValue = fiatValue;
        this.imageUrl = imageUrl;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getBalance() {
        return balance;
    }

    public String getFiatValue() {
        return fiatValue;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getImageResId() {
        return imageResId;
    }
}

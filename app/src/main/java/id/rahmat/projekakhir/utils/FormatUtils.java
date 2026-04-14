package id.rahmat.projekakhir.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class FormatUtils {

    private static final DecimalFormat ETH_FORMAT = new DecimalFormat("#,##0.0000", DecimalFormatSymbols.getInstance(Locale.US));
    private static final DecimalFormat FIAT_FORMAT = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
    private static final DecimalFormat TOKEN_FORMAT = new DecimalFormat("#,##0.0000", DecimalFormatSymbols.getInstance(Locale.US));

    private FormatUtils() {
    }

    public static String formatEth(BigDecimal value) {
        return ETH_FORMAT.format(value == null ? BigDecimal.ZERO : value) + " ETH";
    }

    public static String formatToken(BigDecimal value, String symbol) {
        String safeSymbol = symbol == null ? "" : symbol;
        return TOKEN_FORMAT.format(value == null ? BigDecimal.ZERO : value) + " " + safeSymbol;
    }

    public static String formatUsd(BigDecimal value) {
        return "$" + FIAT_FORMAT.format(value == null ? BigDecimal.ZERO : value);
    }

    public static String formatIdr(BigDecimal value) {
        return "Rp " + FIAT_FORMAT.format(value == null ? BigDecimal.ZERO : value);
    }

    public static BigDecimal safeMultiply(BigDecimal left, BigDecimal right) {
        if (left == null || right == null) {
            return BigDecimal.ZERO;
        }
        return left.multiply(right);
    }
}

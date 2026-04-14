package id.rahmat.projekakhir.utils;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public final class WindowInsetsHelper {

    private WindowInsetsHelper() {
    }

    public static void applySystemBarPadding(View view, boolean top, boolean bottom) {
        final int startPadding = view.getPaddingStart();
        final int topPadding = view.getPaddingTop();
        final int endPadding = view.getPaddingEnd();
        final int bottomPadding = view.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(view, (target, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int appliedTop = top ? topPadding + systemBars.top : topPadding;
            int appliedBottom = bottom ? bottomPadding + systemBars.bottom : bottomPadding;
            target.setPaddingRelative(startPadding, appliedTop, endPadding, appliedBottom);
            return insets;
        });
    }
}

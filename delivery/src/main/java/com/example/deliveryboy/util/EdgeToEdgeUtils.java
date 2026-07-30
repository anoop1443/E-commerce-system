package com.example.deliveryboy.util;

import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EdgeToEdgeUtils {

    /**
     * Applies system bar insets as padding to the given view, adding to existing padding.
     */
    public static void applyInsets(View view) {
        if (view == null) return;
        int initialLeft = view.getPaddingLeft();
        int initialTop = view.getPaddingTop();
        int initialRight = view.getPaddingRight();
        int initialBottom = view.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                initialLeft + systemBars.left,
                initialTop + systemBars.top,
                initialRight + systemBars.right,
                initialBottom + systemBars.bottom
            );
            return insets;
        });
    }

    /**
     * Applies ONLY the top inset (status bar) as padding to the given view.
     */
    public static void applyTopInset(View view) {
        if (view == null) return;
        int initialTop = view.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), initialTop + systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
    }

    /**
     * Applies ONLY the bottom inset (navigation bar/gestures) as padding to the given view.
     */
    public static void applyBottomInset(View view) {
        if (view == null) return;
        int initialBottom = view.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), initialBottom + navBars.bottom);
            return insets;
        });
    }
}

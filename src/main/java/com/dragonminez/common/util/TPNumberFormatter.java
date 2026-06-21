package com.dragonminez.common.util;

import java.text.NumberFormat;
import java.util.Locale;

public class TPNumberFormatter {

    private static final NumberFormat FORMATTER = NumberFormat.getInstance(Locale.US);
    private static final long BILLION = 1_000_000_000L;
    private static final long TRILLION = 1_000_000_000_000L;
    private static final long QUADRILLION = 1_000_000_000_000_000L;

    public static String format(long value) {
        if (value < BILLION) {
            return FORMATTER.format(value);
        }

        double d = value;
        String suffix;

        if (value >= QUADRILLION) {
            d = (double) value / QUADRILLION;
            suffix = "Q";
        } else if (value >= TRILLION) {
            d = (double) value / TRILLION;
            suffix = "T";
        } else {
            d = (double) value / BILLION;
            suffix = "B";
        }

        if (d >= 100) {
            return String.format(Locale.US, "%.0f%s", d, suffix);
        } else if (d >= 10) {
            return String.format(Locale.US, "%.1f%s", d, suffix);
        } else {
            return String.format(Locale.US, "%.2f%s", d, suffix);
        }
    }
}

package shop.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringUtils {

    public static String lineSeparator() {
        return lineSeparator(80);
    }

    public static String lineSeparator(int length, String symbol) {
        return symbol.repeat(Math.max(0, length));
    }

    public static String lineSeparator(int length) {
        return lineSeparator(length, "_");
    }

    public static String lineSeparator(String symbol) {
        return lineSeparator(80, symbol);
    }

    public static String formatDate(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'"));
    }

    public static String tabulator(int length) {
        return "\t".repeat(Math.max(0, length));
    }

}

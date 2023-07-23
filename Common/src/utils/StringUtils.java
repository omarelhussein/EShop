package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Die StringUtils-Klasse ist eine Hilfsklasse, die eine Reihe von Methoden zur Verfügung stellt, um Operationen auf
 * Strings durchzuführen. Diese Methoden umfassen das Erstellen von Trennzeilen, die Formatierung von Datumsangaben
 * und die Erzeugung von Tabulatoren.
 */
public class StringUtils {

    /**
     * Erstellt eine Trennzeile mit der Standardlänge 80, bestehend aus dem Zeichen "_".
     *
     * @return Eine Trennzeile der Länge 80.
     */
    public static String lineSeparator() {
        return lineSeparator(80);
    }

    /**
     * Erstellt eine Trennzeile mit der gegebenen Länge und dem gegebenen Zeichen.
     *
     * @param length Die Länge der Trennzeile.
     * @param symbol Das Zeichen, das für die Trennzeile verwendet wird.
     * @return Eine Trennzeile der gegebenen Länge.
     */
    public static String lineSeparator(int length, String symbol) {
        // string.repeat(int) ist eine neue Methode, die in Java 11 eingeführt wurde. Sie wiederholt den String x-mal.
        return symbol.repeat(Math.max(0, length));
    }

    /**
     * Erstellt eine Trennzeile mit der gegebenen Länge, bestehend aus dem Zeichen "_".
     *
     * @param length Die Länge der Trennzeile.
     * @return Eine Trennzeile der gegebenen Länge.
     */
    public static String lineSeparator(int length) {
        return lineSeparator(length, "_");
    }

    /**
     * Erstellt eine Trennzeile mit der Standardlänge 80 und dem gegebenen Zeichen.
     *
     * @param symbol Das Zeichen, das für die Trennzeile verwendet wird.
     * @return Eine Trennzeile der Länge 80.
     */
    public static String lineSeparator(String symbol) {
        return lineSeparator(80, symbol);
    }

    /**
     * Formatiert ein LocalDateTime-Objekt zu einem String im Format "dd.MM.yyyy 'um' HH:mm 'Uhr'".
     *
     * @param localDateTime Das LocalDateTime-Objekt, das formatiert werden soll.
     * @return Ein String, der das formatierte Datum und die Uhrzeit repräsentiert.
     */
    public static String formatDate(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'"));
    }

    /**
     * Erzeugt eine Zeichenkette von Tabulatoren der angegebenen Länge.
     *
     * @param length Die Anzahl der Tabulatoren, die erstellt werden sollen.
     * @return Eine Zeichenkette von Tabulatoren der angegebenen Länge.
     */
    public static String tabulator(int length) {
        // string.repeat(int) ist eine neue Methode, die in Java 11 eingeführt wurde. Sie wiederholt den String x-mal.
        return "\t".repeat(Math.max(0, length));
    }

}

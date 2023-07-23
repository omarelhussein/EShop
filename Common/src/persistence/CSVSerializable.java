package persistence;

/**
 * Das CSVSerializable Interface definiert Verhaltensweisen, die Klassen implementieren sollten,
 * um eine CSV-Serialisierung und Deserialisierung zu ermöglichen.
 * <p>
 * Klassen, die dieses Interface implementieren, können ihre Daten als CSV-Zeichenketten darstellen
 * und auch aus solchen Zeichenketten wiederhergestellt werden.
 */
public interface CSVSerializable {

    /**
     * Konvertiert das Objekt in eine CSV-Zeichenkette.
     *
     * @return eine CSV-Zeichenkette, die die Daten des Objekts darstellt
     */
    String toCSVString();

    /**
     * Stellt das Objekt aus einer CSV-Zeichenkette wieder her.
     *
     * @param csv die CSV-Zeichenkette, aus der das Objekt wiederhergestellt werden soll
     */
    void fromCSVString(String csv);

}

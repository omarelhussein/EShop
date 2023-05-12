package shop.domain.exceptions.warenkorb;

public class BestandUeberschrittenException extends Exception {
    public BestandUeberschrittenException() {
        super("Bestand überschritten!");
    }
}

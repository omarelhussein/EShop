package exceptions.warenkorb;

public class RechnungNichtGefundenException extends Exception {

    public RechnungNichtGefundenException(int rechnungsNr) {
        super("Rechnung mit der Rechnungsnummer " + rechnungsNr + " konnte nicht gefunden werden.");
    }

}

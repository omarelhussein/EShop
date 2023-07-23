package exceptions.warenkorb;

public class WarenkorbArtikelNichtGefundenException extends Exception {

    public WarenkorbArtikelNichtGefundenException(int artikelNr) {
        super("Artikel mit der Artikelnummer " + artikelNr + " konnte im Warenkorb nicht gefunden werden.");
    }

}

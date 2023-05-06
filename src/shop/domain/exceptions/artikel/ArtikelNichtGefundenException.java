package shop.domain.exceptions.artikel;

public class ArtikelNichtGefundenException extends Exception{
    public ArtikelNichtGefundenException(int artNr) {
        super("Artikel mit Nummer " + artNr + " konnte nicht gefunden werden.");
    }
}

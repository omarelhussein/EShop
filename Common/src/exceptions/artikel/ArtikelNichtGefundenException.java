package exceptions.artikel;

public class ArtikelNichtGefundenException extends Exception{
    /**
     * wird gethrowed wenn kein Artikel mit der angegebenen Artikelnummer in der Artikelliste gefunden wurde
     * @param artNr
     */
    public ArtikelNichtGefundenException(int artNr) {
        super("Artikel mit Nummer " + artNr + " konnte nicht gefunden werden.");
    }
}

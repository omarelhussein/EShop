package exceptions.artikel;

import entities.Massenartikel;

public class AnzahlPackgroesseException extends Exception {

    /**
     * wird gethrowed wenn sich die angegebene Anzahl nicht durch die Packgröße des angebenen Artikels teilen lässt
     * @param anzahl
     * @param artikel
     */
    public AnzahlPackgroesseException(int anzahl, Massenartikel artikel) {
        super("Die angegebene Anzahl " + anzahl + " bezüglich des Massenartikels \"" + artikel.getBezeichnung() +
              "\" lässt sich nicht durch dessen Packgröße \"" + artikel.getPackgroesse() + " \" teilen.");
    }

}

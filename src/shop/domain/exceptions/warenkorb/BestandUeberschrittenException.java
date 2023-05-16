package shop.domain.exceptions.warenkorb;

public class BestandUeberschrittenException extends Exception {
    public BestandUeberschrittenException(int bestand, int anzahl) {
        super("Bestand von " + bestand + " ist nicht ausreichend für " + anzahl + " Artikel, um den Warenkorb zu befüllen." +
                " Bitte reduzieren Sie die Anzahl oder warten Sie bis der Bestand wieder aufgefüllt wurde.");
    }
}

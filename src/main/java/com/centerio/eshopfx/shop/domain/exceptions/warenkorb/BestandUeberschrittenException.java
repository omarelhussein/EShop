package com.centerio.eshopfx.shop.domain.exceptions.warenkorb;

import shop.entities.Artikel;

public class BestandUeberschrittenException extends Exception {

    private final int bestand;
    private final int anzahl;
    private final Artikel artikel;

    public BestandUeberschrittenException(int bestand, int anzahl, Artikel artikel) {
        this.bestand = bestand;
        this.anzahl = anzahl;
        this.artikel = artikel;
    }

    public int getBestand() {
        return bestand;
    }

    public int getAnzahl() {
        return anzahl;
    }

    public Artikel getArtikel() {
        return artikel;
    }

    @Override
    public String getMessage() {
        return "Leider ist der Bestand vom Artikel \"" + getArtikel().getBezeichnung()
               + "\" nicht mehr ausreichend! Der maximal verfügbare Bestand ist "
               + getArtikel().getBestand() + "!\n" +
               "Bitte entfernen Sie den Artikel aus dem Warenkorb oder verringern Sie die Anzahl!";
    }
}

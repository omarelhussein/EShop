package com.centerio.eshopfx.shop.entities;

public class Massenartikel extends Artikel {

    private int packgroesse;

    public Massenartikel(int artNr, String bezeichnung, double preis, int bestand, int pgroesse) {
        super(artNr, bezeichnung, preis, bestand * pgroesse);
        packgroesse = pgroesse;
    }

    @Override
    public String toString() {
        return "Artikel: " + getArtNr() + " / Bezeichnung: " + getBezeichnung() +
               " / Preis: " + getPreis() + " €" + " / Bestand: " + getBestand() + " stk." +
               " / Packgröße: " + packgroesse + " stk." + " / Packpreis: "
               // Diese Zeile rundet das Ergebnis auf zwei Dezimalstellen.
               // 'Math.round(... * 100.0) / 100.0' ist ein gängiges Muster zum Runden auf zwei Dezimalstellen
               // die .0 ist wichtig, da sonst die Division als Integer-Division interpretiert wird.
               + (Math.round((packgroesse * getPreis()) * 100.) / 100.) + " €";
    }

    public void setPackgroesse(int pgroesse) {
        this.packgroesse = pgroesse;
    }

    public int getPackgroesse() {
        return this.packgroesse;
    }
}

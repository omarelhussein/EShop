package com.centerio.eshopfx.shop.entities;

import java.io.Serializable;

public class WarenkorbArtikel implements Serializable {

    private Artikel artikel;

    private int anzahl;

    private String artikelbezeichnung;

    public WarenkorbArtikel(Artikel artikel, int anzahl) {
        this.artikel = artikel;
        this.anzahl = anzahl;
    }

    public String getArtikelbezeichnung(){ return artikel.getBezeichnung();}
    public Artikel getArtikel() {
        return artikel;
    }

    public Double getEinzelpreis(){
        return artikel.getPreis();
    }

    public int getAnzahl() {
        return anzahl;
    }

    public void setAnzahl(int anzahl) {
        this.anzahl = anzahl;
    }

    public void erhoeheAnzahl() {
        this.anzahl++;
    }

    public void verringereAnzahl() {
        this.anzahl--;
    }

    public void setArtikel(Artikel artikel) {
        this.artikel = artikel;
    }

    public double getPreis() {
        var gesamtPreis = this.anzahl * this.artikel.getPreis();
        return Math.round(gesamtPreis * 100.) / 100.;
    }

    public double getGesamtPreis() {
        var gesamtPreis = this.anzahl * this.artikel.getPreis();
        // Diese Zeile rundet das Ergebnis auf zwei Dezimalstellen.
        // 'Math.round(... * 100.0) / 100.0' ist ein gängiges Muster zum Runden auf zwei Dezimalstellen
        // die .0 ist wichtig, da sonst die Division als Integer-Division interpretiert wird.
        return Math.round(gesamtPreis * 100.) / 100.;
    }

    @Override
    public String toString() {
        return "ArtikelNr: " + this.artikel.getArtNr() + " / Bezeichnung: " + this.artikel.getBezeichnung()
                + " / Anzahl: " + this.anzahl + " / Stückpreis: " + this.artikel.getPreis() + "€ / Gesamtpreis: "
                + getGesamtPreis() + "€";
    }
}

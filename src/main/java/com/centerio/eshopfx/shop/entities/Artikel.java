package com.centerio.eshopfx.shop.entities;

import shop.persistence.CSVSerializable;

import java.io.Serializable;

public class Artikel implements Serializable, CSVSerializable {

    private double preis;
    private int artNr;
    private String bezeichnung;
    private int bestand;

    public Artikel(int artNr, String bezeichnung, double preis, int bestand) {
        this.artNr = artNr;
        this.bezeichnung = bezeichnung;
        this.bestand = bestand;
        this.preis = preis;
    }

    public Artikel() {
    }

    public double getPreis() {
        return Math.round(preis * 100.) / 100.;
    }

    public int getArtNr() {
        return artNr;
    }

    public int getBestand() {
        return bestand;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public void setPreis(double preis) {
        this.preis = preis;
    }

    public void setBestand(int bestand) {
        this.bestand = bestand;
    }


    @Override
    public String toString() {
        return "Artikel: " + artNr + " / Bezeichnung: " + this.bezeichnung +
               " / Preis: " + this.preis + " €" + " / Bestand: " + this.bestand + " stk.";
    }

    /**
     * Artikel vergleichen
     *
     * @param obj das Objekt zum Vergleichen. Hier werden nur die Artikelnummern verglichen.
     * @return true, wenn die Artikelnummern gleich sind, sonst false.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Artikel artikel) {
            return this.artNr == artikel.artNr;
        }
        return false;
    }

    @Override
    public String toCSVString() {
        return artNr + ";" + bezeichnung + ";" + preis + ";" + bestand;
    }

    @Override
    public void fromCSVString(String csv) {
        String[] tokens = csv.split("#");
        String[] artikelTokens = tokens[0].split(";");
        artNr = Integer.parseInt(artikelTokens[0]);
        bezeichnung = artikelTokens[1];
        preis = Double.parseDouble(artikelTokens[2]);
        bestand = Integer.parseInt(artikelTokens[3]);
    }
}


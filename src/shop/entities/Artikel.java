package shop.entities;

import java.util.ArrayList;
import java.util.List;

public class Artikel {

    private double preis;
    private final int artNr;
    private String bezeichnung;
    private int bestand;
    private List<BestandshistorieItem> bestandshistorie;

    public Artikel(int artNr, String bezeichnung, double preis, int bestand) {
        this.artNr = artNr;
        this.bezeichnung = bezeichnung;
        this.bestand = bestand;
        this.preis = preis;
        bestandshistorie = new ArrayList<>();
        bestandshistorie.add(new BestandshistorieItem(bestand, false));
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

    public List<BestandshistorieItem> getBestandshistorie() {
        return bestandshistorie;
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
        if (obj instanceof Artikel) {
            return this.artNr == ((Artikel) obj).artNr;
        }
        return false;
    }

}

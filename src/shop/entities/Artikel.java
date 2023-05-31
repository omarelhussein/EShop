package shop.entities;

import shop.persistence.CSVSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Artikel implements Serializable, CSVSerializable {

    private double preis;
    private int artNr;
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
        if (obj instanceof Artikel artikel) {
            return this.artNr == artikel.artNr;
        }
        return false;
    }

    @Override
    public String toCSVString() {
        StringBuilder sb = new StringBuilder();
        for (BestandshistorieItem item : bestandshistorie) {
            sb.append(item.toCSVString()).append("|");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1); // letztes | entfernen
        }
        return artNr + ";" + bezeichnung + ";" + preis + ";" + bestand + ";" + sb;
    }

    @Override
    public void fromCSVString(String csv) {
        String[] tokens = csv.split(";");
        artNr = Integer.parseInt(tokens[0]);
        bezeichnung = tokens[1];
        preis = Double.parseDouble(tokens[2]);
        bestand = Integer.parseInt(tokens[3]);
        bestandshistorie = new ArrayList<>();
        String[] items = tokens[4].split("\\|");
        for (String item : items) {
            BestandshistorieItem bhi = new BestandshistorieItem();
            bhi.fromCSVString(item);
            bestandshistorie.add(bhi);
        }
    }
}

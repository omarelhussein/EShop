package shop.entities;

public class Artikel {

    private double preis;
    private final int artNr;
    private String bezeichnung;
    private int bestand;

    public Artikel(int artNr, String bezeichnung, double preis, int bestand) {      //Konstruktor
        this.artNr = artNr;
        this.bezeichnung = bezeichnung;
        this.bestand = bestand;
        this.preis = preis;
    }

    public double getPreis() {                                     //Ein- & Ausgabe
        return preis;
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

    /**
     * Artikeldaten als String ausgeben
     *
     * @return
     */
    @Override
    public String toString() {
        return "Artikel: " + artNr + "\n" + "Bezeichnung: " + this.bezeichnung + "\n" +
                "Preis: " + this.preis + "€" + "\n" + "Bestand" + this.bestand + "stk";
    }

    /**
     * Artikel vergleichen
     *
     * @param obj das Objekt zum Vergleichen. Hier werden nur die Artikelnummern verglichen.
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Artikel) {
            return this.artNr == ((Artikel) obj).artNr;
        }
        return false;
    }

}

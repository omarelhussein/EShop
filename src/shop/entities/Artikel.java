package shop.entities;

public class Artikel {

    private double preis;
    private final int artNr;
    private String bezeichnung;
    private int bestand;
    private BestandsHistorie bestandsHistorie;

    public Artikel(int artNr, String bezeichnung, double preis, int bestand) {
        this.artNr = artNr;
        this.bezeichnung = bezeichnung;
        this.bestand = bestand;
        this.preis = preis;
        bestandsHistorie = new BestandsHistorie(this);
    }

    public double getPreis() {                                     //Ein- & Ausgabe
        return preis;
    }

    public int getArtNr() {
        return artNr;
    }

    public BestandsHistorie getBestandsHistorie(){
        return this.bestandsHistorie;
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
                " / Preis: " + this.preis + " €" + " / Bestand: " + this.bestand +  " stk.";
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

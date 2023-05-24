package shop.entities;

public class Massenartikel extends Artikel{

    private int packgroesse;

    public Massenartikel(int artNr, String bezeichnung, double preis, int bestand, int pgroesse) {
        super(artNr, bezeichnung, preis, bestand);
        packgroesse = pgroesse;
    }

    @Override
    public String toString() {
        return "Artikel: " + getArtNr() + " / Bezeichnung: " + getBezeichnung() +
                " / Preis: " + getPreis() + " €" + " / Bestand: " + getBestand() +  " stk." +
                " / Packgröße: " + packgroesse + " stk." + " / Packpreis: " + packgroesse*getPreis() + " €";
    }

    public void setPackgroesse(int pgroesse) {
        this.packgroesse = pgroesse;
    }

    public int getPackgroesse() {
        return this.packgroesse;
    }
}

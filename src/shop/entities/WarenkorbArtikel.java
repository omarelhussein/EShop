package shop.entities;

public class WarenkorbArtikel {

    private Artikel artikel;
    private int anzahl;

    public WarenkorbArtikel(Artikel artikel, int anzahl) {
        this.artikel = artikel;
        this.anzahl = anzahl;
    }

    public Artikel getArtikel() {
        return artikel;
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

    public double getGesamtPreis() {
        var gesamtPreis = this.anzahl * this.artikel.getPreis();
        if (this.artikel instanceof Massenartikel massenartikel) {
            gesamtPreis *= massenartikel.getPackgroesse();
        }
        return Math.round(gesamtPreis * 100.) / 100.;
    }

    @Override
    public String toString() {
        return "ArtikelNr: " + this.artikel.getArtNr() + " / Bezeichnung: " + this.artikel.getBezeichnung()
                + " / Anzahl: " + this.anzahl + " / Stückpreis: " + this.artikel.getPreis() + "€ / Gesamtpreis: "
                + getGesamtPreis() + "€";
    }
}

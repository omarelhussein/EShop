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
}

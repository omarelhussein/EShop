package shop.entities;

import java.util.ArrayList;
import java.util.List;

public class Warenkorb {

    private final Kunde kunde;
    private final List<WarenkorbArtikel> warenkorbArtikelList;

    public Warenkorb(Kunde kunde) {
        this.kunde = kunde;
        this.warenkorbArtikelList = new ArrayList<>();
    }

    public void addArtikel(WarenkorbArtikel artikel) {
        this.warenkorbArtikelList.add(artikel);
    }

    public boolean removeArtikel(WarenkorbArtikel artikel) {
        if (artikel != null) {
            return this.warenkorbArtikelList.remove(artikel);
        }
        return false;
    }

    public double getGesamtSumme() {
        double preis = 0;
        for (WarenkorbArtikel value : this.warenkorbArtikelList) {
            preis += value.getArtikel().getPreis() * value.getAnzahl();
        }
        return Math.round(preis * 100.) / 100.;
    }

    public List<Artikel> getArtikelList() {
        List<Artikel> artikel = new ArrayList<>();
        for (WarenkorbArtikel value : this.warenkorbArtikelList) {
            if (value.getArtikel() != null) {
                artikel.add(value.getArtikel());
            }
        }
        return artikel;
    }

    public List<WarenkorbArtikel> getWarenkorbArtikelList() {
        return this.warenkorbArtikelList;
    }

    public int getAnzahlArtikel() {
        int anzahl = 0;
        for (WarenkorbArtikel value : this.warenkorbArtikelList) {
            anzahl += value.getAnzahl();
        }
        return anzahl;
    }

    public Kunde getKunde() {
        return this.kunde;
    }

    public boolean istLeer() {
        return this.warenkorbArtikelList.isEmpty();
    }

}

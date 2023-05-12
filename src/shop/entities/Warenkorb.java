package shop.entities;

import java.util.ArrayList;
import java.util.List;

public class Warenkorb {

    private final Kunde kunde;
    private List<WarenkorbArtikel> artikelList;

    public Warenkorb(Kunde kunde) {
        this.kunde = kunde;
    }

    public void addArtikel(WarenkorbArtikel artikel) {
        this.artikelList.add(artikel);
    }

    public boolean removeArtikel(WarenkorbArtikel artikel) {
        if (artikel != null) {
            return this.artikelList.remove(artikel);
        }
        return false;
    }

    public int getPreisIngesamt() {
        int preis = 0;
        for (WarenkorbArtikel value : this.artikelList) {
            preis += value.getArtikel().getPreis() * value.getAnzahl();
        }
        return preis;
    }

    public List<Artikel> getArtikelList() {
        List<Artikel> artikel = new ArrayList<>();
        for (WarenkorbArtikel value : this.artikelList) {
            if (value.getArtikel() != null) {
                artikel.add(value.getArtikel());
            }
        }
        return artikel;
    }

    public List<WarenkorbArtikel> getWarenkorbArtikelList() {
        return this.artikelList;
    }

    public Kunde getKunde() {
        return this.kunde;
    }
}

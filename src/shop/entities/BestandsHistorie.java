package shop.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BestandsHistorie {
    private Artikel artikel;
    private List<Integer> bestandsHistorieListe;
    private List<LocalDateTime> datum;

    public BestandsHistorie(Artikel artikel) {
        this.artikel = artikel;
        this.bestandsHistorieListe = new ArrayList<>();
        this.datum = new ArrayList<>();
    }

    public void setBestandsHistorie(List<Integer> historie) {
        this.bestandsHistorieListe = historie;
    }

    public List<Integer> getBestandsHistorieListe() {
        return this.bestandsHistorieListe;
    }

    public void setArtikel(Artikel artikel) {
        this.artikel = artikel;
    }

    public Artikel getArtikel() {
        return this.artikel;
    }

    public void setDatum(List<LocalDateTime> datum) {
        this.datum = datum;
    }

    public List<LocalDateTime> getDatum() {
        return this.datum;
    }

    public void stringMachen() {
        for (int i = 0; i < getBestandsHistorieListe().size(); i++) {
            System.out.println("Artikelbestand von Artikel: /ArtNr: " + getArtikel().getArtNr() + " / " + getArtikel().getBezeichnung() +
                    " / zu " + getBestandsHistorieListe().get(i).toString() + " Stk.        am " + getDatum().get(i) + " geändert");
        }
    }

}



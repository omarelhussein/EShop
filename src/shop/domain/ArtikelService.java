package shop.domain;

import shop.domain.exceptions.artikel.ArtikelBereitsVorhandenException;
import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.entities.Artikel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ArtikelService {

    List<Artikel> artikelList = new ArrayList<>();

    /**
     * Artikel der Liste hinzufügen
     */
    public void addArtikel(Artikel artikel) throws ArtikelBereitsVorhandenException {
        if (getArtikelByArtNr(artikel.getArtNr()) == null) {
            throw new ArtikelBereitsVorhandenException(
                    "Artikel konnte nicht hinzugefügt werden, da einer mit der selben ID bereits existiert"
            );
        }
        artikelList.add(artikel);
    }

    /**
     * Artikel löschen
     */
    public void removeArtikel(Artikel artikel) throws ArtikelNichtGefundenException {
        if (getArtikelByArtNr(artikel.getArtNr()) == null) {
            throw new ArtikelNichtGefundenException("Artikel zum löschen wurde nicht gefunden");
        }
        artikelList.remove(artikel);
    }

    /**
     * Sucht einen Artikel nach Namen
     *
     * @param queryString Die Bezeichnung wonach Artikeln gesucht werden sollen
     */
    public List<Artikel> sucheArtikelByName(String queryString) {
        return artikelList.stream()
                .filter(it -> it.getBezeichnung().contains(queryString))
                .collect(Collectors.toList());
    }

    /**
     * Sucht einen Artikel nach Artikelnummer
     */
    public Artikel sucheArtikelByArtNr(int artNr) {
        Iterator<Artikel> iter = artikelList.iterator();
        while (iter.hasNext()) {
            Artikel derArtikel = iter.next();
            if (derArtikel.getArtNr() == artNr) {
                return derArtikel;
            }
        }
        return null;
    }

    /**
     * Artikel ausgeben (liste)
     */
    public List<Artikel> alleArtikelAusgeben() {
        for (Artikel artikel : artikelList) {
            System.out.println(artikel.toString());
        }
        return artikelList;
    }

    /**
     * ein Artikel ausgeben (maybe mit sortierung)
     */
    public Artikel getArtikelByArtNr(int artikelNr) {
        return artikelList
                .stream()
                .filter(artikel -> artikel.getArtNr() == artikelNr)
                .findFirst().orElse(null);
    }

    /**
     * 2 Artikel vergleichen
     */
    public boolean artikelVergleichen(Artikel artikel1, Artikel artikel2) {
        return artikel1.equals(artikel2);
    }

    public void artikelAktualisierung(Artikel artikel) throws ArtikelNichtGefundenException {
        // die ID suchen, wenn nicht vorhanden dementsprechend fehlermeldung ausgeben
        var gefundenesArtikel = getArtikelByArtNr(artikel.getArtNr());
        if (gefundenesArtikel == null) {
            throw new ArtikelNichtGefundenException("Artikel konnte nicht gefunden werden");
        }
        if (artikel.getBezeichnung() != null) {
            gefundenesArtikel.setBezeichnung(artikel.getBezeichnung());
        }
        if (artikel.getBestand() < 0) {
            gefundenesArtikel.setBestand(artikel.getBestand());
        }
        if (artikel.getPreis() < 0) {
            gefundenesArtikel.setPreis(artikel.getPreis());
        }

    }

    public artikelList getArtList() {
        return this.artikelList;
    }


}

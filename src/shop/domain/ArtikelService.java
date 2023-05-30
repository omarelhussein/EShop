package shop.domain;

import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.entities.Artikel;
import shop.entities.BestandshistorieItem;
import shop.entities.Massenartikel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ArtikelService {

    private final List<Artikel> artikelList;
    private static ArtikelService instance;

    private ArtikelService() {
        artikelList = new ArrayList<>();
    }

    public synchronized static ArtikelService getInstance() {
        if (instance == null) {
            instance = new ArtikelService();
        }
        return instance;
    }


    /**
     * Artikel der Liste hinzufügen
     */
    public void addArtikel(Artikel artikel) {
        Artikel gefundenerArtikel;
        try {
            gefundenerArtikel = getArtikelByArtNr(artikel.getArtNr());
            if (gefundenerArtikel.getArtNr() == artikel.getArtNr()) {
                artikelList.remove(gefundenerArtikel);
            }
        } catch (ArtikelNichtGefundenException e) {
            // do nothing - muss nicht gefunden werden
        }
        artikelList.add(artikel);
    }

    /**
     * Artikel löschen
     */
    public void removeArtikel(Artikel artikel) throws ArtikelNichtGefundenException {
        if (getArtikelByArtNr(artikel.getArtNr()) == null) {
            throw new ArtikelNichtGefundenException(artikel.getArtNr());
        }
        artikelList.remove(artikel);
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
     * ein Artikel ausgeben (maybe mit sortierung)
     */
    public Artikel getArtikelByArtNr(int artikelNr) throws ArtikelNichtGefundenException {
        return artikelList
                .stream()
                .filter(artikel -> artikel.getArtNr() == artikelNr)
                .findFirst().orElseThrow(() -> new ArtikelNichtGefundenException(artikelNr));
    }

    public List<Artikel> sucheArtikelByQuery(String query) {
        return artikelList
                .stream()
                .filter(artikel -> artikel.getBezeichnung().toLowerCase().trim().contains(query.toLowerCase().trim())
                                   || String.valueOf(artikel.getArtNr()).equals(query))
                .collect(Collectors.toList());
    }

    /**
     * 2 Artikel vergleichen
     */
    public boolean artikelVergleichen(Artikel artikel1, Artikel artikel2) {
        return artikel1.equals(artikel2);
    }

    public void artikelAktualisieren(Artikel artikel) throws ArtikelNichtGefundenException {
        // die ID suchen, wenn nicht vorhanden dementsprechend fehlermeldung ausgeben
        var artikelByArtNr = getArtikelByArtNr(artikel.getArtNr());
        if (artikelByArtNr == null) {
            throw new ArtikelNichtGefundenException(artikel.getArtNr());
        }
        if (artikel.getBezeichnung() != null) {
            artikelByArtNr.setBezeichnung(artikel.getBezeichnung());
        }
        if (artikel.getBestand() > 0) {
            if (artikel.getBestand() != artikelByArtNr.getBestand()) {
                artikelByArtNr.getBestandshistorie().add(new BestandshistorieItem(artikel.getBestand(), false));
            }
            artikelByArtNr.setBestand(artikel.getBestand());
        }
        if (artikel.getPreis() > 0) {
            artikelByArtNr.setPreis(artikel.getPreis());
        }
        if (artikel instanceof Massenartikel) {
            if (((Massenartikel) artikel).getPackgroesse() > 0) {
                ((Massenartikel) artikelByArtNr).setPackgroesse(((Massenartikel) artikel).getPackgroesse());
            }
        }
    }

    /**
     * Ändert den Bestand eines Artikels
     *
     * @param artikelNr    Die Artikelnummer des Artikels dessen Bestand geändert werden soll
     * @param neuerBestand Der neue Bestand des Artikels
     * @return true, wenn der Bestand geändert werden konnte, false, wenn der Bestand nicht geändert werden konnte, weil er unter 0 fallen würde
     * @throws ArtikelNichtGefundenException Wenn kein Artikel mit der angegebenen Artikelnummer gefunden wurde
     */
    public boolean aendereArtikelBestand(int artikelNr, int neuerBestand, boolean istKauf) throws ArtikelNichtGefundenException {
        var gefundenerArtikel = getArtikelByArtNr(artikelNr);
        if (neuerBestand < 0) {
            return false;
        }
        gefundenerArtikel.setBestand(neuerBestand);
        gefundenerArtikel.getBestandshistorie().add(new BestandshistorieItem(neuerBestand, istKauf));
        return true;
    }

    public List<Artikel> getArtikelList() {
        return this.artikelList;
    }


    public int getNaechsteId() {
        int max = 0;
        for (Artikel artikel : artikelList) {
            if (artikel.getArtNr() > max) {
                max = artikel.getArtNr();
            }
        }
        return max + 1;
    }
}

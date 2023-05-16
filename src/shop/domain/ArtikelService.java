package shop.domain;

import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.entities.Artikel;

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

    public void artikelAktualisierung(Artikel artikel) throws ArtikelNichtGefundenException {
        // die ID suchen, wenn nicht vorhanden dementsprechend fehlermeldung ausgeben
        var gefundenesArtikel = getArtikelByArtNr(artikel.getArtNr());
        if (gefundenesArtikel == null) {
            throw new ArtikelNichtGefundenException(artikel.getArtNr());
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

    /**
     * Ändert den Bestand eines Artikels
     *
     * @param artikelNr Die Artikelnummer des Artikels dessen Bestand geändert werden soll
     * @param menge     Die Menge, um die der Bestand geändert werden soll, kann auch negativ sein. Der Bestand darf nicht unter 0 fallen.
     * @return true, wenn der Bestand geändert werden konnte, false, wenn der Bestand nicht geändert werden konnte, weil er unter 0 fallen würde
     * @throws ArtikelNichtGefundenException Wenn kein Artikel mit der angegebenen Artikelnummer gefunden wurde
     */
    public boolean aendereArtikelBestand(int artikelNr, int menge) throws ArtikelNichtGefundenException {
        var gefundenesArtikel = getArtikelByArtNr(artikelNr);
        if (gefundenesArtikel == null) {
            throw new ArtikelNichtGefundenException(artikelNr);
        }
        if (gefundenesArtikel.getBestand() + menge < 0) {
            return false;
        }
        gefundenesArtikel.setBestand(gefundenesArtikel.getBestand() + menge);
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

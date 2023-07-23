package domain;

import exceptions.artikel.AnzahlPackgroesseException;
import exceptions.artikel.ArtikelNichtGefundenException;
import entities.Artikel;
import entities.Massenartikel;
import persistence.FilePersistenceManager;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ArtikelService {

    private final List<Artikel> artikelList;
    private static ArtikelService instance;
    private final FilePersistenceManager<Artikel> persistenceManager;

    private ArtikelService() throws IOException {
        persistenceManager = new FilePersistenceManager<>("artikel.csv");
        artikelList = persistenceManager.readAll();
    }

    /**
     * Gibt die Singleton-Instanz von der Klasse zurück.
     * Wenn die Instanz noch nicht erstellt wurde, wird sie initialisiert.
     * Ein Singleton ist ein Entwurfsmuster, das sicherstellt, dass von einer Klasse nur
     * eine Instanz erstellt wird und einen globalen Zugriffspunkt zu dieser Instanz bereitstellt.
     * Es ist nützlich für Ressourcen, von denen nur eine einzige Instanz benötigt wird, wie
     * Dienste, Manager oder Datenbankzugriffe.
     */
    public synchronized static ArtikelService getInstance() throws IOException {
        if (instance == null) {
            instance = new ArtikelService();
        }
        return instance;
    }

    /**
     * Artikel der Liste hinzufügen
     */
    public void addArtikel(Artikel artikel) throws IOException {
        try {
            getArtikelByArtNr(artikel.getArtNr());
        } catch (ArtikelNichtGefundenException e) {
            artikelList.add(artikel);
        }
    }

    /**
     * Artikel löschen
     */
    public void removeArtikel(Artikel artikel) throws ArtikelNichtGefundenException {
        if (getArtikelByArtNr(artikel.getArtNr()) == null) {
            throw new ArtikelNichtGefundenException(artikel.getArtNr());
        }
        artikel.setBestand(0);
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
        // .stream() wandelt die Liste in einen Stream um, der die Methoden filter, map, reduce, etc. zur Verfügung stellt.
        // .filter() filtert die Liste nach den angegebenen Kriterien. Ein filter() kann auch mehrfach hintereinander aufgerufen werden
        // um suchkriterien zu verfeinern. Ein filter() erwartet ein Predicate (Lambda-Ausdruck), das true oder false zurückgibt.
        // .findFirst() gibt das erste Element des Streams zurück, das die Filterkriterien erfüllt. Falls kein Element gefunden wird,
        // wird eine NoSuchElementException geworfen. Deshalb wird hier ein orElseThrow() verwendet. Dieser muss auch als Lambda-Ausdruck
        // geschrieben werden. Falls kein Element gefunden wird, wird eine ArtikelNichtGefundenException geworfen.
        return artikelList
                .stream()
                .filter(artikel -> artikel.getArtNr() == artikelNr)
                .findFirst().orElseThrow(() -> new ArtikelNichtGefundenException(artikelNr));
    }

    public List<Artikel> sucheArtikelByQuery(String query) {
        // .stream() wandelt die Liste in einen Stream um, der die Methoden filter, map, reduce, etc. zur Verfügung stellt.
        // .filter() filtert die Liste nach den angegebenen Kriterien. Ein filter() kann auch mehrfach hintereinander aufgerufen werden
        // um suchkriterien zu verfeinern. Ein filter() erwartet ein Predicate (Lambda-Ausdruck), das true oder false zurückgibt.
        // .toLowerCase().trim() wandelt den String in Kleinbuchstaben um und entfernt Leerzeichen am Anfang und Ende.
        // .toList() wandelt den Stream wieder in eine Liste um.
        return artikelList
                .stream()
                .filter(artikel -> artikel.getBezeichnung().toLowerCase().trim().contains(query.toLowerCase().trim())
                                   || String.valueOf(artikel.getArtNr()).equals(query))
                .toList();
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

    public void save() throws IOException {
        persistenceManager.replaceAll(artikelList);
    }

    public void anzahlPackgroesseVergleich(Artikel artikel, int anzahl) throws AnzahlPackgroesseException {
        if (artikel instanceof Massenartikel) {
            double massenanzahl = (double) anzahl / ((Massenartikel) artikel).getPackgroesse();
            if (massenanzahl % 1 != 0) throw new AnzahlPackgroesseException(anzahl, (Massenartikel) artikel);
        }
    }
}

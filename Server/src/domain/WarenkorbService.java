package domain;

import entities.*;
import exceptions.artikel.AnzahlPackgroesseException;
import exceptions.artikel.ArtikelNichtGefundenException;
import exceptions.warenkorb.BestandUeberschrittenException;
import exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * WarenkorbService ist eine Singleton-Klasse, die das Verwalten von Warenkörben ermöglicht.
 * Sie nutzt den ArtikelService, um Artikelinformationen abzurufen.
 */
public class WarenkorbService {
    private final List<Warenkorb> warenkorbList = Collections.synchronizedList(new ArrayList<>());
    private final ArtikelService artikelservice;
    private static WarenkorbService instance;


    /**
     * Der private Konstruktor für WarenkorbService.
     * Initialisiert die Instanz des ArtikelService durch Aufruf von dessen getInstance-Methode.
     *
     * @throws IOException wenn die Instanz des ArtikelService nicht abgerufen werden kann
     */
    private WarenkorbService() throws IOException {
        artikelservice = ArtikelService.getInstance();
    }

    /**
     * Gibt die Singleton-Instanz von der Klasse zurück.
     * Wenn die Instanz noch nicht erstellt wurde, wird sie initialisiert.
     * Ein Singleton ist ein Entwurfsmuster, das sicherstellt, dass von einer Klasse nur
     * eine Instanz erstellt wird und einen globalen Zugriffspunkt zu dieser Instanz bereitstellt.
     * Es ist nützlich für Ressourcen, von denen nur eine einzige Instanz benötigt wird, wie
     * Dienste, Manager oder Datenbankzugriffe.
     *
     * @return die Singleton-Instanz von WarenkorbService
     * @throws IOException wenn die Instanz des WarenkorbService nicht erstellt werden kann
     */
    public synchronized static WarenkorbService getInstance() throws IOException {
        if (instance == null) {
            instance = new WarenkorbService();
        }
        return instance;
    }

    public boolean legeArtikelImWarenkorb(int artikelNr, int anzahl)
            throws ArtikelNichtGefundenException, BestandUeberschrittenException,
            IOException, WarenkorbArtikelNichtGefundenException, AnzahlPackgroesseException {
        var artikel = artikelservice.getArtikelByArtNr(artikelNr);

        if (anzahl <= 0) {
            return false;
        }
        var warenkorbArtikelList = WarenkorbService.getInstance().getWarenkorb().getWarenkorbArtikelList();
        for (WarenkorbArtikel warenkorbArtikel : warenkorbArtikelList) {
            if (artikel.equals(warenkorbArtikel.getArtikel()) && (warenkorbArtikel.getAnzahl() + anzahl > artikel.getBestand())) {
                return false;
            }
            if (artikel.equals(warenkorbArtikel.getArtikel())) {
                WarenkorbService.getInstance().aendereWarenkorbArtikelAnzahl(warenkorbArtikel.getArtikel().getArtNr(), anzahl + warenkorbArtikel.getAnzahl(), true);
                return true;
            }
        }
        artikelservice.anzahlPackgroesseVergleich(artikelservice.getArtikelByArtNr(artikelNr), anzahl);
        var warenkorb = getWarenkorbByKundenNr(UserContext.getUser().getPersNr());
        if (anzahl > artikel.getBestand()) {
            throw new BestandUeberschrittenException(artikelNr, anzahl, artikel);
        }
        warenkorb.addArtikel(new WarenkorbArtikel(artikel, anzahl));
        return true;
    }

    public boolean removeArtikelVomWarenkorb(int artikelNr) throws WarenkorbArtikelNichtGefundenException {
        var artikel = getWarenkorbArtikelByArtNrOrThrow(artikelNr);
        var warenkorb = getWarenkorb();
        if (warenkorb == null) return false;

        var iterator = warenkorb.getWarenkorbArtikelList().iterator();
        while (iterator.hasNext()) {
            if (artikel.getArtikel().getArtNr() == iterator.next().getArtikel().getArtNr()) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * Ändert die Anzahl eines Artikels im Warenkorb. Kann auch negativ sein, um die Anzahl zu verringern.
     * Setzt die Anzahl auf 0, wenn die Anzahl unter 0 fällt.
     * Z. B. im Warenkorb sind 5 Artikel, es wird versucht 10 Artikel zu entfernen.
     *
     * @param artikelNr Die Artikelnummer des Artikels dessen Anzahl im Warenkorb geändert werden soll
     * @param anzahl    Die neue anzahl des Artikels im Warenkorb
     * @throws exceptions.warenkorb.BestandUeberschrittenException         Wenn die Anzahl der Artikel im Warenkorb den Bestand des Artikels übersteigt
     * @throws exceptions.warenkorb.WarenkorbArtikelNichtGefundenException Wenn kein Artikel mit der angegebenen Artikelnummer im Warenkorb gefunden wurde
     */
    public void aendereWarenkorbArtikelAnzahl(int artikelNr, int anzahl, boolean pruefeBestand) throws BestandUeberschrittenException,
            WarenkorbArtikelNichtGefundenException, ArtikelNichtGefundenException, AnzahlPackgroesseException {
        var warenkorbArtikel = getWarenkorbArtikelByArtNrOrThrow(artikelNr);
        if (pruefeBestand) pruefeBestand(warenkorbArtikel, anzahl);
        artikelservice.anzahlPackgroesseVergleich(artikelservice.getArtikelByArtNr(artikelNr), anzahl);
        if (anzahl <= 0) {
            removeArtikelVomWarenkorb(artikelNr);
        } else {
            warenkorbArtikel.setAnzahl(anzahl);
        }
    }

    public WarenkorbArtikel getWarenkorbArtikelByArtNrOrThrow(int artNr) throws WarenkorbArtikelNichtGefundenException {
        var warenkorb = getWarenkorbArtikelByArtNr(artNr);
        if (warenkorb == null) throw new WarenkorbArtikelNichtGefundenException(artNr);
        return warenkorb;
    }

    public synchronized WarenkorbArtikel getWarenkorbArtikelByArtNr(int artNr) {
        var warenkorb = getWarenkorb();
        var warenkorbArtikelList = warenkorb.getWarenkorbArtikelList();
        for (WarenkorbArtikel warenkorbArtikel : warenkorbArtikelList) {
            if (warenkorbArtikel.getArtikel().getArtNr() == artNr) {
                return warenkorbArtikel;
            }
        }
        return null;
    }

    public synchronized Warenkorb getWarenkorbByKundenNr(int kundenNr) {
        for (Warenkorb value : warenkorbList) {
            if (kundenNr == value.getKunde().getPersNr()) {
                return value;
            }
        }
        return null;
    }

    public synchronized Warenkorb getWarenkorb() {
        var user = UserContext.getUser();
        if (user == null || user instanceof Mitarbeiter) {
            System.out.println("User null");
            System.out.println("ON THREAD: " + Thread.currentThread().getName());
            return null;
        }
        var warenkorb = getWarenkorbByKundenNr(user.getPersNr());
        if (warenkorb == null && user instanceof Kunde kunde) {
            warenkorb = neuerKorb(kunde);
        }
        System.out.println("USER: " + user.getNutzername() + " has WARENKORB: " + warenkorb);
        System.out.println("ON THREAD: " + Thread.currentThread().getName());
        return warenkorb;
    }

    public synchronized Warenkorb neuerKorb(Kunde kunde) {
        var warenkorb = getWarenkorbByKundenNr(kunde.getPersNr());
        if (warenkorb != null) {
            warenkorbList.remove(warenkorb);
        }
        var neuerKorb = new Warenkorb(kunde);
        warenkorbList.add(neuerKorb);
        return neuerKorb;
    }

    public List<Warenkorb> getWarenkorbList() {
        return warenkorbList;
    }

    public void warenkorbLeeren() {
        if (UserContext.getUser() instanceof Kunde kunde) {
            neuerKorb(kunde);
        }
    }

    public void pruefeBestand(WarenkorbArtikel warenkorbArtikel, int neuAnzahl) throws BestandUeberschrittenException, ArtikelNichtGefundenException {
        var artikel = artikelservice.getArtikelByArtNr(warenkorbArtikel.getArtikel().getArtNr());
        var tmpNeuBestand = artikel.getBestand() - neuAnzahl;
        if (tmpNeuBestand < 0)
            throw new BestandUeberschrittenException(artikel.getBestand(), neuAnzahl, artikel);
    }

    public void kaufeArtikel(WarenkorbArtikel warenkorbArtikel) throws BestandUeberschrittenException, ArtikelNichtGefundenException {
        pruefeBestand(warenkorbArtikel, warenkorbArtikel.getAnzahl());
        var neuerBestand = warenkorbArtikel.getArtikel().getBestand() - warenkorbArtikel.getAnzahl();
        artikelservice.aendereArtikelBestand(warenkorbArtikel.getArtikel().getArtNr(), neuerBestand, true);
    }

}
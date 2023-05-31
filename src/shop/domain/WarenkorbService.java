package shop.domain;

import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import shop.domain.exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import shop.entities.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WarenkorbService {
    private final List<Warenkorb> warenkorbList = new ArrayList<>();
    private final ArtikelService artikelservice;
    private static WarenkorbService instance;


    private WarenkorbService() throws IOException {
        artikelservice = ArtikelService.getInstance();
    }

    public synchronized static WarenkorbService getInstance() throws IOException {
        if (instance == null) {
            instance = new WarenkorbService();
        }
        return instance;
    }

    public boolean legeArtikelImWarenkorb(int artikelNr, int anzahl)
            throws ArtikelNichtGefundenException, BestandUeberschrittenException {
        var artikel = artikelservice.getArtikelByArtNr(artikelNr);

        if (getWarenkorbArtikelByArtNr(artikelNr) != null || anzahl <= 0) {
            return false;
        }
        var warenkorb = getWarenkorbByKundenNr(UserContext.getUser().getPersNr());
        var anzahlZuKaufen = (artikel instanceof Massenartikel massenartikel) ? anzahl * massenartikel.getPackgroesse() : anzahl;
        if (anzahlZuKaufen > artikel.getBestand()) {
            throw new BestandUeberschrittenException(artikelNr, anzahlZuKaufen, artikel);
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
     * @throws BestandUeberschrittenException         Wenn die Anzahl der Artikel im Warenkorb den Bestand des Artikels übersteigt
     * @throws WarenkorbArtikelNichtGefundenException Wenn kein Artikel mit der angegebenen Artikelnummer im Warenkorb gefunden wurde
     */
    public void aendereWarenkorbArtikelAnzahl(int artikelNr, int anzahl) throws BestandUeberschrittenException,
            WarenkorbArtikelNichtGefundenException {
        var warenkorbArtikel = getWarenkorbArtikelByArtNrOrThrow(artikelNr);
        pruefeBestand(warenkorbArtikel, anzahl);
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

    public WarenkorbArtikel getWarenkorbArtikelByArtNr(int artNr) {
        var warenkorb = getWarenkorb();
        var warenkorbArtikelList = warenkorb.getWarenkorbArtikelList();
        for (WarenkorbArtikel warenkorbArtikel : warenkorbArtikelList) {
            if (warenkorbArtikel.getArtikel().getArtNr() == artNr) {
                return warenkorbArtikel;
            }
        }
        return null;
    }

    public Warenkorb getWarenkorbByKundenNr(int kundenNr) {
        for (Warenkorb value : warenkorbList) {
            if (kundenNr == value.getKunde().getPersNr()) {
                return value;
            }
        }
        return null;
    }

    public Warenkorb getWarenkorb() {
        var user = UserContext.getUser();
        if (user == null || user instanceof Mitarbeiter) return null;
        var warenkorb = getWarenkorbByKundenNr(user.getPersNr());
        if (warenkorb == null && user instanceof Kunde kunde) {
            neuerKorb(kunde);
        }
        return getWarenkorbByKundenNr(user.getPersNr());
    }

    public void neuerKorb(Kunde kunde) {
        var warenkorb = getWarenkorbByKundenNr(kunde.getPersNr());
        if (warenkorb != null) {
            warenkorbList.remove(warenkorb);
        }
        warenkorbList.add(new Warenkorb(kunde));
    }

    public List<Warenkorb> getWarenkorbList() {
        return warenkorbList;
    }

    public void warenkorbLeeren() {
        if (UserContext.getUser() instanceof Kunde kunde) {
            neuerKorb(kunde);
        }
    }

    public void pruefeBestand(WarenkorbArtikel warenkorbArtikel, int neuAnzahl) throws BestandUeberschrittenException {
        var anzahl = berechneGenaueAnzahl(warenkorbArtikel, neuAnzahl);
        var tmpNeuBestand = warenkorbArtikel.getArtikel().getBestand() + anzahl;
        if (tmpNeuBestand < 0 || tmpNeuBestand < anzahl)
            throw new BestandUeberschrittenException(warenkorbArtikel.getArtikel().getBestand(),
                    anzahl,
                    warenkorbArtikel.getArtikel());
    }

    /**
     * Berechnet die genaue Anzahl eines Artikels im Warenkorb.
     * Bei Massenartikeln wird die Anzahl mit der Packgröße multipliziert.
     */
    private int berechneGenaueAnzahl(WarenkorbArtikel warenkorbArtikel, int neuAnzahl) {
        return (warenkorbArtikel.getArtikel() instanceof Massenartikel massenartikel) ?
                neuAnzahl * massenartikel.getPackgroesse() : neuAnzahl;
    }

    public void kaufeArtikel(WarenkorbArtikel warenkorbArtikel) throws BestandUeberschrittenException, ArtikelNichtGefundenException {
        pruefeBestand(warenkorbArtikel, warenkorbArtikel.getAnzahl());
        var neuerBestand = warenkorbArtikel.getArtikel().getBestand() - berechneGenaueAnzahl(warenkorbArtikel, warenkorbArtikel.getAnzahl());
        artikelservice.aendereArtikelBestand(warenkorbArtikel.getArtikel().getArtNr(), neuerBestand, true);
    }

}
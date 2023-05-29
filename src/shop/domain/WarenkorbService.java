package shop.domain;

import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import shop.domain.exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import shop.entities.*;

import java.util.ArrayList;
import java.util.List;

public class WarenkorbService {
    private final List<Warenkorb> warenkorbList = new ArrayList<>();
    private final ArtikelService artikelservice;
    private static WarenkorbService instance;

    private WarenkorbService() {
        artikelservice = ArtikelService.getInstance();
    }

    public synchronized static WarenkorbService getInstance() {
        if (instance == null) {
            instance = new WarenkorbService();
        }
        return instance;
    }


    public boolean legeArtikelImWarenkorb(int artikelNr, int anzahl)
            throws ArtikelNichtGefundenException, BestandUeberschrittenException, WarenkorbArtikelNichtGefundenException {
        var artikel = artikelservice.getArtikelByArtNr(artikelNr);
        if (artikel == null) throw new ArtikelNichtGefundenException(artikelNr);

        if (getWarenkorbArtikelByArtNr2(artikelNr) != null) {
            return false;
        }

        if (artikel instanceof Massenartikel) {
            if (artikel.getBestand() >= anzahl * ((Massenartikel) artikel).getPackgroesse() && anzahl > 0) {
                var warenkorb = getWarenkorbByKundenNr(UserContext.getUser().getPersNr());
                warenkorb.addArtikel(new WarenkorbArtikel(artikel, anzahl * ((Massenartikel) artikel).getPackgroesse()));
                return artikelservice.aendereArtikelBestand(artikelNr, artikel.getBestand() - ((Massenartikel) artikel).getPackgroesse() * anzahl);
            } else {
                throw new BestandUeberschrittenException(artikel.getBestand(), anzahl);
            }
        } else {
            if (artikel.getBestand() >= anzahl && anzahl > 0) {
                var warenkorb = getWarenkorbByKundenNr(UserContext.getUser().getPersNr());
                warenkorb.addArtikel(new WarenkorbArtikel(artikel, anzahl));
                return artikelservice.aendereArtikelBestand(artikelNr, artikel.getBestand() - anzahl);
            } else {
                throw new BestandUeberschrittenException(artikel.getBestand(), anzahl);
            }
        }
    }

    public boolean removeArtikelVomWarenkorb(int artikelNr) throws ArtikelNichtGefundenException {
        var artikel = artikelservice.getArtikelByArtNr(artikelNr);
        if (artikel == null) throw new ArtikelNichtGefundenException(artikelNr);
        var warenkorb = getWarenkorb();
        if (warenkorb == null || warenkorb.istLeer()) return false;
        var warenkorbArtikelList = warenkorb.getWarenkorbArtikelList();
        var iterator = warenkorbArtikelList.iterator();
        while (iterator.hasNext()) {
            var warenkorbArtikel = iterator.next();
            if (artikel.getArtNr() != warenkorbArtikel.getArtikel().getArtNr()) continue;
            artikelservice.aendereArtikelBestand(artikelNr, artikel.getBestand() + warenkorbArtikel.getAnzahl());
            iterator.remove();
            return true;
        }
        return false;
    }

    /**
     * Ändert die Anzahl eines Artikels im Warenkorb. Kann auch negativ sein, um die Anzahl zu verringern.
     * Setzt die Anzahl auf 0, wenn die Anzahl unter 0 fällt.
     * Z. B. im Warenkorb sind 5 Artikel, es wird versucht 10 Artikel zu entfernen.
     *
     * @param artikelNr Die Artikelnummer des Artikels dessen Anzahl im Warenkorb geändert werden soll
     * @param menge     Die neue menge des Artikels im Warenkorb
     * @throws ArtikelNichtGefundenException          Wenn kein Artikel mit der angegebenen Artikelnummer gefunden wurde
     * @throws BestandUeberschrittenException         Wenn die Anzahl der Artikel im Warenkorb den Bestand des Artikels übersteigt
     * @throws WarenkorbArtikelNichtGefundenException Wenn kein Artikel mit der angegebenen Artikelnummer im Warenkorb gefunden wurde
     */
    public void aendereWarenkorbArtikelAnzahl(int artikelNr, int menge)
            throws ArtikelNichtGefundenException, BestandUeberschrittenException,
            WarenkorbArtikelNichtGefundenException {
        var warenkorbArtikel = getWarenkorbArtikelByArtNr(artikelNr);
        var tmpNeuBestand = warenkorbArtikel.getArtikel().getBestand() + warenkorbArtikel.getAnzahl();
        if (tmpNeuBestand < 0 || tmpNeuBestand < menge)
            throw new BestandUeberschrittenException(warenkorbArtikel.getArtikel().getBestand(), menge);
        if (removeArtikelVomWarenkorb(artikelNr)) {
            if (menge > 0) {
                legeArtikelImWarenkorb(artikelNr, menge);
            }
        } else {
            throw new WarenkorbArtikelNichtGefundenException(artikelNr);
        }
    }

    public WarenkorbArtikel getWarenkorbArtikelByArtNr(int artNr) throws WarenkorbArtikelNichtGefundenException {
        var warenkorb = getWarenkorb();
        var warenkorbArtikelList = warenkorb.getWarenkorbArtikelList();
        for (WarenkorbArtikel warenkorbArtikel : warenkorbArtikelList) {
            if (warenkorbArtikel.getArtikel().getArtNr() == artNr) {
                return warenkorbArtikel;
            }
        }
        throw new WarenkorbArtikelNichtGefundenException(artNr);
    }

    public WarenkorbArtikel getWarenkorbArtikelByArtNr2(int artNr) {
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


}
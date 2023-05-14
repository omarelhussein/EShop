package shop.domain;

import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import shop.entities.Kunde;
import shop.entities.Warenkorb;
import shop.entities.WarenkorbArtikel;

import java.util.ArrayList;
import java.util.List;

public class WarenkorbService {
    private final List<Warenkorb> warenkorbList = new ArrayList<>();
    private final ArtikelService artikelservice = new ArtikelService();
    private Kunde aktuellerKunde;

    public WarenkorbService(Kunde aktuellerKunde) {
        this.aktuellerKunde = aktuellerKunde;
    }

    public boolean legeArtikelImWarenkorb(int artikelNr, int anzahl)
            throws ArtikelNichtGefundenException, BestandUeberschrittenException {
        var artikel = artikelservice.getArtikelByArtNr(artikelNr);
        if (artikel == null) throw new ArtikelNichtGefundenException(artikelNr);
        if (artikel.getBestand() > anzahl && anzahl > 0) {
            var warenkorb = getWarenkorbByKundenNr(aktuellerKunde.getPersNr());
            warenkorb.addArtikel(new WarenkorbArtikel(artikel, anzahl));
            return artikelservice.aendereArtikelBestand(artikelNr, -anzahl);
        } else {
            throw new BestandUeberschrittenException();
        }
    }

    public boolean removeArtikelVomWarenkorb(int artikelNr, int anzahl) throws ArtikelNichtGefundenException {
        var artikel = artikelservice.getArtikelByArtNr(artikelNr);
        if (artikel == null) throw new ArtikelNichtGefundenException(artikelNr);
        var warenkorb = getWarenkorb();
        if (warenkorb == null) return false;
        var warenkorbArtikelList = warenkorb.getWarenkorbArtikelList();
        var iterator = warenkorbArtikelList.iterator();
        while (iterator.hasNext()) {
            var warenkorbArtikel = iterator.next();
            if (artikel.getArtNr() != warenkorbArtikel.getArtikel().getArtNr()) continue;
            if (warenkorbArtikel.getAnzahl() > anzahl) {
                var erfolg = aendereWarenkorbArtikelAnzahl(artikel.getArtNr(), anzahl);
                if (warenkorbArtikel.getAnzahl() <= 0) {
                    iterator.remove();
                }
                return erfolg;
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
     * @param menge     Die Menge, um die die Anzahl des Artikels geändert werden soll. Kann auch negativ sein, um die Anzahl zu verringern
     * @return true, wenn die Anzahl erfolgreich geändert wurde, false, wenn nicht
     * @throws ArtikelNichtGefundenException Wenn kein Artikel mit der angegebenen Artikelnummer gefunden wurde
     */
    public boolean aendereWarenkorbArtikelAnzahl(int artikelNr, int menge) throws ArtikelNichtGefundenException {
        var warenkorb = getWarenkorb();
        if (warenkorb == null) return false;
        var warenkorbArtikelList = warenkorb.getWarenkorbArtikelList();
        for (WarenkorbArtikel warenkorbArtikel : warenkorbArtikelList) {
            if (warenkorbArtikel.getArtikel().getArtNr() == artikelNr) {
                if (warenkorbArtikel.getAnzahl() + menge < 0) {
                    warenkorbArtikel.setAnzahl(0);
                }
                warenkorbArtikel.setAnzahl(warenkorbArtikel.getAnzahl() + menge);
                return true;
            }
        }
        throw new ArtikelNichtGefundenException(artikelNr);
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
        return getWarenkorbByKundenNr(aktuellerKunde.getPersNr());
    }

    public void neuerKorb(Kunde kunde) {
        warenkorbList.add(new Warenkorb(kunde));
    }

    public void setAktuellerKunde(Kunde aktuellerKunde) {
        this.aktuellerKunde = aktuellerKunde;
    }

    public List<Warenkorb> getWarenkorbList() {
        return warenkorbList;
    }

    public void WarenkorbLeeren(){
        var iterator = warenkorbList.iterator();
        while (iterator.hasNext()) {
            var warenkorb = iterator.next();
            if (warenkorb.getKunde() == aktuellerKunde) {
                warenkorb = new Warenkorb(aktuellerKunde);
            }
        }
    }

}
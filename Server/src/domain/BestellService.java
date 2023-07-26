package domain;

import exceptions.artikel.ArtikelNichtGefundenException;
import exceptions.warenkorb.BestandUeberschrittenException;
import exceptions.warenkorb.RechnungNichtGefundenException;
import entities.Rechnung;
import entities.WarenkorbArtikel;
import entities.enums.EreignisTyp;
import entities.enums.KategorieEreignisTyp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BestellService {

    private final WarenkorbService warenkorbservice;
    private final List<Rechnung> rechnungen;

    public BestellService() throws IOException {
        warenkorbservice = WarenkorbService.getInstance();
        rechnungen = new ArrayList<>();
    }

    /**
     * kauft den Warenkorb des aktiellen Kunden. Verändert den Bestand im Lager um die Anzahl den Bestand der Artikel
     * im Warenkorb und leert den Warenkorb
     * @throws BestandUeberschrittenException
     * @throws ArtikelNichtGefundenException
     * @throws IOException
     */
    public void kaufen() throws BestandUeberschrittenException, ArtikelNichtGefundenException, IOException {
        var warenkorb = warenkorbservice.getWarenkorb();
        for (WarenkorbArtikel artikel : warenkorb.getWarenkorbArtikelList()) {
            // erstmal prüfen, dann kaufen
            warenkorbservice.pruefeBestand(artikel, artikel.getAnzahl());
        }
        for (WarenkorbArtikel artikel : warenkorb.getWarenkorbArtikelList()) {
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.KAUF, artikel.getArtikel(),  true);
            warenkorbservice.kaufeArtikel(artikel);
        }

        warenkorbservice.warenkorbLeeren();
    }

    /**
     * Erstellt ein Rechnungsobjekt, fügt dies in die Liste in der Rechnungen gespeichert werden ein und gibt
     * die erstellte Rechnung wieder
     * @return
     */
    public Rechnung erstelleRechnung() {
        Rechnung rechnung = new Rechnung(
                warenkorbservice.getWarenkorb(),
                warenkorbservice.getWarenkorb().getKunde(),
                getNaechsteRechnungsNr()
        );
        rechnungen.add(rechnung);
        return rechnung;
    }

    /**
     * Gibt die Liste aller Rechnungen wieder
     * @return
     */
    public List<Rechnung> getRechnungen() {
        return rechnungen;
    }

    /**
     * sucht die Liste der Rechnungen nach der Rechnung mit der angegebenen Rechnungsnummer ab und returnt diese
     * @param rechnungsNr
     * @return
     * @throws RechnungNichtGefundenException
     */
    public Rechnung getRechnung(int rechnungsNr) throws RechnungNichtGefundenException {
        for (Rechnung rechnung : rechnungen) {
            if (rechnung.getRechnungsNr() == rechnungsNr) {
                return rechnung;
            }
        }
        throw new RechnungNichtGefundenException(rechnungsNr);
    }

    /**
     * sucht die Liste der Rechnungen nach der Rechnung mit der angegebenen Kundennummer ab und returnt diese
     * @param kundenNr
     * @return
     */
    public List<Rechnung> getRechnungenByKunde(int kundenNr) {
        List<Rechnung> rechnungen = new ArrayList<>();
        for (Rechnung rechnung : this.rechnungen) {
            if (rechnung.getKunde().getPersNr() == kundenNr) {
                rechnungen.add(rechnung);
            }
        }
        return rechnungen;
    }

    /**
     * Gibt die nächste Rechnungsnummer zurück. Diese Methode geht alle Rechnungen durch und sucht die höchste
     * Rechnungsnummer. Diese wird dann um 1 erhöht und zurückgegeben.
     * @return die nächste Rechnungsnummer
     */
    private int getNaechsteRechnungsNr() {
        int max = 0;
        for (Rechnung rechnung : rechnungen) {
            if (rechnung.getRechnungsNr() > max) {
                max = rechnung.getRechnungsNr();
            }
        }
        return max + 1;
    }


}

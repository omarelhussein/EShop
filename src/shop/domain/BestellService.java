package shop.domain;

import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import shop.domain.exceptions.warenkorb.RechnungNichtGefundenException;
import shop.entities.Rechnung;
import shop.entities.WarenkorbArtikel;

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

    public void kaufen() throws BestandUeberschrittenException, ArtikelNichtGefundenException {
        var warenkorb = warenkorbservice.getWarenkorb();
        for (WarenkorbArtikel artikel : warenkorb.getWarenkorbArtikelList()) {
            // erstmal prüfen, dann kaufen
            warenkorbservice.pruefeBestand(artikel, artikel.getAnzahl());
        }
        for (WarenkorbArtikel artikel : warenkorb.getWarenkorbArtikelList()) {
            warenkorbservice.kaufeArtikel(artikel);
        }
        warenkorbservice.warenkorbLeeren();
    }

    public Rechnung erstelleRechnung() {
        Rechnung rechnung = new Rechnung(
                warenkorbservice.getWarenkorb(),
                warenkorbservice.getWarenkorb().getKunde(),
                getNaechsteRechnungsNr()
        );
        rechnungen.add(rechnung);
        return rechnung;
    }

    public List<Rechnung> getRechnungen() {
        return rechnungen;
    }

    public Rechnung getRechnung(int rechnungsNr) throws RechnungNichtGefundenException {
        for (Rechnung rechnung : rechnungen) {
            if (rechnung.getRechnungsNr() == rechnungsNr) {
                return rechnung;
            }
        }
        throw new RechnungNichtGefundenException(rechnungsNr);
    }

    public List<Rechnung> getRechnungenByKunde(int kundenNr) {
        List<Rechnung> rechnungen = new ArrayList<>();
        for (Rechnung rechnung : this.rechnungen) {
            if (rechnung.getKunde().getPersNr() == kundenNr) {
                rechnungen.add(rechnung);
            }
        }
        return rechnungen;
    }

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

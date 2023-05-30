package shop.domain;

import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import shop.entities.Rechnung;
import shop.entities.WarenkorbArtikel;

public class BestellService {

    private final WarenkorbService warenkorbservice;

    public BestellService() {
        warenkorbservice = WarenkorbService.getInstance();
    }

    public void kaufen() throws BestandUeberschrittenException, ArtikelNichtGefundenException {
        var warenkorb = warenkorbservice.getWarenkorb();
        for (WarenkorbArtikel artikel: warenkorb.getWarenkorbArtikelList()) {
            // erstmal prüfen, dann kaufen
            warenkorbservice.pruefeBestand(artikel, artikel.getAnzahl());
        }
        for (WarenkorbArtikel artikel: warenkorb.getWarenkorbArtikelList()) {
            warenkorbservice.kaufeArtikel(artikel);
        }
        warenkorbservice.warenkorbLeeren();
    }

    public String rechnungToString() {
        Rechnung rechner = new Rechnung();
        return rechner.toString();
    }
}

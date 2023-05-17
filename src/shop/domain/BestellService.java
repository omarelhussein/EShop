package shop.domain;

import shop.entities.Rechnung;
import shop.entities.WarenkorbArtikel;

public class BestellService {

    private final WarenkorbService warenkorbservice;

    public BestellService() {
        warenkorbservice = WarenkorbService.getInstance();
    }

    public void kaufen() {
        warenkorbservice.warenkorbLeeren();
    }

    public String rechnungtoString() {
        Rechnung rechner = new Rechnung();
        return rechner.toString();
    }
}

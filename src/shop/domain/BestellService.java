package shop.domain;

import shop.entities.Rechnung;

public class BestellService {

    private final WarenkorbService warenkorbservice;

    public BestellService() {
        warenkorbservice = WarenkorbService.getInstance();
    }

    public void kaufen() {
        EreignisService.getInstance().gekauftEreignis(warenkorbservice.getWarenkorb().getArtikelList());
        warenkorbservice.warenkorbLeeren();
    }

    public String rechnungtoString() {
        Rechnung rechner = new Rechnung();
        return rechner.toString();
    }
}

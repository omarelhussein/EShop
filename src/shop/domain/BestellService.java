package shop.domain;

import shop.entities.Warenkorb;
import shop.entities.WarenkorbArtikel;

public class BestellService {

    private final WarenkorbService warenkorbservice;

    public BestellService() {
        warenkorbservice = WarenkorbService.getInstance();
    }

    public void kaufen() {
        warenkorbservice.warenkorbLeeren();
    }

    public void rechnungErstellen() {
        var warenkorb = warenkorbservice.getWarenkorb();
        for (WarenkorbArtikel warenkorbArtikel : warenkorb.getWarenkorbArtikelList()) {
            var artikel = warenkorbArtikel.getArtikel();
            System.out.println("Name: " + artikel.getBezeichnung() + "  x"
                    + warenkorbArtikel.getAnzahl() +
                    "  Einzelpreis: " + artikel.getPreis() + "€" +
                    "  Gesamtpreis: " + warenkorbArtikel.getGesamtPreis() + "€");
        }
        System.out.println(" ");
        System.out.println("Gesamtsumme: " + warenkorb.getGesamtSumme() + "€");
    }
}

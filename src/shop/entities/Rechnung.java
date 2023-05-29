package shop.entities;

import shop.domain.WarenkorbService;

public class Rechnung {

    private final Warenkorb warenkorb;

    public Rechnung() {
        warenkorb = WarenkorbService.getInstance().getWarenkorb();
    }

    public String toString() {
        String rechner = "";
        var kunde = (Kunde) UserContext.getUser();
        rechner += ("\nRechnung\n" + kunde.getName() + "\n" + kunde.getAdresse() + kunde.getEmail() + "\n \n Artikel:\n");
        for (WarenkorbArtikel warenkorbArtikel : this.warenkorb.getWarenkorbArtikelList()) {
            var artikel = warenkorbArtikel.getArtikel();
            rechner += ("Name: " + artikel.getBezeichnung() + "  x"
                    + warenkorbArtikel.getAnzahl() +
                    "  Einzelpreis: " + artikel.getPreis() + "€" +
                    "  Gesamtpreis: " + warenkorbArtikel.getGesamtPreis() + "€\n");
        }
        rechner += ("\nGesamtsumme: " + warenkorb.getGesamtSumme() + "€\n");
        return rechner;
    }
}

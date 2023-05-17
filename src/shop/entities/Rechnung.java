package shop.entities;

import shop.domain.WarenkorbService;
import shop.entities.Kunde;

public class Rechnung {

    private final WarenkorbService warenkorbservice;
    private Warenkorb warenkorb;
    private Kunde kunde;

    public Rechnung() {
        warenkorbservice = WarenkorbService.getInstance();
        kunde = warenkorbservice.getAktuellerKunde();
        warenkorb = warenkorbservice.getWarenkorb();
    }

    public String toString() {
        String rechner ="";
        rechner += ( "\nRechnung\n" + kunde.getName() + "\n" + kunde.getAdresse() + kunde.getEmail() + "\n \n Artikel:\n");
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

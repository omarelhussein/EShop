package shop.entities;

import shop.domain.WarenkorbService;
import shop.utils.StringUtils;

public class Rechnung {

    private final Warenkorb warenkorb;

    public Rechnung() {
        warenkorb = WarenkorbService.getInstance().getWarenkorb();
    }

    public String toString() {
        String rechnung = "\t" + StringUtils.lineSeparator(50);
        var kunde = (Kunde) UserContext.getUser();
        rechnung += ("\n\t\t\t\t\tRechnung\n\n\t\tKunde: " + kunde.getName() + "\n\t\tAdresse: " + kunde.getAdresse() + "\n\t\tNutzername: "
                     + kunde.getNutzername() + "\n\n\t\tGekaufte Artikel:\n");
        for (WarenkorbArtikel warenkorbArtikel : this.warenkorb.getWarenkorbArtikelList()) {
            var artikel = warenkorbArtikel.getArtikel();
            rechnung += ("\t\t\t-\t" + artikel.getBezeichnung() + ":\n\t\t\t\t\tx" + warenkorbArtikel.getAnzahl() +
                         "\tEinzelpreis: " + artikel.getPreis() + "€" +
                         "\n\t\t\t\t\t\tGesamtpreis: " + warenkorbArtikel.getGesamtPreis() + "€\n");
        }
        rechnung += ("\n\t\t\tGesamtsumme: " + warenkorb.getGesamtSumme() + "€\n");
        rechnung += ("\n\t\t\tVielen Dank für Ihren Einkauf!\n");
        rechnung += "\t" + StringUtils.lineSeparator(50);
        rechnung += "\n";
        return rechnung;
    }
}

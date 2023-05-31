package shop.entities;

import shop.utils.StringUtils;

import java.time.LocalDateTime;

public class Rechnung {
    private final Warenkorb warenkorb;
    private final Kunde kunde;
    private final LocalDateTime rechnungsDatum;
    private final int rechnungsNr;
    private final String rechnungsTitel;

    public Rechnung(Warenkorb warenkorb, Kunde kunde, int rechnungsNr) {
        this.warenkorb = warenkorb.deepCopy(); // erstellen einer Kopie des Warenkorbs für spätere Verwendung
        this.kunde = kunde;
        this.rechnungsDatum = LocalDateTime.now();
        this.rechnungsNr = rechnungsNr;
        this.rechnungsTitel = "Rechnung: " + rechnungsNr;
    }

    public Warenkorb getWarenkorb() {
        return warenkorb;
    }

    public Kunde getKunde() {
        return kunde;
    }

    public LocalDateTime getRechnungsDatum() {
        return rechnungsDatum;
    }

    public int getRechnungsNr() {
        return rechnungsNr;
    }

    public String getRechnungsTitel() {
        return rechnungsTitel;
    }

    @Override
    public String toString() {
        StringBuilder rechnung = new StringBuilder("\t" + StringUtils.lineSeparator(50));
        rechnung.append("\n")
                .append(StringUtils.tabulator(5)).append(rechnungsTitel).append("\n\n")
                .append(StringUtils.tabulator(2)).append("Kunde: ").append(kunde.getName())
                .append("\n\t\tAdresse: ").append(kunde.getAdresse())
                .append("\n\t\tNutzername: ").append(kunde.getNutzername())
                .append("\n\n\t\tGekaufte Artikel:\n");
        for (WarenkorbArtikel warenkorbArtikel : this.warenkorb.getWarenkorbArtikelList()) {
            var artikel = warenkorbArtikel.getArtikel();
            rechnung.append(StringUtils.tabulator(3))
                    .append("-\t").append(artikel.getBezeichnung()).append(":\n")
                    .append(StringUtils.tabulator(5))
                    .append("x").append(warenkorbArtikel.getAnzahl())
                    .append("\tEinzelpreis: ").append(artikel.getPreis()).append("€")
                    .append("\n").append(StringUtils.tabulator(6))
                    .append("Gesamtpreis: ").append(warenkorbArtikel.getGesamtPreis()).append("€\n");
        }
        rechnung.append("\n").append(StringUtils.tabulator(3))
                .append("Gesamtsumme: ").append(warenkorb.getGesamtSumme()).append("€\n")
                .append("\n").append(StringUtils.tabulator(3))
                .append("Rechnungsdatum: ").append(StringUtils.formatDate(this.rechnungsDatum)).append("\n")
                .append(StringUtils.tabulator(3))
                .append("Vielen Dank für Ihren Einkauf!\n")
                .append("\t")
                .append(StringUtils.lineSeparator(50))
                .append("\n");
        return rechnung.toString();
    }

    public Warenkorb warenkorb() {
        return warenkorb;
    }

    public Kunde kunde() {
        return kunde;
    }

}

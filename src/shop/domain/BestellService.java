package shop.domain;

import shop.entities.Artikel;
import shop.entities.Kunde;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BestellService {

    private final Kunde aktuellerKunde;

    public BestellService(Kunde aktuellerKunde) {
        this.aktuellerKunde = aktuellerKunde;
    }

    public void kaufen() {
        var warenkorbservice = WarenkorbService.getInstance();
        warenkorbservice.setAktuellerKunde(aktuellerKunde);
        rechnungerstellen(warenkorbservice);
        kaufenBestaetigen(warenkorbservice);
    }

    public void kaufenBestaetigen(WarenkorbService warenkorbservice) {
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();
        if (str.equals("ja")) {
            warenkorbservice.warenkorbLeeren();
        } else if (str.equals("nein")) {
            System.out.println("Kauf abgebrochen.");
        } else {
            System.out.println("Invalide Eingabe");
            kaufenBestaetigen(warenkorbservice);
        }
    }

    public void rechnungerstellen(WarenkorbService warenkorbservice) {
        List<Artikel> ArtikelListe = new ArrayList(warenkorbservice.getWarenkorb().getWarenkorbArtikelList());
        var iterator = ArtikelListe.iterator();
        float Gesamtpreis = 0;
        while (iterator.hasNext()) {
            var Artikel = iterator.next();
            System.out.println("Name: " + Artikel.getBezeichnung() + "  " + Artikel.getBestand() + "mal  Preis: " + Artikel.getPreis() + "€");
            Gesamtpreis += Artikel.getPreis();
        }
        System.out.println(" ");
        System.out.println("Gesamtpreis: " + Gesamtpreis + "€");
    }
}

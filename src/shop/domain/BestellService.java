package shop.domain;

import shop.domain.WarenkorbService;
import shop.entities.Kunde;
import shop.entities.Artikel;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
public class BestellService {

    private final Kunde aktuellerKunde;
    public BestellService(Kunde aktuellerKunde) {
        this.aktuellerKunde=aktuellerKunde;
    }

    public void kaufen() {
        var warenkorbservice = new WarenkorbService(this.aktuellerKunde);
        Rechnungerstellen(warenkorbservice);
        KaufenBestaetigen(warenkorbservice);
    }

    public void KaufenBestaetigen(WarenkorbService warenkorbservice) {
        Scanner sc= new Scanner(System.in);
        String str= sc.nextLine();
        if (str.equals("ja")) {
            warenkorbservice.WarenkorbLeeren();
        } else if (str.equals("nein")) {
            System.out.println("Kauf abgebrochen.");
        }else {
            System.out.println("Invalide Eingabe");
            KaufenBestaetigen(warenkorbservice);
        }
    }

    public void Rechnungerstellen(WarenkorbService warenkorbservice) {
        List<Artikel> ArtikelListe = new ArrayList(warenkorbservice.getWarenkorb().getWarenkorbArtikelList());
        var iterator = ArtikelListe.iterator();
        float Gesamtpreis = 0;
        while (iterator.hasNext()) {
            var Artikel = iterator.next();
            System.out.println("Name: " + Artikel.getBezeichnung() + "  " +  Artikel.getBestand() + "mal  Preis: " + Artikel.getPreis() + "€");
            Gesamtpreis+= Artikel.getPreis();
        }
        System.out.println(" ");
        System.out.println("Gesamtpreis: " + Gesamtpreis + "€");
    }
}

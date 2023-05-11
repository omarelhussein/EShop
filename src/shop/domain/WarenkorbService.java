package shop.domain;

import shop.domain.exceptions.warenkorb.IstKeinArtikelException;
import shop.entities.Artikel;
import shop.entities.Warenkorb;

import java.util.ArrayList;
import java.util.List;

public class WarenkorbService {
    List<Warenkorb> Koerbe = new ArrayList<>();

    private final ArtikelService artikelservice = new ArtikelService();
    private final Warenkorb warenkorb = new Warenkorb(1);
    private final Artikel artikels = new Artikel(1,"eins",1,1);


    public void addArtikelToWarenkorb(Artikel artikel, int KundenNr, int Anzahl) throws IstKeinArtikelException {
            if(artikel != null) {
                for (int i = 0; i < artikelservice.getArtList().size(); i++) {
                    if (artikel.getBestand() > Anzahl && Anzahl > 0) {
                        int k = 0;
                        for (int o = 0; i < Koerbe.size(); o++) {
                            if (KundenNr == Koerbe.get(o).getKundenNummer()) {

                                Koerbe.get(o).addArtikel(artikel);
                                k = 1;

                                artikel.setBestand(artikel.getBestand() - Anzahl);
                            }
                        }
                        if (k == 0) {
                            System.out.println("Es wurde kein Korb mit dieser Kundennummer gefunden.");
                        }
                    } else {
                        throw new IstKeinArtikelException("Artikel ist kein valider Artikel.");
                    }
                }
            }
            else {
                throw new IstKeinArtikelException("Artikel ist kein valider Artikel.");
            }
    }

    public void RemoveArtikelFromWarenkorb(Artikel artikel, int KundenNr,int Anzahl) throws IstKeinArtikelException {
        if(artikel != null) {
            for (int i = 0; i < artikelservice.getArtList().size(); i++) {
                if (artikel.getBestand() > Anzahl && Anzahl > 0) {
                    int k = 0;
                    for (Warenkorb value : Koerbe) {
                        if (KundenNr == value.getKundenNummer()) {

                            value.Artikelloeschen(artikel);
                            k = 1;

                            artikel.setBestand(artikel.getBestand() + Anzahl);
                        }
                    }
                    if (k == 0) {
                        System.out.println("Es wurde kein Korb mit dieser Kundennummer gefunden.");
                    }
                } else {
                    throw new IstKeinArtikelException("Artikel ist kein valider Artikel.");
                }
            }
        }
        else {
            throw new IstKeinArtikelException("Artikel ist kein valider Artikel.");
        }
    }

    public void NeuerKorb(int KundNummer) {
        Koerbe.add(new Warenkorb(KundNummer));
    }

    public List<Warenkorb> getKoerbe() {
        return Koerbe;
    }

}
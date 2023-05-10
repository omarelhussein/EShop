package shop.domain;

import shop.domain.exceptions.warenkorb.IstKeinArtikelException;
import shop.entities.Artikel;
import shop.domain.ArtikelService;
import shop.entities.Warenkorb;

import java.util.ArrayList;
import java.util.List;

public class WarenkorbService {
    List<Warenkorb> Koerbe = new ArrayList<>();
    private ArtikelService artikelservice = new ArtikelService();

    public void addArtikelToWarenkorb(Artikel artikel, int KundenNr) throws IstKeinArtikelException {
            if(artikel instanceof Artikel) {
                for (int i = 0; i < ArtikelService.getArtList(artikelservice).size(); i++) {
                    if (ArtikelService.getArtikelByArtNr(Artikel.getArtNr(artikel)) == artikel && Artikel.getBestand(ArtikelService.getArtikelByArtNr(Artikel.getArtNr(artikel))) > artikel.getBestand(artikel)) {
                        int k = 0;
                        for (int i = 0; i < Koerbe.size(); i++) {
                            if (KundenNr == Koerbe[i].KundenNummer) {

                                Koerbe[i].ArtikelImKorb.add(artikel);
                                k = 1;

                                ArtikelService.getArtikelByArtNr(Artikel.getArtNr(artikel)).setBestand(Artikel.getBestand(ArtikelService.getArtikelByArtNr(Artikel.getArtNr(artikel))) - artikel.getBestand(artikel))
                            }
                        }
                        if (k == 0) {
                            System.out.println("Es wurde kein Korb mit dieser Kundennummer gefunden.")
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

    public void RemoveArtikelFromWarenkorb(Artikel artikel, int KundenNr) throws IstKeinArtikelException {
        if(artikel instanceof Artikel) {
            for (int i = 0; i < ArtikelService.getArtList(artikelservice).size(); i++) {
                if (ArtikelService.getArtikelByArtNr(Artikel.getArtNr(artikel)) == artikel && Artikel.getBestand(ArtikelService.getArtikelByArtNr(Artikel.getArtNr(artikel))) > artikel.getBestand(artikel)) {
                    int k = 0;
                    for (int i = 0; i < Koerbe.size(); i++) {
                        if (KundenNr == Koerbe[i].KundenNummer) {

                            Koerbe[i].ArtikelImKorb.Artikelloeschen(artikel);
                            k = 1;

                            ArtikelService.getArtikelByArtNr(Artikel.getArtNr(artikel)).setBestand(Artikel.getBestand(ArtikelService.getArtikelByArtNr(Artikel.getArtNr(artikel))) + artikel.getBestand(artikel))
                        }
                    }
                    if (k == 0) {
                        System.out.println("Es wurde kein Korb mit dieser Kundennummer gefunden.")
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
        Koerbe[].add(new Warenkorb(KundNummer));
    }

    public Koerbe getKoerbe() {
        return Koerbe;
    }

}
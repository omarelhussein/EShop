package shop.domain;

import shop.domain.exceptions.warenkorb.IstKeinArtikelException;
import shop.entities.Artikel;

import java.util.ArrayList;
import java.util.List;

public class WarenkorbService {
    List<Artikel> Warenkorb = new ArrayList<>();

    public void addArtikelToWarenkorb(Artikel artikel) throws IstKeinArtikelException {
        if(artikel instanceof Artikel){
                Warenkorb.add(artikel);
        }
        else {
            throw new IstKeinArtikelException("Artikel ist kein valider Artikel.");
        }
    }
}

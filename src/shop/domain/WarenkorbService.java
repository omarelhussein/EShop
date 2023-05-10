package shop.domain;

import shop.domain.exceptions.warenkorb.IstKeinArtikelException;
import shop.entities.Artikel;

import java.util.ArrayList;
import java.util.List;

public class WarenkorbService {
    List<Artikel> Warenkorb = new ArrayList<>();

    public void addArtikelToWarenkorb(Artikel artikel) throws IstKeinArtikelException {
                Warenkorb.add(artikel);
    }


    public void removeArtikelFromWarenkorb(){

    }

    public void showWarenkorb(){

    }
}

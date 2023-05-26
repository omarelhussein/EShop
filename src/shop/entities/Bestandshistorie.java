package shop.entities;

import java.util.ArrayList;
import java.util.List;

public class Bestandshistorie {
    private Artikel artikel;
    private List<Integer> bestandshistoryList;
    private List<String> datum;

    public Bestandshistorie(Artikel artikel){
        this.artikel = artikel;
        this.bestandshistoryList = new ArrayList<>();
        this.datum = new ArrayList<>();
    }

    public void setBestandshistory(List historie){
        this.bestandshistoryList = historie;
    }

    public List<Integer> getBestandshistoryList(){
        return this.bestandshistoryList;
    }

    public void setArtikel(Artikel artikel){
        this.artikel = artikel;
    }

    public Artikel getArtikel(){
        return this.artikel;
    }

    public void setDatum(List<String> datum){
        this.datum = datum;
    }

    public List<String> getDatum(){
        return this.datum;
    }

    public void stringMachen(){
        for(int i = 0; i < getBestandshistoryList().size(); i++){
            System.out.println("Artikelbestand von Artikel: /ArtNr: " + getArtikel().getArtNr() + " / " + getArtikel().getBezeichnung() +
                    " / zu " + getBestandshistoryList().get(i).toString() + " Stk.        am " + getDatum().get(i) + " geändert");
        }
    }

}



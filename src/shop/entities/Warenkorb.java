package shop.entities;
import java.util.ArrayList;
import java.util.List;

public class Warenkorb {
    private int produkteInWarenkorb;
    private int preisIngesamt;
    final int KundenNummer;
    List<Artikel> ArtikelImKorb;

    public Warenkorb(int Nummer){
        KundenNummer = Nummer;
        List<Artikel> ArtikelImKorb = new ArrayList<>();
    }

    public void addArtikel(Artikel artikel) {
        this.ArtikelImKorb.add(artikel);
    }

    public void Artikelloeschen(Artikel artikel) {
        int k=0;
        for (int i=0; i< this.ArtikelImKorb.size(); i++) {
            if (artikel == this.ArtikelImKorb.get(i)) {
                this.ArtikelImKorb.remove(i);
                k=1;
            }
        }
        if (k==0) {
            System.out.println("Dieser Artikel befindet sich nicht im Warenkorb dieses Kunden.");
        }
    }

    public void setProdukteInWarenkorb(int eingabe){
        this.produkteInWarenkorb = eingabe;
    }

    public void setPreisIngesamt(int eingabe){
        this.preisIngesamt = eingabe;
    }

    public int getProdukteInWarenkorb(){ return this.produkteInWarenkorb;}

    public int getPreisIngesamt(){ return this.preisIngesamt;}

    public List<Artikel> getArtikelImKorb() {
        return this.ArtikelImKorb;
    }

    public int getKundenNummer() {return this.KundenNummer;}
}

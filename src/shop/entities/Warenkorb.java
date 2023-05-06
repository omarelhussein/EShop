package shop.entities;

public class Warenkorb {
    private int produkteInWarenkorb;
    private int preisIngesamt;
    public Warenkorb(){

    }

    public void setProdukteInWarenkorb(int eingabe){
        this.produkteInWarenkorb = eingabe;
    }

    public void setPreisIngesamt(int eingabe){
        this.preisIngesamt = eingabe;
    }

    public int getProdukteInWarenkorb(){ return this.produkteInWarenkorb;}

    public int getPreisIngesamt(){ return this.preisIngesamt;}
}

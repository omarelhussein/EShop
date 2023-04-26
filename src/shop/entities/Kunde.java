package shop.entities;

public class Kunde extends Person {

    private String adresse;

    public Kunde(int kundenNr, String name, String adresse) {       //Konstruktor
        super(kundenNr, name);
        this.adresse = adresse;
    }

    public void setAdresse(String adresse) {                        //Ein- & Ausgabe von Adresse
        this.adresse = adresse;
    }

    public String getAdresse() {
        return adresse;
    }

}

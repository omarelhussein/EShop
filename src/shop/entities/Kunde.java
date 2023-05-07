package shop.entities;

public class Kunde extends Person {

    private String adresse;

    public Kunde(int kundenNr, String name, String adresse, String passwort) {       //Konstruktor
        super(kundenNr, name, passwort);
        this.adresse = adresse;
    }

    public void setAdresse(String adresse) {                        //Ein- & Ausgabe von Adresse
        this.adresse = adresse;
    }

    public String getAdresse() {
        return adresse;
    }

}

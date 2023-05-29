package shop.entities;

public class Kunde extends Person {

    private Adresse adresse;

    public Kunde(int kundenNr, String email, String name, Adresse adresse, String passwort) {       //Konstruktor
        super(kundenNr, email, name, passwort);
        this.adresse = adresse;
    }

    public void setAdresse(Adresse adresse) {                        //Ein- & Ausgabe von Adresse
        this.adresse = adresse;
    }

    public Adresse getAdresse() {
        return this.adresse;
    }

}

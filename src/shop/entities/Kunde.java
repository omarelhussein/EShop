package shop.entities;

import java.io.Serializable;

public class Kunde extends Person {

    private Adresse adresse;

    public Kunde(int kundenNr, String nutzername, String name, Adresse adresse, String passwort) {
        super(kundenNr, nutzername, name, passwort);
        this.adresse = adresse;
    }

    public void setAdresse(Adresse adresse) {                        //Ein- & Ausgabe von Adresse
        this.adresse = adresse;
    }

    public Adresse getAdresse() {
        return this.adresse;
    }

}

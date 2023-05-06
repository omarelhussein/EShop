package shop.entities;

public class Kunde extends Person {

    private String adresse;

    public Kunde(int kundenNr, String name, String adresse, String nutzername, String passwort, String email) {
        super(kundenNr, name, nutzername, passwort, email);
        this.adresse = adresse;
    }

    public void setAdresse(String adresse) {                        //Ein- & Ausgabe von Adresse
        this.adresse = adresse;
    }

    public String getAdresse() {
        return adresse;
    }

}

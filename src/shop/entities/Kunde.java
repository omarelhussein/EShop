package shop.entities;

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

    public String toCSVString() {
        return getPersNr() + ";" + getNutzername() + ";" + getName() + ";" + getPasswort() + ";" +
               getAdresse().getStrasse() + ";" + getAdresse().getHausnummer() + ";" + getAdresse().getPlz() + ";" + getAdresse().getOrt();
    }

    @Override
    public void fromCSVString(String csv) {
        String[] tokens = csv.split(";");
        setPersNr(Integer.parseInt(tokens[0]));
        setNutzername(tokens[1]);
        setName(tokens[2]);
        setPasswort(tokens[3]);
        adresse = new Adresse(tokens[4], tokens[5], tokens[6], tokens[7]);
    }
}

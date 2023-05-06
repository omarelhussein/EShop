package shop.entities;

import shop.ui.cui.enums.AuswahlTyp;

public abstract class Person {

    private String name;
    private int persNr;
    private String nutzername;
    private String passwort;
    private String email;

    public Person(int persNr, String name, String nutzername, String passwort, String email) {
        this.name = name;
        this.persNr = persNr;
        this.nutzername = nutzername;
        this.passwort = passwort;
        this.email = email;
    }

    public void setName(String name) {                      //Ein- & Ausgabe
        this.name = name;
    }

    public void setPersNr(int persNr) {
        this.persNr = persNr;
    }

    public String getName() {
        return name;
    }

    public int getPersNr() {
        return persNr;
    }

    public String getEmail() {
        return email;
    }

    public String getNutzername() {
        return nutzername;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNutzername(String nutzername) {
        this.nutzername = nutzername;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    /**
     * Personen vergleichen
     *
     * @param obj das Objekt zum Vergleichen. Hier werden nur die Personalnummern verglichen.
     * @return true, wenn die Personen die gleiche Personalnummer haben, sonst false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person) {
            return this.persNr == ((Person) obj).persNr;
        }
        return false;
    }

    @Override
    public String toString() {
        return "PersNr: " + this.persNr + "\n" +
                "Name: " + this.name + "\n";
    }

    public AuswahlTyp getTyp() {
        if (this instanceof Mitarbeiter) {
            return AuswahlTyp.MITARBEITER;
        } else if (this instanceof Kunde) {
            return AuswahlTyp.KUNDE;
        } else {
            return AuswahlTyp.START;
        }
    }

}

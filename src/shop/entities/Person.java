package shop.entities;

import shop.ui.cui.enums.AuswahlTyp;

public abstract class Person {

    private String name;
    private int persNr;

    private String passwort;

    public Person(int persNr, String name, String passwort) {
        this.name = name;
        this.persNr = persNr;
        this.passwort = passwort;
    }

    public void setName(String name) {                      //Ein- & Ausgabe
        this.name = name;
    }

    public void setPersNr(int persNr) {
        this.persNr = persNr;
    }

    public void setPasswort(String passwort) { this.passwort = passwort; }

    public String getName() {
        return name;
    }

    public int getPersNr() {
        return persNr;
    }

    public String getPasswort() { return passwort;}



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

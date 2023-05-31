package shop.entities;

import shop.persistence.CSVSerializable;

import java.io.Serializable;

public abstract class Person implements Serializable, CSVSerializable {

    private String name;
    private int persNr;
    private String passwort;

    private String nutzername;

    public Person(int persNr, String nutzername, String name, String passwort) {
        this.name = name;
        this.persNr = persNr;
        this.passwort = passwort;
        this.nutzername = nutzername;
    }

    public Person() {
        // no-args constructor for serialization
    }

    public void setNutzername(String nutzername) {
        this.nutzername = nutzername;
    }

    public void setName(String name) {                      //Ein- & Ausgabe
        this.name = name;
    }

    public void setPersNr(int persNr) {
        this.persNr = persNr;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public String getNutzername() {
        return nutzername;
    }

    public String getName() {return name;}

    public int getPersNr() {
        return persNr;
    }

    public String getPasswort() {
        return passwort;
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
        return "PersNr: " + this.persNr + " / " +
                " Name: " + this.name + " / Nutzername: " + nutzername;
    }

}

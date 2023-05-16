package shop.entities;

public abstract class Person {

    private String name;
    private int persNr;
    private String passwort;

    private String email;

    public Person(int persNr, String email, String name, String passwort) {
        this.name = name;
        this.persNr = persNr;
        this.passwort = passwort;
        this.email = email;
    }

    public void setEmail(String eMail) {                      //Ein- & Ausgabe
        this.email = eMail;
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

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

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
                " Name: " + this.name + " / E-Mail: " + email;
    }

}

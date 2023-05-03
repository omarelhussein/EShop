package shop.entities;

public abstract class Person {

    private String name;
    private int persNr;

    public Person(int persNr, String name) {
        this.name = name;
        this.persNr = persNr;
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

}

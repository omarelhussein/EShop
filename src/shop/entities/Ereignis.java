package shop.entities;

import java.time.LocalDateTime;

public class Ereignis {

    private Person person;
    private Object object;
    private LocalDateTime datum;
    private EreignisArt ereignisArt;
    private String description;
    private int anzahl;

    public Ereignis(Person person, Object object, String description, LocalDateTime datum) {
        this.person = person;
        this.description = description;
        this.object = object;
        this.datum = datum;
    }

    public Ereignis(Person person, Object object, String description, EreignisArt ereignisArt, LocalDateTime datum) {
        this.person = person;
        this.description = description;
        this.object = object;
        this.ereignisArt = ereignisArt;
        this.datum = datum;
    }

    public int getPersNr() {
        return this.person.getPersNr();
    }

    public LocalDateTime getDatum() {
        return this.datum;
    }


    public String getName() {
        return this.person.getName();
    }

    public Object getObject() {
        return this.object;
    }

    public int getObjectBestand() {
        if (this.object instanceof Artikel) {
            return ((Artikel) this.object).getBestand();
        }
        return 0;
    }


    public void setObject(Object object) {
        this.object = object;
    }

    public Person getPerson() {
        return this.person;
    }

    public EreignisArt getEreignisArt() {
        return this.ereignisArt;
    }

    public void setEreignisArt(EreignisArt ereignisArt) {
        this.ereignisArt = ereignisArt;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return "Person: \"" + this.getName() + "\" mit der PersNr: " + this.getPersNr() + " " + this.getDescription();
    }
}


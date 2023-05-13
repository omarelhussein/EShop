package shop.entities;

import java.util.ArrayList;

public class Ereignis {

    private int persNr;
    private String name;

    private String description;

    public Ereignis(Person person, String description){
        this.persNr = person.getPersNr();
        this.name = person.getName();
        this.description = description;
    }
}

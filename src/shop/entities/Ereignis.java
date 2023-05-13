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

    public int getPersNr(){
        return this.persNr;
    }

    public void setPersNr(int persNr){
        this.persNr = persNr;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String Name){
        this.name = Name;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
    }

}


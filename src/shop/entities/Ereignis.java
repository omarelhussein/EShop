package shop.entities;

public class Ereignis {

    private Person person;
    private int persNr;
    private String name;

    private Object extraInfo;
    private String datum;
    private String ereignisArt;
    private String description;

    public Ereignis(Person person, Object object,  String description, String datum) {
        this.person = person;
        this.description = description;
        this.extraInfo = object;
        this.ereignisArt = "";
        this.datum = datum;

    }
    public Ereignis(Person person, Object object,  String description, String ereignisArt, String datum) {
        this.person = person;
        this.description = description;
        this.extraInfo = object;
        this.ereignisArt = ereignisArt;
        this.datum = datum;
    }

    public int getPersNr() {
        return this.person.getPersNr();
    }

    public String getDatum(){
        return this.datum;
    }


    public String getName() {
        return this.person.getName();
    }

    public Object getObject(){
        return this.extraInfo;
    }

    public void setObjectBestand(int bestand){
        if(this.extraInfo instanceof Artikel){
            ((Artikel) this.extraInfo).setBestand(bestand);
        }
    }
    public int getObjectBestand(){
        if(this.extraInfo instanceof Artikel){
            return ((Artikel) this.extraInfo).getBestand();
        }
        return 0;
    }

    public Bestandshistorie getBestandshistorie(){
        return ((Artikel) this.extraInfo).getBestandshistory();
    }
    public void setObject(Object object){
        this.extraInfo = object;
    }
    public Person getPerson(){
        return this.person;
    }

    public String getEreignisArt(){
        return this.ereignisArt;
    }
    public void setEreignisArt(String ereignisArt){
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


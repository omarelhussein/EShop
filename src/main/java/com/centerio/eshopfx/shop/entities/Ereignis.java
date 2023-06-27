package com.centerio.eshopfx.shop.entities;

import com.centerio.eshopfx.shop.domain.LogMessageGenerator;
import com.centerio.eshopfx.shop.entities.enums.EreignisTyp;
import com.centerio.eshopfx.shop.entities.enums.KategorieEreignisTyp;
import com.centerio.eshopfx.shop.persistence.CSVSerializable;
import com.centerio.eshopfx.shop.utils.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ereignis implements Serializable, CSVSerializable {
       private Person person;
       private Object object;
       private KategorieEreignisTyp kategorieEreignisTyp;
       private EreignisTyp ereignisTyp;
       private LocalDateTime datum;
       private boolean erfolg;
       private Integer bestand;

       public Ereignis(Person person, Object object, KategorieEreignisTyp kategorieEreignisTyp, EreignisTyp ereignisTyp, LocalDateTime datum, boolean erfolg){
           this.person = person;
           this.object = object;
           this.kategorieEreignisTyp = kategorieEreignisTyp;
           this.ereignisTyp = ereignisTyp;
           this.datum = datum;
           this.erfolg = erfolg;
       }

    public Ereignis(Person person, Object object, KategorieEreignisTyp kategorieEreignisTyp, EreignisTyp ereignisTyp, LocalDateTime datum, boolean erfolg, int bestand){
        this.person = person;
        this.object = object;
        this.kategorieEreignisTyp = kategorieEreignisTyp;
        this.ereignisTyp = ereignisTyp;
        this.datum = datum;
        this.erfolg = erfolg;
        this.bestand = bestand;
    }
    public Ereignis(){

    }

    public Object getObject() {
        return this.object;
    }

    public Person getPerson() {
        return this.person;
    }

    public int getPersNr(){
           return person.getPersNr();
    }

    public String getPersName(){
        return person.getName();
    }
    public String getArt(){
        return ereignisTyp.toString();
    }
    public String getDatumString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm'Uhr'");
                return datum.format(formatter);
    }

    public LocalDateTime getDatum() {
        return this.datum;
    }

    public String erfolgToString(){
           if(erfolg) {
               return "true";
           }
           else{
               return "false";
           }
    }

    public String kundeOderMitarbeiterCheck(){
           if (person instanceof Kunde){
               return "Kunde";
           } else return "Mitarbeiter";
    }

    public String artikelOderPersonCheck(){
        if(object instanceof Massenartikel){
            return "Massenartikel";
        }
           if(object instanceof Artikel){
               return "Artikel";
           }
           if(object instanceof Mitarbeiter){
               return "Mitarbeiter";
           }
           if(object instanceof Kunde){
               return "Kunde";
           }
           if(object instanceof String){
               return (String)object;
           }
           else return "null";
    }

    public String getBezug(){
           if(object instanceof Artikel){
               return "Artikel: " + ((Artikel)object).getBezeichnung();
           }
           if(object instanceof Person){
               return "Person: " + ((Person)object).getNutzername();
           }
           if(ereignisTyp == EreignisTyp.ARTIKEL_SUCHEN){
               return "Suchbegriff: " + (String)object;
           }
           return "";
    }

    public boolean stringToErfolg(String erfolgsString){
           if(erfolgsString.equals("true")){
               return true;
           } else return false;
    }

    public Integer getBestand(){
           if (bestand != null)
           return this.bestand;
           else return null;
    }

    public String getBestandString(){
           if(bestand == null){
               return "";
           } else {
               String stringBestand = "" + getBestand();
               return stringBestand;
           }
    }
    public KategorieEreignisTyp getKategorieEreignisTyp(){
           return this.kategorieEreignisTyp;
    }
    public EreignisTyp getEreignisTyp(){ return this.ereignisTyp;}
    @Override
    public String toString() {
        var builder = new StringBuilder();

        var nutzerString = person instanceof Kunde ? "Kunde" : "Mitarbeiter";
        String datumString = StringUtils.formatDate(datum);
        builder.append(nutzerString).append(" \"").append(person.getName()).append("\" mit PersNr ").append(person.getPersNr())
                .append(" hat Aktion \"").append(kategorieEreignisTyp.name()).append("\" am ").append(datumString).append(" ausgeführt. ");
        builder.append(LogMessageGenerator.generateLogMessage(this));
        builder.append(" [ Status der Abfrage: ").append(erfolg ? "Erfolgreich" : "Fehlgeschlagen").append(" ]");
        builder.append("\n").append(StringUtils.lineSeparator(20, " ~"));
        return builder.toString();
    }

    public String toCSVString() {
           if (bestand == null) {
               if(object instanceof Artikel) {
                   return  kundeOderMitarbeiterCheck() + ";" +
                           person.toCSVString() + ";" +
                           artikelOderPersonCheck() + ";" +
                           ((Artikel)object).toCSVString() + ";" +
                           kategorieEreignisTyp.toString() + ";" +
                           ereignisTyp.toString() + ";" +
                           datum.toString() + ";" +
                           erfolgToString();
               }
               if(object instanceof Kunde kunde) {
                   return  kundeOderMitarbeiterCheck() + ";" +
                           person.toCSVString() + ";" +
                           artikelOderPersonCheck() + ";" +
                           kunde.toCSVString() + ";" +
                           kategorieEreignisTyp.toString() + ";" +
                           ereignisTyp.toString() + ";" +
                           datum.toString() + ";" +
                           erfolgToString();
               }
               if(object instanceof Mitarbeiter mitarbeiter) {
                   return  kundeOderMitarbeiterCheck() + ";" +
                           person.toCSVString() + ";" +
                           artikelOderPersonCheck() + ";" +
                           mitarbeiter.toCSVString() + ";" +
                           kategorieEreignisTyp.toString() + ";" +
                           ereignisTyp.toString() + ";" +
                           datum.toString() + ";" +
                           erfolgToString();
               }
               return kundeOderMitarbeiterCheck() + ";" +
                       person.toCSVString() + ";" +
                       artikelOderPersonCheck() + ";" +
                       kategorieEreignisTyp.toString() + ";" +
                       ereignisTyp.toString() + ";" +
                       datum.toString() + ";" +
                       erfolgToString();

           } else {
               if(object instanceof Artikel) {
                   return  kundeOderMitarbeiterCheck() + ";" +
                           person.toCSVString() + ";" +
                           artikelOderPersonCheck() + ";" +
                           ((Artikel)object).toCSVString() + ";" +
                           kategorieEreignisTyp.toString() + ";" +
                           ereignisTyp.toString() + ";" +
                           datum.toString() + ";" +
                           erfolgToString() + ";" +
                           bestand.toString();

               }
               if(object instanceof Kunde kunde) {
                   return  kundeOderMitarbeiterCheck() + ";" +
                           person.toCSVString() + ";" +
                           artikelOderPersonCheck() + ";" +
                           kunde.toCSVString() + ";" +
                           kategorieEreignisTyp.toString() + ";" +
                           ereignisTyp.toString() + ";" +
                           datum.toString() + ";" +
                           erfolgToString() + ";" +
                           bestand.toString();
               }
               if(object instanceof Mitarbeiter mitarbeiter) {
                   return  kundeOderMitarbeiterCheck() + ";" +
                           person.toCSVString() + ";" +
                           artikelOderPersonCheck() + ";" +
                           mitarbeiter.toCSVString() + ";" +
                           kategorieEreignisTyp.toString() + ";" +
                           ereignisTyp.toString() + ";" +
                           datum.toString() + ";" +
                           erfolgToString()+ ";" +
                           bestand.toString();
               }
               return kundeOderMitarbeiterCheck() + ";" +
                       person.toCSVString() + ";" +
                       artikelOderPersonCheck() + ";" +
                       kategorieEreignisTyp.toString() + ";" +
                       ereignisTyp.toString() + ";" +
                       datum.toString() + ";" +
                       erfolgToString()+ ";" +
                       bestand.toString();
           }

    }

    public void fromCSVString(String csv) {
        String[] tokens = csv.split(";");
        if(tokens[0].equals("Kunde")){
            person = new Kunde(Integer.parseInt(tokens[1]), tokens[2], tokens[3], new Adresse(tokens[5], tokens[6], tokens[7], tokens[8]), tokens[4]);
            if(tokens[9].equals("Artikel")){
                object = new Artikel(Integer.parseInt(tokens[10]), tokens[11], Double.parseDouble(tokens[12]), Integer.parseInt(tokens[13]));
                kategorieEreignisTyp = KategorieEreignisTyp.valueOf(tokens[14]);
                ereignisTyp = EreignisTyp.valueOf(tokens[15]);
                datum = LocalDateTime.parse(tokens[16]);
                erfolg = stringToErfolg(tokens[17]);
                bestand = Integer.parseInt(tokens[18]);
                return;
            }
            if(tokens[9].equals("Massenartikel")){
                object = new Massenartikel(Integer.parseInt(tokens[10]), tokens[11], Double.parseDouble(tokens[12]), Integer.parseInt(tokens[13]), Integer.parseInt(tokens[14]));
                kategorieEreignisTyp = KategorieEreignisTyp.valueOf(tokens[15]);
                ereignisTyp = EreignisTyp.valueOf(tokens[16]);
                datum = LocalDateTime.parse(tokens[17]);
                erfolg = stringToErfolg(tokens[18]);
                bestand = Integer.parseInt(tokens[19]);
                return;
            }
            if(tokens[9].equals("Mitarbeiter")){
                object = new Mitarbeiter(Integer.parseInt(tokens[10]), tokens[11], tokens[12], tokens[13]);
                kategorieEreignisTyp = KategorieEreignisTyp.valueOf(tokens[14]);
                ereignisTyp = EreignisTyp.valueOf(tokens[15]);
                datum = LocalDateTime.parse(tokens[16]);
                erfolg = stringToErfolg(tokens[17]);
                return;
            }
            if(tokens[9].equals("Kunde")){
                object = new Kunde(Integer.parseInt(tokens[10]), tokens[11], tokens[12], new Adresse(tokens[14], tokens[15], tokens[16], tokens[17]),tokens[13]);
                kategorieEreignisTyp = KategorieEreignisTyp.valueOf(tokens[18]);
                ereignisTyp = EreignisTyp.valueOf(tokens[19]);
                datum = LocalDateTime.parse(tokens[20]);
                erfolg = stringToErfolg(tokens[21]);
                return;
            } else {
                object = tokens[9];
                kategorieEreignisTyp = KategorieEreignisTyp.valueOf(tokens[10]);
                ereignisTyp = EreignisTyp.valueOf(tokens[11]);
                datum = LocalDateTime.parse(tokens[12]);
                erfolg = stringToErfolg(tokens[13]);
                return;
            }
        } else {
            person = new Mitarbeiter(Integer.parseInt(tokens[1]), tokens[2], tokens[3], tokens[4]);
            System.out.println("Mitarbeiter ist drinne");
            if(tokens[5].equals("Artikel")){
                System.out.println("Artikel ist drinne");
                object = new Artikel(Integer.parseInt(tokens[6]), tokens[7], Double.parseDouble(tokens[8]), Integer.parseInt(tokens[9]));
                kategorieEreignisTyp = KategorieEreignisTyp.valueOf(tokens[10]);
                ereignisTyp = EreignisTyp.valueOf(tokens[11]);
                datum = LocalDateTime.parse(tokens[12]);
                erfolg = stringToErfolg(tokens[13]);
                bestand = Integer.parseInt(tokens[14]);
                return;
            }
            if(tokens[5].equals("Massenartikel")){
                object = new Massenartikel(Integer.parseInt(tokens[6]), tokens[7], Double.parseDouble(tokens[8]), Integer.parseInt(tokens[9]), Integer.parseInt(tokens[10]));
                kategorieEreignisTyp = KategorieEreignisTyp.valueOf(tokens[11]);
                ereignisTyp = EreignisTyp.valueOf(tokens[12]);
                datum = LocalDateTime.parse(tokens[13]);
                erfolg = stringToErfolg(tokens[14]);
                bestand = Integer.parseInt(tokens[15]);
                return;
            }
            if(tokens[5].equals("Mitarbeiter")){
                object = new Mitarbeiter(Integer.parseInt(tokens[6]), tokens[7], tokens[8], tokens[9]);
                kategorieEreignisTyp = KategorieEreignisTyp.valueOf(tokens[10]);
                ereignisTyp = EreignisTyp.valueOf(tokens[11]);
                datum = LocalDateTime.parse(tokens[12]);
                erfolg = stringToErfolg(tokens[13]);
                return;
            }
            if (tokens[5].equals("Kunde")){
                object = new Kunde(Integer.parseInt(tokens[6]), tokens[7], tokens[8], new Adresse(tokens[10], tokens[11], tokens[12], tokens[13]),tokens[9]);
                kategorieEreignisTyp = KategorieEreignisTyp.valueOf(tokens[14]);
                ereignisTyp = EreignisTyp.valueOf(tokens[15]);
                datum = LocalDateTime.parse(tokens[16]);
                erfolg = stringToErfolg(tokens[17]);
                return;
            } else {
                object = tokens[5];
                kategorieEreignisTyp = KategorieEreignisTyp.valueOf(tokens[6]);
                ereignisTyp = EreignisTyp.valueOf(tokens[7]);
                datum = LocalDateTime.parse(tokens[8]);
                erfolg = stringToErfolg(tokens[9]);
                return;
            }

        }

    }
}




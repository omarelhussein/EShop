package com.centerio.eshopfx.shop.entities;

import com.centerio.eshopfx.shop.domain.LogMessageGenerator;
import com.centerio.eshopfx.shop.entities.enums.EreignisTyp;
import com.centerio.eshopfx.shop.entities.enums.KategorieEreignisTyp;
import com.centerio.eshopfx.shop.persistence.CSVSerializable;
import com.centerio.eshopfx.shop.utils.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

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
        return object;
    }

    public Person getPerson() {
        return person;
    }

    public LocalDateTime getDatum() {
        return datum;
    }

    public Integer getBestand(){
           return bestand;
    }
    public KategorieEreignisTyp getKategorieEreignisTyp(){
           return kategorieEreignisTyp;
    }
    public EreignisTyp getEreignisTyp(){ return ereignisTyp;}
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
               return person + ";" + object + ";" + kategorieEreignisTyp + ";" + datum + ";" + erfolg;
           } else {
               return person + ";" + object + ";" + kategorieEreignisTyp + ";" + datum + ";" + erfolg + ";" + bestand ;
           }

    }

    public void fromCSVString(String csv) {
        String[] tokens = csv.split("#");
        String[] EreignisTokens = tokens[0].split(";");
    }
}




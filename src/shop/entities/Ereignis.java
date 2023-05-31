package shop.entities;

import shop.domain.LogMessageGenerator;
import shop.entities.enums.KategorieEreignisTyp;
import shop.utils.StringUtils;

import java.time.LocalDateTime;

public class Ereignis{
       private Person person;
       private Object object;
       private KategorieEreignisTyp kategorieEreignisTyp;
       private LocalDateTime datum;
       private boolean erfolg;
       private int bestand;

       public Ereignis(Person person, Object object, KategorieEreignisTyp kategorieEreignisTyp, LocalDateTime datum, boolean erfolg){
           this.person = person;
           this.object = object;
           this.kategorieEreignisTyp = kategorieEreignisTyp;
           this.datum = datum;
           this.erfolg = erfolg;
       }

    public Ereignis(Person person, Object object, KategorieEreignisTyp kategorieEreignisTyp, LocalDateTime datum, boolean erfolg, int bestand){
        this.person = person;
        this.object = object;
        this.kategorieEreignisTyp = kategorieEreignisTyp;
        this.datum = datum;
        this.erfolg = erfolg;
        this.bestand = bestand;
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

    public KategorieEreignisTyp getKategorieEreignisTyp(){
           return kategorieEreignisTyp;
    }
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

}


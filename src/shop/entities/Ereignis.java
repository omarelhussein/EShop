package shop.entities;

import shop.domain.LogMessageGenerator;
import shop.utils.StringUtils;

import java.time.LocalDateTime;

public record Ereignis(
        Person person,
        Object object,
        EreignisTyp ereignisTyp,
        LocalDateTime datum,
        boolean erfolg
) {
    @Override
    public String toString() {
        var builder = new StringBuilder();

        var nutzerString = person instanceof Kunde ? "Kunde" : "Mitarbeiter";
        String datumString = StringUtils.formatDate(datum);
        builder.append(nutzerString).append(" \"").append(person.getName()).append("\" mit PersNr ").append(person.getPersNr())
                .append(" hat Aktion \"").append(ereignisTyp.name()).append("\" am ").append(datumString).append(" ausgeführt. ");
        builder.append(LogMessageGenerator.generateLogMessage(this));
        builder.append(" [ Status der Abfrage: ").append(erfolg ? "Erfolgreich" : "Fehlgeschlagen").append(" ]");
        builder.append("\n").append(StringUtils.lineSeparator(20, " ~"));
        return builder.toString();
    }

}


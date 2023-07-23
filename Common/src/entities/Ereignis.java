package entities;

import entities.enums.EreignisTyp;
import entities.enums.KategorieEreignisTyp;
import persistence.CSVSerializable;
import utils.LogMessageGenerator;
import utils.StringUtils;

import java.io.IOException;
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

    public Ereignis(Person person, Object object, KategorieEreignisTyp kategorieEreignisTyp, EreignisTyp ereignisTyp, LocalDateTime datum, boolean erfolg) throws IOException {
        this.person = person;
        this.object = object;
        this.kategorieEreignisTyp = kategorieEreignisTyp;
        this.ereignisTyp = ereignisTyp;
        this.datum = datum;
        this.erfolg = erfolg;
    }

    public Ereignis(Person person, Object object, KategorieEreignisTyp kategorieEreignisTyp, EreignisTyp ereignisTyp, LocalDateTime datum, boolean erfolg, int bestand) throws IOException {
        this.person = person;
        this.object = object;
        this.kategorieEreignisTyp = kategorieEreignisTyp;
        this.ereignisTyp = ereignisTyp;
        this.datum = datum;
        this.erfolg = erfolg;
        this.bestand = bestand;
    }

    public Ereignis() throws IOException {

    }

    public Object getObject() {
        return this.object;
    }

    public Person getPerson() {
        return this.person;
    }

    public int getPersNr() {
        return person.getPersNr();
    }

    public String getPersName() {
        return person.getName();
    }

    public String getArt() {
        return ereignisTyp.toString();
    }

    public String getDatumString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm'Uhr'");
        return datum.format(formatter);
    }

    public LocalDateTime getDatum() {
        return this.datum;
    }

    public String erfolgToString() {
        if (erfolg) {
            return "true";
        } else {
            return "false";
        }
    }

    public String kundeOderMitarbeiterCheck() {
        if (person instanceof Kunde) {
            return "Kunde";
        } else return "Mitarbeiter";
    }

    public String artikelOderPersonCheck() {
        if (object instanceof Massenartikel) {
            return "Massenartikel";
        }
        if (object instanceof Artikel) {
            return "Artikel";
        }
        if (object instanceof Mitarbeiter) {
            return "Mitarbeiter";
        }
        if (object instanceof Kunde) {
            return "Kunde";
        }
        if (object instanceof String) {
            return (String) object;
        } else return "null";
    }

    public String getBezug() {
        if (object instanceof Artikel) {
            return "Artikel: " + ((Artikel) object).getBezeichnung();
        }
        if (object instanceof Person) {
            return "Person: " + ((Person) object).getNutzername();
        }
        if (ereignisTyp == EreignisTyp.ARTIKEL_SUCHEN) {
            return "Suchbegriff: " + object;
        }
        return "";
    }

    public boolean stringToErfolg(String erfolgsString) {
        if (erfolgsString.equals("true")) {
            return true;
        } else return false;
    }

    public Integer getBestand() {
        if (bestand != null)
            return this.bestand;
        else return null;
    }

    public String getBestandString() {
        if (bestand == null) {
            return "";
        } else {
            return String.valueOf(getBestand());
        }
    }

    public KategorieEreignisTyp getKategorieEreignisTyp() {
        return this.kategorieEreignisTyp;
    }

    public EreignisTyp getEreignisTyp() {
        return this.ereignisTyp;
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

    public String toCSVString() {
        String ereignisCSV = "";
        ereignisCSV = person.getPersNr() + ";" +
                      artikelOderPersonCheck() + ";";
        if (object instanceof Artikel) {
            ereignisCSV = ereignisCSV + ((Artikel) object).getArtNr() + ";";
        }
        if (object instanceof Person) {
            ereignisCSV = ereignisCSV + ((Person) object).getPersNr() + ";";
        }
        if (!(object instanceof Artikel) && !(object instanceof Person)) {
            ereignisCSV = ereignisCSV + null + ";";
        }
        ereignisCSV = ereignisCSV + kategorieEreignisTyp.toString() + ";" +
                      ereignisTyp.toString() + ";" +
                      datum.toString() + ";" +
                      erfolgToString() + ";" +
                      getBestand();

        return ereignisCSV;
    }

    public void fromCSVString(String csv) {
        String[] tokens = csv.split(";");
        /*try { // FIXME : Services können hier nicht verwendet werden!
            person = PersonenService.getInstance().getPersonByPersNr(Integer.parseInt(tokens[0]));
            if (tokens[1].equals("Artikel") || tokens[1].equals("Massenartikel")) {
                object = ArtikelService.getInstance().getArtikelByArtNr(Integer.parseInt(tokens[2]));
            }
            if (tokens[1].equals("Kunde") || tokens[1].equals("Mitarbeiter")) {
                object = PersonenService.getInstance().getPersonByPersNr(Integer.parseInt(tokens[2]));
            }
            if (tokens[1].equals("null")) {
                object = null;
            }
        } catch (IOException | ArtikelNichtGefundenException e) {
            System.out.println("IOException in Ereignisse");
        }*/
        kategorieEreignisTyp = KategorieEreignisTyp.valueOf(tokens[3]);
        ereignisTyp = EreignisTyp.valueOf(tokens[4]);
        datum = LocalDateTime.parse(tokens[5]);
        erfolg = stringToErfolg(tokens[6]);
        if (!(tokens[7].equals("null")))
            bestand = Integer.parseInt(tokens[7]);

    }

}




package shop.domain;

import shop.entities.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EreignisService {

    private static EreignisService ereignisService;

    private final ArrayList<Ereignis> ereignisList;

    private Person person;

    private EreignisService() {
        ereignisList = new ArrayList<>();
        person = new Mitarbeiter(0, "system", "system", "system");
    }

    public static synchronized EreignisService getInstance() {
        if (ereignisService == null) {
            ereignisService = new EreignisService();
        }
        // default person if non specified
        return ereignisService;
    }

    public void artikelAddEreignis(Artikel artikel) {
        String description = "hat " + artikel.toString() + " hinzugefügt."; //placeholder
        addEreignis(person, description);
    }

    public void artikelRemoveEreignis(Artikel artikel) {
        String description = "hat " + artikel.getArtNr() +
                " mit der Bezeichnung " + artikel.getBezeichnung() + " entfernt."; //placeholder
        addEreignis(person, description);
    }

    public void getArtikelListEreignis() {
        String description = "hat Artikelliste ausgegeben."; //placeholder
        addEreignis(person, description);
    }

    public void sucheArtikelByArtQueryEreignis(String query) {
        String description = "hat Artikel mit dem Suchbegriff " + query + " gesucht."; //placeholder
        addEreignis(person, description);
    }

    public void getLoginEreignis() {
        String description = "hat sich eingeloggt";
        addEreignis(person, description);
    }

    private void addEreignis(Person person, String description) {
        var beschreibungMitDatum = description + " Ereignis wurde am "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'"))
                + " erstellt.";
        Ereignis ereignis = new Ereignis(person, beschreibungMitDatum);
        ereignisList.add(ereignis);
    }

    public ArrayList<Ereignis> kundeOderMitarbeiterEreignisListe() {
        if (person instanceof Kunde) {
            ArrayList<Ereignis> kundenEreignisListe = new ArrayList<>();
            for (Ereignis ereignis : getEreignisList()) {
                if (ereignis.getPersNr() == person.getPersNr()) {
                    kundenEreignisListe.add(ereignis);
                }
            }
            return kundenEreignisListe;
        } else {
            return getEreignisList();
        }
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public ArrayList<Ereignis> getEreignisList() {
        return ereignisList;
    }
}

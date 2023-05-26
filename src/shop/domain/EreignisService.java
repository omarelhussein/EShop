package shop.domain;

import shop.entities.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
        String description = "hat " + artikel.toString() + " hinzugefügt"; //placeholder
        Ereignis ereignis = addEreignis(person, artikel, description, "Bestandveraenderung");
    }

    public void artikelRemoveEreignis(Artikel artikel) {
        String description = "hat " + artikel.getArtNr() +
                " mit der Bezeichnung " + artikel.getBezeichnung() + " entfernt"; //placeholder
        Ereignis ereignis = addEreignis(person, artikel, description);
    }

    public void getArtikelListEreignis(Object artikelListe) {
        String description = "hat Artikelliste ausgegeben"; //placeholder
        addEreignis(person, artikelListe, description);
    }

    public void sucheArtikelByArtQueryEreignis(Object suchErgebnis, String query) {
        String description = "hat Artikel mit dem Suchbegriff " + query + " gesucht"; //placeholder
        addEreignis(person, suchErgebnis, description);
    }

    public void loginEreignis(Object loginStatus) {
        String description = "hat sich eingeloggt";
        addEreignis(person, loginStatus, description);
    }

    private Ereignis addEreignis(Person person, Object obj, String description) {
        String datum = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'"));
        var beschreibungMitDatum = description + ". Ereignis wurde am " + datum + " erstellt.";
        Ereignis ereignis = new Ereignis(person, obj, beschreibungMitDatum, datum);
        ereignisList.add(ereignis);
        BestandshistorieService.getInstance().addBestandHistorie();
        return ereignis;
    }

    private Ereignis addEreignis(Person person, Object obj, String description, String ereignisArt) {
        String datum = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'"));
        var beschreibungMitDatum = description + ". Ereignis wurde am " + datum + " erstellt.";
        Ereignis ereignis = new Ereignis(person, obj, beschreibungMitDatum, ereignisArt, datum);
        ereignisList.add(ereignis);
        BestandshistorieService.getInstance().addBestandHistorie();
        return ereignis;
    }

    public ArrayList<Ereignis> kundeOderMitarbeiterEreignisListe() {
        if (person instanceof Kunde) {
            ArrayList<Ereignis> kundenEreignisListe = new ArrayList<>();
            for (Ereignis ereignis : getEreignisList()) {
                if (ereignis.getPerson().equals(person)) {
                    kundenEreignisListe.add(ereignis);
                }
            }
            return kundenEreignisListe;
        } else {
            if(person instanceof Mitarbeiter){
                ArrayList<Ereignis> mitarbeiterEreignisListe = new ArrayList<>();
                for(Ereignis ereignis : getEreignisList()) {
                    if (ereignis.getPerson() instanceof Mitarbeiter) {
                        mitarbeiterEreignisListe.add(ereignis);
                    }
                }
                return mitarbeiterEreignisListe;
            }
            return getEreignisList();
        }
    }

    public void warenkorbAusgabeEreignis(Object warenkorb){
        String description = "hat seinen Warenkorb ausgegeben";
        addEreignis(person, warenkorb, description);
    }

    public void warenkorbArtikelAnzahlEreignis(Object warenkorb){
        String description = "hat Artikelanzahl im Warenkorb verändert";
        addEreignis(person, warenkorb, description);
    }

    public void addArtikelWarenkorbEreignis(Object artikel){
        String description = "hat einen Artikel zum Warenkorb hinzugefügt";
        addEreignis(person, artikel, description);
    }

    public void mitarbeiterAusgebenEreignis(List<Mitarbeiter> mitarbeiterListe){
        String description = "hat Mitarbeiterliste ausgegeben";
        addEreignis(person, mitarbeiterListe, description);
    }

    public void mitarbeiterSuchenEreignis(List<Mitarbeiter> gesuchterMitarbeiter, String suchbegriff){
        String description = "hat " + suchbegriff + "über Mitarbeitersuche gesucht";
        addEreignis(person, gesuchterMitarbeiter, description);
    }

    public void mitarbeiterLoeschenEreignis(int mitarbeiterId){
        String description = "hat Mitarbeiter mit der ID: " + mitarbeiterId + "gelöscht";
        addEreignis(person, mitarbeiterId, description);
    }

    public void bestandAenderungEreignis(Artikel artikel){
        String description = "hat den Bestand von Artikel " + artikel.getArtNr() + " " + artikel.getBezeichnung() + "auf " + artikel.getBestand() + "geaendert";
        Ereignis ereignis = addEreignis(person, artikel, description, "Bestandveraenderung");
    }

    public void ereignislistAusgabeEreignis(List<Ereignis> liste){
        String description = "hat die EreignisListe ausgegeben";
        addEreignis(person, liste, description);
    }

    public void gekauftEreignis(List<Artikel> artikelListe){
        String description = "hat gekauft";
        Ereignis ereignis = addEreignis(person, artikelListe, description, "BestandUpdate");
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public ArrayList<Ereignis> getEreignisList() {
        return ereignisList;
    }
}

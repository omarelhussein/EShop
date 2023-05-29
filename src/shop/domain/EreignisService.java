package shop.domain;

import shop.entities.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EreignisService {
    private static EreignisService ereignisService;

    private final ArrayList<Ereignis> ereignisList;


    private EreignisService() {
        ereignisList = new ArrayList<>();
    }

    public static synchronized EreignisService getInstance() {
        if (ereignisService == null) {
            ereignisService = new EreignisService();
        }
        // default person if non specified
        return ereignisService;
    }

    public void artikelAddEreignis(Artikel artikel) {
        String description = "hat " + artikel.toString() + " hinzugefügt";
        addEreignis(artikel, description, EreignisArt.BESTAND_VERAENDERUNG);
    }

    public void artikelRemoveEreignis(Artikel artikel) {
        String description = "hat " + artikel.getArtNr() +
                " mit der Bezeichnung " + artikel.getBezeichnung() + " entfernt";
        addEreignis(artikel, description);
    }

    public void getArtikelListEreignis(Object artikelListe) {
        String description = "hat Artikelliste ausgegeben"; //placeholder
        addEreignis(artikelListe, description);
    }

    public void sucheArtikelByArtQueryEreignis(Object suchErgebnis, String query) {
        String description = "hat Artikel mit dem Suchbegriff " + query + " gesucht"; //placeholder
        addEreignis(suchErgebnis, description);
    }

    public void loginEreignis(Object loginStatus) {
        String description = "hat sich eingeloggt";
        addEreignis(loginStatus, description);
    }

    private Ereignis addEreignis(Object obj, String beschreibung) {
        return addEreignis(obj, beschreibung, null);
    }

    private Ereignis addEreignis(Object obj, String beschreibung, EreignisArt ereignisArt) {
        var now = LocalDateTime.now();
        String datum = now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'"));
        var beschreibungMitDatum = beschreibung + ". Ereignis wurde am " + datum + " erstellt.";
        var user = UserContext.getUser();
        if (user == null) { // at application start, no user is logged in, but we still want to log events
            user = new Mitarbeiter(1, "system", "system", "system");
        }
        Ereignis ereignis = new Ereignis(user, obj, beschreibungMitDatum, ereignisArt, now);
        ereignisList.add(ereignis);
        BestandshistorieService.getInstance().addBestandshistorie(ereignis);
        return ereignis;
    }

    public ArrayList<Ereignis> kundeOderMitarbeiterEreignisListe() {
        if (UserContext.getUser() instanceof Kunde) {
            ArrayList<Ereignis> kundenEreignisListe = new ArrayList<>();
            for (Ereignis ereignis : getEreignisList()) {
                if (ereignis.getPerson().equals(UserContext.getUser())) {
                    kundenEreignisListe.add(ereignis);
                }
            }
            return kundenEreignisListe;
        } else {
            if (UserContext.getUser() instanceof Mitarbeiter) {
                ArrayList<Ereignis> mitarbeiterEreignisListe = new ArrayList<>();
                for (Ereignis ereignis : getEreignisList()) {
                    if (ereignis.getPerson() instanceof Mitarbeiter) {
                        mitarbeiterEreignisListe.add(ereignis);
                    }
                }
                return mitarbeiterEreignisListe;
            }
            return getEreignisList();
        }
    }

    public void warenkorbAusgabeEreignis(Object warenkorb) {
        String description = "hat seinen Warenkorb ausgegeben";
        addEreignis(warenkorb, description);
    }

    public void warenkorbArtikelAnzahlEreignis(Object warenkorb) {
        String description = "hat Artikelanzahl im Warenkorb verändert";
        addEreignis(warenkorb, description);
    }

    public void addArtikelWarenkorbEreignis(Object artikel) {
        String description = "hat einen Artikel zum Warenkorb hinzugefügt";
        addEreignis(artikel, description);
    }

    public void mitarbeiterAusgebenEreignis(List<Mitarbeiter> mitarbeiterListe) {
        String description = "hat Mitarbeiterliste ausgegeben";
        addEreignis(mitarbeiterListe, description);
    }

    public void mitarbeiterSuchenEreignis(List<Mitarbeiter> gesuchterMitarbeiter, String suchbegriff) {
        String description = "hat " + suchbegriff + "über Mitarbeitersuche gesucht";
        addEreignis(gesuchterMitarbeiter, description);
    }

    public void mitarbeiterLoeschenEreignis(int mitarbeiterId) {
        String description = "hat Mitarbeiter mit der ID: " + mitarbeiterId + "gelöscht";
        addEreignis(mitarbeiterId, description);
    }

    public void bestandAenderungEreignis(Artikel artikel) {
        String description = "hat den Bestand von Artikel " + artikel.getArtNr() + " " + artikel.getBezeichnung() + "auf " + artikel.getBestand() + "geaendert";
        addEreignis(artikel, description, EreignisArt.BESTAND_VERAENDERUNG);
    }

    public void ereignislistAusgabeEreignis(List<Ereignis> liste) {
        String description = "hat die EreignisListe ausgegeben";
        addEreignis(liste, description);
    }

    public void gekauftEreignis(List<Artikel> artikelListe) {
        String description = "hat gekauft";
        addEreignis(artikelListe, description, EreignisArt.KAUF);
    }

    public ArrayList<Ereignis> getEreignisList() {
        return ereignisList;
    }
}

package com.centerio.eshopfx.shop.domain;

import com.centerio.eshopfx.shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.entities.*;
import com.centerio.eshopfx.shop.entities.enums.EreignisTyp;
import com.centerio.eshopfx.shop.entities.enums.KategorieEreignisTyp;
import com.centerio.eshopfx.shop.persistence.FilePersistenceManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class HistorienService {
    private static HistorienService historienService;
    private final ArrayList<Ereignis> ereignisList;

    private FilePersistenceManager<Ereignis> persistenceManager;

    private static final int STANDARD_TAGE_ZURUECK = 30;

    private HistorienService() throws IOException {
        persistenceManager = new FilePersistenceManager<>("ereignis.csv");
        // FIXME: Wird nicht richtig gespeichert und auch nicht richtig gelesen
        //  Führt zu NullPointerExceptions
        ereignisList = (ArrayList<Ereignis>)persistenceManager.readAll();
//        ereignisList = new ArrayList<>();
    }

    /**
     * Gibt die Singleton-Instanz von der Klasse zurück.
     * Wenn die Instanz noch nicht erstellt wurde, wird sie initialisiert.
     * Ein Singleton ist ein Entwurfsmuster, das sicherstellt, dass von einer Klasse nur
     * eine Instanz erstellt wird und einen globalen Zugriffspunkt zu dieser Instanz bereitstellt.
     * Es ist nützlich für Ressourcen, von denen nur eine einzige Instanz benötigt wird, wie
     * Dienste, Manager oder Datenbankzugriffe.
     */
    public static synchronized HistorienService getInstance() throws IOException {
        if (historienService == null) {
            historienService = new HistorienService();
        }
        return historienService;
    }

    public void addEreignis(KategorieEreignisTyp ereignisKategorie, EreignisTyp ereignisTyp, Object obj, boolean erfolg) throws IOException {
        var user = UserContext.getUser();
        if (user == null) { // at application start, no user is logged in, but we still want to log events
            user = new Mitarbeiter(1, "system", "system", "system");
        }
        if (ereignisKategorie == KategorieEreignisTyp.ARTIKEL_EREIGNIS && obj instanceof Artikel) {
            Ereignis ereignis = new Ereignis(user, obj, ereignisKategorie, ereignisTyp, LocalDateTime.now(), erfolg, ((Artikel) obj).getBestand());
            ereignisList.add(ereignis);
        } else {
            Ereignis ereignis = new Ereignis(user, obj, ereignisKategorie, ereignisTyp, LocalDateTime.now(), erfolg);
            ereignisList.add(ereignis);
        }
    }

    public void addEreignis(KategorieEreignisTyp ereignisKategorie, EreignisTyp ereignisTyp, Object obj, boolean erfolg, int anzahl) throws IOException {
        var user = UserContext.getUser();
        if (user == null) { // at application start, no user is logged in, but we still want to log events
            user = new Mitarbeiter(1, "system", "system", "system");
        }
        if (ereignisKategorie == KategorieEreignisTyp.WARENKORB_EREIGNIS && obj instanceof Artikel) {
            Ereignis ereignis = new Ereignis(user, obj, ereignisKategorie, ereignisTyp, LocalDateTime.now(), erfolg, anzahl);
            ereignisList.add(ereignis);
        }
    }
    public List<Ereignis> getUngefiltertPersonEreignishistorie() {
        List<Ereignis> personhistorie = new ArrayList<>();
        for (Ereignis ereignis : ereignisList) {
            EreignisTyp personEreignisTyp = ereignis.getEreignisTyp();
            if (ereignis.getKategorieEreignisTyp() == KategorieEreignisTyp.PERSONEN_EREIGNIS && ereignis.getObject() instanceof Person
                && (personEreignisTyp == EreignisTyp.MITARBEITER_ANLEGEN ||
                    personEreignisTyp == EreignisTyp.MITARBEITER_LOESCHEN ||
                    personEreignisTyp == EreignisTyp.MITARBEITER_SUCHEN ||
                    personEreignisTyp == EreignisTyp.MITARBEITER_ANZEIGEN)) {
                personhistorie.add(ereignis);
            }
        }
        return personhistorie;
    }

    public List<Ereignis> getUngefiltertArtikelEreignishistorie() {
        List<Ereignis> artikelhistorie = new ArrayList<>();
        for (Ereignis ereignis : ereignisList) {
            EreignisTyp artikelEreignisTyp = ereignis.getEreignisTyp();
            if (ereignis.getKategorieEreignisTyp() == KategorieEreignisTyp.ARTIKEL_EREIGNIS && ereignis.getObject() instanceof Artikel
                && (artikelEreignisTyp == EreignisTyp.ARTIKEL_ANLEGEN ||
                    artikelEreignisTyp == EreignisTyp.ARTIKEL_AKTUALISIEREN ||
                    artikelEreignisTyp == EreignisTyp.ARTIKEL_LOESCHEN ||
                    artikelEreignisTyp == EreignisTyp.BESTANDAENDERUNG ||
                    artikelEreignisTyp == EreignisTyp.KAUF)) {
                artikelhistorie.add(ereignis);
            }
        }
        return artikelhistorie;
    }

    public List<Ereignis> getUngefiltertWarenkorbEreignishistorie() {
        List<Ereignis> warenkorbhistorie = new ArrayList<>();
        for (Ereignis ereignis : ereignisList) {
            EreignisTyp artikelEreignisTyp = ereignis.getEreignisTyp();
            if (ereignis.getKategorieEreignisTyp() == KategorieEreignisTyp.WARENKORB_EREIGNIS
                    && (artikelEreignisTyp == EreignisTyp.WARENKORB_AENDERN ||
                    artikelEreignisTyp == EreignisTyp.WARENKORB_ANZEIGEN ||
                    artikelEreignisTyp == EreignisTyp.WARENKORB_HINZUFUEGEN)) {
                warenkorbhistorie.add(ereignis);
            }
        }
        return warenkorbhistorie;
    }

    public List<Ereignis> suchPersonhistorie(int persNr, int tage, Person person) throws ArtikelNichtGefundenException, IOException {
        List<Ereignis> personhistorie = new ArrayList<>();
        var neueTage = ueberpruefeTage(tage);
        for (Ereignis ereignis : ereignisList) {
            if (ereignis.getPerson().equals(person) && ereignis.getDatum().isAfter(ereignis.getDatum().minus(neueTage, ChronoUnit.DAYS))) {
                personhistorie.add(ereignis);
            }
        }
        return personhistorie;
    }

    public List<Ereignis> suchBestandshistorie(int artNr, int tage, Boolean istKaufFilter) throws ArtikelNichtGefundenException, IOException {
        var artikel = ArtikelService.getInstance().getArtikelByArtNr(artNr);
        var neueTage = ueberpruefeTage(tage);
        List<Ereignis> betroffeneArtikelEreignisList = new ArrayList<>();
        for (Ereignis ereignis : getUngefiltertArtikelEreignishistorie()) {
            if (ereignis.getObject().equals(artikel) && ereignis.getDatum().isAfter(ereignis.getDatum().minus(neueTage, ChronoUnit.DAYS))) {
                betroffeneArtikelEreignisList.add(ereignis);
            }
        }
        return betroffeneArtikelEreignisList;
    }

    private int ueberpruefeTage(int tage) {
        if (tage <= 0) tage = STANDARD_TAGE_ZURUECK;
        return tage;
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

    public void save() throws IOException {
        persistenceManager.replaceAll(ereignisList);
    }

    public ArrayList<Ereignis> getEreignisList() {
        return ereignisList;
    }

}

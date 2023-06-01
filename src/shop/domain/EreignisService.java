package shop.domain;

import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.entities.*;
import shop.entities.enums.EreignisTyp;
import shop.entities.enums.KategorieEreignisTyp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class EreignisService {
    private static EreignisService ereignisService;
    private final ArrayList<Ereignis> ereignisList;
    private final ArrayList<BestandshistorieItem> bestandshistorieItemsList;

    private static final int STANDARD_TAGE_ZURUECK = 30;

    private EreignisService() {
        ereignisList = new ArrayList<>();
        bestandshistorieItemsList = new ArrayList<>();
    }

    /**
     * Gibt die Singleton-Instanz von der Klasse zurück.
     * Wenn die Instanz noch nicht erstellt wurde, wird sie initialisiert.
     * Ein Singleton ist ein Entwurfsmuster, das sicherstellt, dass von einer Klasse nur
     * eine Instanz erstellt wird und einen globalen Zugriffspunkt zu dieser Instanz bereitstellt.
     * Es ist nützlich für Ressourcen, von denen nur eine einzige Instanz benötigt wird, wie
     * Dienste, Manager oder Datenbankzugriffe.
     */
    public static synchronized EreignisService getInstance() {
        if (ereignisService == null) {
            ereignisService = new EreignisService();
        }
        return ereignisService;
    }

    public void addEreignis(KategorieEreignisTyp ereignisKategorie, EreignisTyp ereignisTyp, Object obj, boolean erfolg) {
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

    public List<Ereignis> suchPersonhistorie(Person person) throws ArtikelNichtGefundenException, IOException {
        List<Ereignis> personhistorie = new ArrayList<>();
        for (Ereignis ereignis : ereignisList) {
            if (ereignis.getPerson().equals(person)) {
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

    public ArrayList<Ereignis> getEreignisList() {
        return ereignisList;
    }

}

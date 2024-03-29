package domain;


import entities.Kunde;
import entities.Mitarbeiter;
import entities.Person;
import entities.enums.EreignisTyp;
import entities.enums.KategorieEreignisTyp;
import exceptions.personen.PasswortNameException;
import exceptions.personen.PersonNichtGefundenException;
import exceptions.personen.PersonVorhandenException;
import persistence.FilePersistenceManager;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class PersonenService {

    private List<Person> personList;
    private final WarenkorbService warenkorbService;
    private final FilePersistenceManager<Person> persistenceManager;

    private static PersonenService instance;

    private PersonenService() throws IOException {
        warenkorbService = WarenkorbService.getInstance();
        persistenceManager = new FilePersistenceManager<>("personen.csv");
        if (personList == null || personList.isEmpty()) {
            personList = persistenceManager.readAll();
        }
    }

    public synchronized static PersonenService getInstance() throws IOException {
        if (instance == null) {
            instance = new PersonenService();
        }
        return instance;
    }

    /**
     * Überprüft mithilfe einer for-Schleife alle Personen in der Personenliste,
     * ob der eingegebene Nutzername mit der einer registrierten Person übereinstimmt, falls der Nutzername mit der
     * einer registrierten Person übereinstimmt wird das eingegebene Passwort mit der Person überprüft
     * trifft Nutzername und Passwort zu wird "true" return ansonsten false
     */
    public Person login(String nutzername, String passwort) throws PasswortNameException, IOException {
        for (Person person : personList) {
            if (nutzername.equalsIgnoreCase(person.getNutzername()) && passwort.equals(person.getPasswort())) {
                return person;
            }
        }
        throw new PasswortNameException();
    }

    /**
     * Registriert eine Person. Überprüft, ob der eingegebene Nutzername oder KundenNr bereits
     * vergeben ist, wenn die KundenNr oder Nutzername vergeben ist, wird eine Exception geworfen,
     * ansonsten wird ein neues KundenObjekt mit den eingegebenen Parametern erstellt und in
     * die PersonenListe hinzugefügt.
     *
     * @param person ist ein Objekt der Klasse {@link Person} und enthält die Attribute der zu registrierenden Person,
     *               welches über eine der Kindklassen {@link entities.Kunde} oder {@link entities.Mitarbeiter} erstellt wird.
     */
    public Person registerPerson(Person person) throws PersonVorhandenException {
        for (Person aktuellePerson : personList) {
            if (aktuellePerson.getNutzername().trim().equalsIgnoreCase(person.getNutzername().trim())) {
                throw new PersonVorhandenException();
            }
        }
        personList.add(person);
        // Wenn die Person ein Kunde ist, wird ein neuer Warenkorb erstellt
        if (person instanceof Kunde kunde) {
            warenkorbService.neuerKorb(kunde);
        }
        return person;
    }

    /**
     * gibt die Person mit der angegebenen Personennummer aus der Personenliste wieder
     * @param persNr
     * @return
     */
    public Person getPersonByPersNr(int persNr) {
        for (Person person : personList) {
            if (person.getPersNr() == persNr) {
                return person;
            }
        }
        return null;
    }

    /**
     * Überprüft, ob die eingegebene Person ein Mitarbeiter ist, falls nein wird eine Exception geworfen,
     * sonst wird der Mitarbeiter aus der Personen-Liste entfernt
     */
    public void removeMitarbeiter(int mitarbeiterNr) throws PersonNichtGefundenException, IOException {
        var personToRemove = getPersonByPersNr(mitarbeiterNr);
        if (!(personToRemove instanceof Mitarbeiter)) {
            throw new PersonNichtGefundenException(mitarbeiterNr);
        }
        HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS,
                EreignisTyp.MITARBEITER_LOESCHEN,
                mitarbeiterNr,
                true);
        personList.remove(personToRemove);
    }

    /**
     * Sucht Kunden anhand von kundenNr und kundenNamen.
     * Mithilfe einer for-Schleife werden die Kunden in der Personen-Liste darauf überprüft ob die kundenNr
     * und der kundenName übereinstimmt, falls ja wird der Kunde returnt, falls nein wurde die Person nicht
     * gefunden und es wird eine Exception geworfen
     */
    public Person personSuche(int persNr, String name) throws PersonNichtGefundenException {
        for (Person person : personList) {
            if (name.equalsIgnoreCase(person.getName()) && persNr == person.getPersNr()) {
                return person;
            }
        }
        throw new PersonNichtGefundenException(persNr);
    }

    /**
     * Gibt eine Liste mit allen Mitarbeitern zurück, die in der Personen-Liste sind.
     */
    public List<Mitarbeiter> getMitarbeiter() {
        return personList.stream()
                .filter(person -> person instanceof Mitarbeiter)
                .map(person -> (Mitarbeiter) person)
                .toList();
    }

    /**
     * gibt eine Liste aus Personen wieder dessen Name/Nutzername den angegebenen string beinhalten
     * @param query
     * @return
     */
    public Stream<Person> suchePersonByQuery(String query) {
        return personList.stream()
                .filter(person -> person.getName().toLowerCase().contains(query.toLowerCase()) ||
                                  person.getNutzername().toLowerCase().contains(query.toLowerCase()) ||
                                  String.valueOf(person.getPersNr()).contains(query)
                );
    }

    /**
     * gibt die nächstfreie Personennummer wieder
     * @return
     */
    public int getNaechsteId() {
        int max = 0;
        for (Person person : personList) {
            if (person.getPersNr() > max) {
                max = person.getPersNr();
            }
        }
        return max + 1;
    }

    /**
     * überprüft ob der angegebene Nutzername schon vergeben ist
     * @param nutzername
     * @return
     */
    public boolean istNutzernameVerfuegbar(String nutzername) {
        for (Person person : personList) {
            if (person.getNutzername().trim().equalsIgnoreCase(nutzername.trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * gibt die Liste aller Personenen wieder
     * @return
     */
    public List<Person> getPersonList() {
        return this.personList;
    }

    /**
     * speichert die aktuelle Personenliste in einer Datei ab
     * @throws IOException
     */
    public void save() throws IOException {
        persistenceManager.replaceAll(this.personList);
    }
}

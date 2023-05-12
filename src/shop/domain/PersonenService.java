package shop.domain;

import shop.domain.exceptions.personen.PersonNichtGefundenException;
import shop.domain.exceptions.personen.PersonVorhandenException;
import shop.entities.Kunde;
import shop.entities.Mitarbeiter;
import shop.entities.Person;

import java.util.ArrayList;
import java.util.List;

public class PersonenService {

    List<Person> personList = new ArrayList<>();

    /**
     * Überprüft mithilfe einer for-Schleife alle Personen in der Personenliste,
     * ob die eingegebene E-Mail mit der einer registrierten Person übereinstimmt, falls die E-Mail mit der
     * einer registrierten Person übereinstimmt wird das eingegebene Passwort mit der Person überprüft
     * trifft E-Mail und Passwort zu wird "true" return ansonsten false
     */
    public Person login(String email, String passwort) {
        for (Person person : personList) {
            if (email.equals(person.getEmail()) && passwort.equals(person.getPasswort())) {
                return person;
            }
        }
        return null;
    }

    /**
     * Registriert eine Person. Überprüft, ob die eingegebene E-Mail oder KundenNr bereits
     * vergeben ist, wenn die KundenNr oder E-Mail vergeben ist, wird eine Exception geworfen,
     * ansonsten wird ein neues KundenObjekt mit den eingegebenen Parametern erstellt und in
     * die PersonenListe hinzugefügt.
     *
     * @param person ist ein Objekt der Klasse {@link Person} und enthält die Attribute der zu registrierenden Person,
     *               welches über eine der Kindklassen {@link Kunde} oder {@link Mitarbeiter} erstellt wird.
     */
    public Person registerPerson(Person person) throws PersonVorhandenException {
        for (Person aktuellePerson : personList) {
            if (aktuellePerson.getEmail().trim().equalsIgnoreCase(person.getEmail().trim())) {
                throw new PersonVorhandenException();
            }
        }
        personList.add(person);
        // Wenn die Person ein Kunde ist, wird ein neuer Warenkorb erstellt
        if (person instanceof Kunde) {
            WarenkorbService warenkorbService = new WarenkorbService((Kunde) person);
            warenkorbService.neuerKorb((Kunde) person);
        }
        return person;
    }

    public Person getPersonByPersNr(int persNr) {
        for (Person person : personList) {
            if (person.getPersNr() == persNr) {
                return person;
            }
        }
        return null;
    }

    /**
     * Initialisiert eine Mitarbeiter-Liste und überprüft mithilfe einer for-Schleife jede Person darauf,
     * ob sie ein Mitarbeiter ist. Wenn die Person ein Mitarbeiter ist, wird sie in die Mitarbeiter-Liste
     * hinzugefügt. Daraufhin wird die Mitarbeiter-Liste returnt.
     */
    public List<Mitarbeiter> ShowMitarbeiter() {
        List<Mitarbeiter> MitarbeiterList = new ArrayList<>();
        for (Person personMitarbeiter : personList) {
            if (personMitarbeiter instanceof Mitarbeiter) MitarbeiterList.add((Mitarbeiter) personMitarbeiter);
        }
        return MitarbeiterList;
    }

    /**
     * Überprüft ob die eingegebene Person ein Mitarbeiter ist, falls nein wird eine Exception geworfen,
     * sonst wird der Mitarbeiter aus der Personen-Liste entfernt
     */
    public void removeMitarbeiter(int mitarbeiterNr) throws PersonNichtGefundenException {
        var personToRemove = getPersonByPersNr(mitarbeiterNr);
        if (!(personToRemove instanceof Mitarbeiter)) {
            throw new PersonNichtGefundenException(mitarbeiterNr);
        }
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
     * Gibt eine Liste mit allen Kunden zurück die in der Personen-Liste sind.
     */
    public List<Kunde> getKunden() {
        return personList.stream()
                .filter(person -> person instanceof Kunde)
                .map(person -> (Kunde) person)
                .toList();
    }

    /**
     * Gibt eine Liste mit allen Mitarbeitern zurück die in der Personen-Liste sind.
     */
    public List<Mitarbeiter> getMitarbeiter() {
        return personList.stream()
                .filter(person -> person instanceof Mitarbeiter)
                .map(person -> (Mitarbeiter) person)
                .toList();
    }

    public int getNaechsteId() {
        int max = 0;
        for (Person person : personList) {
            if (person.getPersNr() > max) {
                max = person.getPersNr();
            }
        }
        return max + 1;
    }

    public boolean istEmailVerfuegbar(String email) {
        for (Person person : personList) {
            if (person.getEmail().trim().equalsIgnoreCase(email.trim())) {
                return false;
            }
        }
        return true;
    }
}

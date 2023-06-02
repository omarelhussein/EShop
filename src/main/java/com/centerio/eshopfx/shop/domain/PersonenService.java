package com.centerio.eshopfx.shop.domain;

import shop.domain.exceptions.personen.PersonNichtGefundenException;
import shop.domain.exceptions.personen.PersonVorhandenException;
import shop.entities.Kunde;
import shop.entities.Mitarbeiter;
import shop.entities.Person;
import shop.persistence.FilePersistenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PersonenService {

    private final List<Person> personList = new ArrayList<>();
    private final WarenkorbService warenkorbService;
    private final FilePersistenceManager<Person> persistenceManager;

    public PersonenService() throws IOException {
        warenkorbService = WarenkorbService.getInstance();
        persistenceManager = new FilePersistenceManager<>("personen.csv");
    }

    /**
     * Überprüft mithilfe einer for-Schleife alle Personen in der Personenliste,
     * ob der eingegebene Nutzername mit der einer registrierten Person übereinstimmt, falls der Nutzername mit der
     * einer registrierten Person übereinstimmt wird das eingegebene Passwort mit der Person überprüft
     * trifft Nutzername und Passwort zu wird "true" return ansonsten false
     */
    public Person login(String nutzername, String passwort) {
        for (Person person : personList) {
            if (nutzername.equals(person.getNutzername()) && passwort.equals(person.getPasswort())) {
                return person;
            }
        }
        return null;
    }

    /**
     * Registriert eine Person. Überprüft, ob der eingegebene Nutzername oder KundenNr bereits
     * vergeben ist, wenn die KundenNr oder Nutzername vergeben ist, wird eine Exception geworfen,
     * ansonsten wird ein neues KundenObjekt mit den eingegebenen Parametern erstellt und in
     * die PersonenListe hinzugefügt.
     *
     * @param person ist ein Objekt der Klasse {@link Person} und enthält die Attribute der zu registrierenden Person,
     *               welches über eine der Kindklassen {@link Kunde} oder {@link Mitarbeiter} erstellt wird.
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
     * Gibt eine Liste mit allen Mitarbeitern zurück, die in der Personen-Liste sind.
     */
    public List<Mitarbeiter> getMitarbeiter() {
        return personList.stream()
                .filter(person -> person instanceof Mitarbeiter)
                .map(person -> (Mitarbeiter) person)
                .toList();
    }

    public Stream<Person> suchePersonByQuery(String query) {
        return personList.stream()
                .filter(person -> person.getName().toLowerCase().contains(query.toLowerCase()) ||
                                  person.getNutzername().toLowerCase().contains(query.toLowerCase()) ||
                                  String.valueOf(person.getPersNr()).contains(query)
                );
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

    public boolean istNutzernameVerfuegbar(String nutzername) {
        for (Person person : personList) {
            if (person.getNutzername().trim().equalsIgnoreCase(nutzername.trim())) {
                return false;
            }
        }
        return true;
    }

    public List<Person> getPersonList() {
        return this.personList;
    }

    public void save() throws IOException {
        persistenceManager.replaceAll(this.personList);
    }
}

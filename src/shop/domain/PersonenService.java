package shop.domain;

import shop.domain.exceptions.personen.PasswortNameException;
import shop.domain.exceptions.personen.PersonNichtGefundenException;
import shop.domain.exceptions.personen.GibtEsBereitsException;
import shop.entities.Kunde;
import shop.entities.Mitarbeiter;
import shop.entities.Person;

import java.util.ArrayList;
import java.util.List;

public class PersonenService {

    List<Person> personList = new ArrayList<>();

    // login (mitarbeiter bzw. kunde)
    public boolean login(String name, String passwort) throws PasswortNameException {
        for(Person persone: personList){
            if (name == persone.getName()){
                if(passwort == persone.getPasswort()){
                    return true;
                }
            }
            else {
                throw new PasswortNameException("Passwort oder Benutzername war falsch");
            }
        }
        return false;
    }

    // registrieren (auch mitarbeiter bzw. kunde). Es muss mindestens ein Standard Mitarbeiter geben
    // der andere Mitarbeiter registriert
    public void registerKunde(int kundenNr, String name, String Adresse, String Passwort) throws GibtEsBereitsException {
        for(Person personKunde: personList){
            if(personKunde instanceof Kunde && (name == personKunde.getName() || personKunde.getPersNr() == kundenNr)){
                throw new GibtEsBereitsException("Der Name oder ist bereits vergeben");
            }
            else {
                Person registrierendePerson = new Kunde(kundenNr, name, Adresse, Passwort);
                WarenkorbService.NeuerKorb(kundenNr);
                personList.add(registrierendePerson);
            }
        }
    }

    public void registerMitarbeiter(int mitarbeiterNr, String name, String Passwort) throws GibtEsBereitsException {
        for(Person personMitarbeiter: personList){
            if(personMitarbeiter instanceof Kunde && (name == personMitarbeiter.getName() || personMitarbeiter.getPersNr() == mitarbeiterNr)){
                throw new GibtEsBereitsException("Der Name oder ist bereits vergeben");
            }
            else {
                Person registrierendePerson = new Mitarbeiter(mitarbeiterNr, name, Passwort);
                personList.add(registrierendePerson);
            }
        }
    }

    // mitarbeiter anzeigen
    public void ShowMitarbeiter(){
        for(Person personMitarbeiter: personList){
            if(personMitarbeiter instanceof Mitarbeiter)
            System.out.println(personMitarbeiter.toString());
        }
    }

    // mitarbeiter entfernen
    public void removeMitarbeiter(Person mitarbeiter) throws PersonNichtGefundenException {
        if (mitarbeiter.getName() == null) {
            throw new PersonNichtGefundenException("Person zum löschen wurde nicht gefunden");
        }
        personList.remove(mitarbeiter);
    }

    // kunde suchen
    public Kunde kundenSuchen(String kundenName) throws PersonNichtGefundenException{
        for(Person personKunde: personList){
            if(personKunde instanceof Kunde && kundenName == personKunde.getName()){
                return (Kunde)personKunde;
            }
            else {
                throw new PersonNichtGefundenException("Person zum löschen wurde nicht gefunden");
            }
        }
        return null;
    }

    // kunden anzeigen?
    public void ShowKunden(){
        for(Person personKunde: personList){
            if(personKunde instanceof Kunde)
                System.out.println(personKunde.toString());
        }
    }
}

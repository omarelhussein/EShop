package shop.domain;

import shop.domain.exceptions.personen.PasswortNameException;
import shop.domain.exceptions.personen.PersonNichtGefundenException;
import shop.domain.exceptions.personen.GibtEsBereitsException;
import shop.domain.WarenkorbService;
import shop.entities.Kunde;
import shop.entities.Mitarbeiter;
import shop.entities.Person;

import java.util.ArrayList;
import java.util.List;

public class PersonenService {

    List<Person> personList = new ArrayList<>();

    /**
     * Überprüft mithilfe einer for-Schleife alle Personen in der Personenliste,
     * ob die eingegebene E-Mail mit der einer registrierten Person übereinstimmt falls die E-Mail mit der
     * einer registrierten Person übereinstimmt wird das eingebene Passwort mit der Person überprüft
     * trifft EMail und Passwort zu wird "true" returnt ansonsten false
     */
    public boolean login(String email, String passwort) throws PasswortNameException {
        for(Person persone: personList){
            if (email.equals(persone.getEmail())){
                if(passwort.equals(persone.getPasswort())){
                    return true;
                }
            }
            else {
                throw new PasswortNameException("Passwort oder Benutzername war falsch");
            }
        }
        return false;
    }

    /**
     * Registriert einen Kunden, @Parameter sind kundenNr, email, name, Adresse und Passwort.
     * Mithilfe einer for-Schleife wird jede Person darauf überprüft, ob die eingegebene E-Mail oder KundenNr bereits
     * vergeben ist, wenn die KundenNr oder EMail vergeben ist wird eine Exception geworfen,
     * ansonsten wird ein neues KundenObjekt mit den eingegebenen Parametern erstellt und in
     * die PersonenListe hinzugefügt.
     */
    public void registerKunde(int kundenNr, String email, String name, String Adresse, String Passwort) throws GibtEsBereitsException {
        for(Person personKunde: personList) {
            if (personKunde instanceof Kunde && (email.equals(personKunde.getEmail()) || personKunde.getPersNr() == kundenNr)) {
                throw new GibtEsBereitsException("Der Name oder ist bereits vergeben");
            }
        }
        Person registrierendePerson = new Kunde(kundenNr, email, name, Adresse, Passwort);
        personList.add(registrierendePerson);
        WarenkorbService Warenkorb = new WarenkorbService();
        Warenkorb.NeuerKorb(kundenNr);

    }

    /**
     * Registriert einen Mitarbeiter, @Parameter sind mitarbeiterNr, email, name und Passwort.
     * Mithilfe einer for-Schleife wird jede Person darauf überprüft, ob die eingegebene E-Mail oder MitarbeiterNr bereits
     * vergeben ist, wenn die MitarbeiterNr oder EMail vergeben ist wird eine Exception geworfen,
     * ansonsten wird ein neues MitarbeiterObjekt mit den eingegebenen Parametern erstellt und in
     * die PersonenListe hinzugefügt.
     */
    public void registerMitarbeiter(int mitarbeiterNr, String email, String name, String Passwort) throws GibtEsBereitsException {
        for(Person personMitarbeiter: personList) {
            if (personMitarbeiter instanceof Mitarbeiter && (email.equals(personMitarbeiter.getEmail()) || personMitarbeiter.getPersNr() == mitarbeiterNr)) {
                throw new GibtEsBereitsException("Die E-Mail oder die MitarbeiterNr ist bereits vergeben");
            }
        }
        Person registrierendePerson = new Mitarbeiter(mitarbeiterNr, email, name, Passwort);
        personList.add(registrierendePerson);

    }


    /**
     * Initialisiert eine Mitarbeiter-Liste und überprüft mithilfe einer for-Schleife jede Person darauf,
     * ob sie ein Mitarbeiter ist. Wenn die Person ein Mitarbeiter ist, wird sie in die Mitarbeiter-Liste
     * hinzugefügt. Daraufhin wird die Mitarbeiter-Liste returnt.
     */
    public List<Mitarbeiter> ShowMitarbeiter(){
        List<Mitarbeiter> MitarbeiterList = new ArrayList<>();
        for(Person personMitarbeiter: personList){
            if(personMitarbeiter instanceof Mitarbeiter)
                MitarbeiterList.add((Mitarbeiter)personMitarbeiter);
        }
        return MitarbeiterList;
    }

    /**
     * Überprüft ob die eingegebene Person ein Mitarbeiter ist, falls nein wird eine Exception geworfen,
     * sonst wird der Mitarbeiter aus der Personen-Liste entfernt
     */
    public void removeMitarbeiter(Person mitarbeiter) throws PersonNichtGefundenException {
        if (mitarbeiter instanceof Kunde) {
            throw new PersonNichtGefundenException("Person zum löschen ist ein Kunde und kein Mitarbeiter");
        }
        personList.remove(mitarbeiter);
    }

    /**
     * Sucht Kunden anhand von kundenNr und kundenNamen.
     * Mithilfe einer for-Schleife werden die Kunden in der Personen-Liste darauf überprüft ob die kundenNr
     * und der kundenName übereinstimmt, falls ja wird der Kunde returnt, falls nein wurde die Person nicht
     * gefunden und es wird eine Exception geworfen
     */
    public Kunde kundenSuchen(int kundenNr, String kundenName) throws PersonNichtGefundenException{
        for(Person personKunde: personList){
            if(personKunde instanceof Kunde && kundenName.equals(personKunde.getName()) && kundenNr == personKunde.getPersNr()){
                return (Kunde)personKunde;
            }
            else {
                throw new PersonNichtGefundenException("Person wurde nicht gefunden");
            }
        }
        return null;
    }

    /**
     * Initialisiert eine Kunden-Liste und überprüft mithilfe einer for-Schleife jede Person darauf,
     * ob sie ein Kunde ist. Wenn die Person ein Kunde ist, wird sie in die Kunden-Liste
     * hinzugefügt. Daraufhin wird die Kunden-Liste returnt.
     */
    public List<Kunde> ShowKunden(){
        List<Kunde> kundenList = new ArrayList<>();
        for(Person personKunde: personList){
            if(personKunde instanceof Kunde)
                kundenList.add((Kunde)personKunde);
        }
        return kundenList;
    }
}

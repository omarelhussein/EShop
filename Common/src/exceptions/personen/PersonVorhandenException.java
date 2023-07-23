package exceptions.personen;

public class PersonVorhandenException extends Exception {
    public PersonVorhandenException() {
        super("Person bereits vorhanden!");
    }
}

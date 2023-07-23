package exceptions.personen;

public class PersonNichtGefundenException extends Exception{
    public PersonNichtGefundenException(int personId){
        super("Person mit der ID " + personId + " nicht gefunden!");
    }
}

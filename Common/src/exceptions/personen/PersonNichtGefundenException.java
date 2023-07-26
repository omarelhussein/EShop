package exceptions.personen;

public class PersonNichtGefundenException extends Exception{
    /**
     * Wird gethrowed wenn keine Person mit der angegebenen Personennummer in der Personenliste gefunden wurde
     * @param personId
     */
    public PersonNichtGefundenException(int personId){
        super("Person mit der ID " + personId + " nicht gefunden!");
    }
}

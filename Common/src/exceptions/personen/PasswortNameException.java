package exceptions.personen;

public class PasswortNameException extends Exception{
    /**
     * wird gethrowed wenn es keine Person gibt mit der eingegebenen Kombination aus Nutzername und Passwort
     */
    public PasswortNameException()  {
        super("Keinen Nutzer wurde mit dieser Kombination aus Name und Passwort gefunden");
    }
}

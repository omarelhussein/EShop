package exceptions.personen;

public class PasswortNameException extends Exception{
    public PasswortNameException()  {
        super("Keinen Nutzer wurde mit dieser Kombination aus Name und Passwort gefunden");
    }
}

package com.centerio.eshopfx.shop.domain.exceptions.personen;

public class PasswortNameException extends Exception{
    public PasswortNameException(String name, String passwort)  {
        super("Keinen Nutzer mit dem Namen " + name + " und dem Passwort " + passwort + " gefunden.");
    }
}

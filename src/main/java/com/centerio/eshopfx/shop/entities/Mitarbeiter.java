package com.centerio.eshopfx.shop.entities;

public class Mitarbeiter extends Person {

    public Mitarbeiter(int mitarbeiterNr, String nutzername, String name, String passwort) {
        super(mitarbeiterNr, nutzername, name, passwort);
    }

    public String toCSVString() {
        StringBuilder sb = new StringBuilder();
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1); // letztes | entfernen
        }
        return "Mitarbeiter;" + getPersNr() + ";" + getNutzername() + ";" + getName() + ";" + getPasswort();
    }

    public void fromCSVString(String csv) {
        // split ist eine Methode der Klasse String, die einen String anhand eines Trennzeichens in ein Array von Strings aufteilt.
        // bsp: "Hallo;Welt;!" -> split(";") -> ["Hallo", "Welt", "!"]
        String[] tokens = csv.split(";");
        setPersNr(Integer.parseInt(tokens[0]));
        setNutzername(tokens[1]);
        setName(tokens[2]);
        setPasswort(tokens[3]);
    }
}

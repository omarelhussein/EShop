package com.centerio.eshopfx.shop.entities.enums;

import com.centerio.eshopfx.shop.entities.Artikel;

// TODO: Idee, noch ein parameter wo definiert, ob dieser Ereignis registriert werden soll oder nicht.
// TODO: Kann vom Mitarbeiter über die GUI/CUI eingestellt werden.
public enum EreignisTyp {
    BESTANDAENDERUNG(),
    KAUF(),
    MITARBEITER_ANLEGEN(),
    MITARBEITER_LOESCHEN(),
    MITARBEITER_ANZEIGEN(),
    MITARBEITER_SUCHEN(),
    LOGIN(),
    LOGOUT(),
    ARTIKEL_ANLEGEN(),
    ARTIKEL_LOESCHEN(),
    ARTIKEL_AKTUALISIEREN(),
    ARTIKEL_ANZEIGEN(),
    ARTIKEL_SUCHEN(),
    WARENKORB_HINZUFUEGEN(),
    WARENKORB_AENDERN(),
    WARENKORB_ANZEIGEN(),
    EREIGNIS_ANZEIGEN();

    public String toGUIString(){
        String ereignisTyp = "";
        switch (this) {
            case BESTANDAENDERUNG -> {ereignisTyp = "Bestandänderung"; return ereignisTyp;}
            case LOGIN -> {ereignisTyp = "Login"; return ereignisTyp;}
            case LOGOUT -> {ereignisTyp = "Logout"; return ereignisTyp;}
            case ARTIKEL_ANZEIGEN -> {ereignisTyp = "Artikel angezeigt"; return ereignisTyp;}
            case ARTIKEL_SUCHEN -> {ereignisTyp = "Artikel gesucht"; return ereignisTyp;}
            case ARTIKEL_LOESCHEN -> {ereignisTyp = "Artikel gelöscht"; return ereignisTyp;}
            case ARTIKEL_ANLEGEN -> {ereignisTyp = "Artikel angelegt"; return ereignisTyp;}
            case ARTIKEL_AKTUALISIEREN -> {ereignisTyp = "Artikel aktualisiert"; return ereignisTyp;}
            case KAUF -> {ereignisTyp = "Kauf"; return ereignisTyp;}
            case MITARBEITER_SUCHEN -> {ereignisTyp = "Mitarbeiter gesucht"; return ereignisTyp;}
            case MITARBEITER_LOESCHEN -> {ereignisTyp = "Mitarbeiter gelöscht"; return ereignisTyp;}
            case MITARBEITER_ANZEIGEN -> {ereignisTyp = "Mitarbeiter angezeigt"; return ereignisTyp;}
            case MITARBEITER_ANLEGEN -> {ereignisTyp = "Mitarbeiter angelegt"; return ereignisTyp;}
            case EREIGNIS_ANZEIGEN -> {ereignisTyp = "Ereignisse angezeigt"; return ereignisTyp;}
            case WARENKORB_HINZUFUEGEN -> {ereignisTyp = "Zum Warenkorb hinzugefügt"; return ereignisTyp;}
            case WARENKORB_AENDERN -> {ereignisTyp = "Warenkorb geändert"; return ereignisTyp;}
            case WARENKORB_ANZEIGEN -> {ereignisTyp = "Warenkorb angezeigt"; return ereignisTyp;}

        }
        return ereignisTyp;
    }
}

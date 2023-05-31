package shop.entities.enums;

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

}

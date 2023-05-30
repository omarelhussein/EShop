package shop.entities;

// TODO: Idee, noch ein parameter wo definiert, ob dieser Ereignis registriert werden soll oder nicht.
// TODO: Kann vom Mitarbeiter über die GUI/CUI eingestellt werden.
public enum EreignisTyp {
    BESTANDAENDERUNG(true),
    KAUF(true),
    MITARBEITER_ANLEGEN(true),
    MITARBEITER_LOESCHEN(true),
    MITARBEITER_ANZEIGEN(true),
    MITARBEITER_SUCHEN(true),
    LOGIN(true),
    LOGOUT(true),
    ARTIKEL_ANLEGEN(true),
    ARTIKEL_LOESCHEN(true),
    ARTIKEL_AKTUALISIEREN(true),
    ARTIKEL_ANZEIGEN(true),
    ARTIKEL_SUCHEN(true),
    WARENKORB_HINZUFUEGEN(true),
    WARENKORB_AENDERN(true),
    WARENKORB_ANZEIGEN(true),
    EREIGNIS_ANZEIGEN(true);
    private boolean sichtbar;

    EreignisTyp(boolean sichtbar) {
        this.sichtbar = sichtbar;
    }

    public void setSichtbar(boolean sichtbar) {
        this.sichtbar = sichtbar;
    }

    public boolean isSichtbar() {
        return sichtbar;
    }

}

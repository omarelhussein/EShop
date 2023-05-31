package shop.entities;

import shop.domain.EreignisService;

public class Mitarbeiter extends Person {

    public Mitarbeiter(int mitarbeiterNr, String nutzername, String name, String passwort) {
        super(mitarbeiterNr, nutzername, name, passwort);
    }

    public String toCSVString() {
        StringBuilder sb = new StringBuilder();
        for (BestandshistorieItem item : EreignisService.getInstance().getBestandhistorieItemList()) {
            sb.append(item.toCSVString()).append("|");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1); // letztes | entfernen
        }
        return "Mitarbeiter;" + getPersNr() + ";" + getNutzername() + ";" + getName() + ";" + getPasswort();
    }

    public void fromCSVString(String csv) {
        String[] tokens = csv.split(";");
        setPersNr(Integer.parseInt(tokens[0]));
        setNutzername(tokens[1]);
        setName(tokens[2]);
        setPasswort(tokens[3]);
    }
}

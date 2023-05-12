package shop.entities;

public class Adresse {

    private String strasse;
    private String hausnummer;
    private String plz;
    private String ort;

    public Adresse(String strasse, String hausnummer, String plz, String ort) {
        this.strasse = strasse;
        this.hausnummer = hausnummer;
        this.plz = plz;
        this.ort = ort;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setHausnummer(String hausnummer) {
        this.hausnummer = hausnummer;
    }

    public String getHausnummer() {
        return hausnummer;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getPlz() {
        return plz;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getOrt() {
        return ort;
    }

    @Override
    public String toString() {
        return "Adresse: " + strasse + " " + hausnummer + ", " + plz + " " + ort + "\n";
    }
}

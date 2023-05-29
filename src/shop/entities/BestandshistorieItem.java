package shop.entities;

import java.time.LocalDateTime;

public class BestandshistorieItem {
    private int bestand;
    private LocalDateTime datum;

    public BestandshistorieItem(int bestand) {
        setBestand(bestand);
    }

    public void setBestand(int bestand) {
        this.bestand = bestand;
        this.datum = LocalDateTime.now();
    }

    public int getBestand() {
        return bestand;
    }

    public LocalDateTime getDatum() {
        return datum;
    }

    @Override
    public String toString() {
        return "Bestand: " + bestand + " / Datum: " + datum;
    }
}



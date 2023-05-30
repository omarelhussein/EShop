package shop.entities;

import shop.utils.StringUtils;

import java.time.LocalDateTime;

public class BestandshistorieItem {
    private final int bestand;
    private final LocalDateTime datum;
    private final boolean istKauf;

    public BestandshistorieItem(int bestand, boolean istKauf) {
        this.bestand = bestand;
        this.datum = LocalDateTime.now();
        this.istKauf = istKauf;
    }

    public int getBestand() {
        return bestand;
    }

    public LocalDateTime getDatum() {
        return datum;
    }

    /**
     * Gibt an, ob die Bestandsänderung ein Kauf war.
     */
    public boolean istKauf() {
        return istKauf;
    }

    @Override
    public String toString() {
        return "\t\t- Am " + StringUtils.formatDate(datum) + " war der Bestand " + bestand + " Stück "
               + (istKauf ? "[Kauf]" : "[Ein/Auslagerung]") + ".";
    }
}



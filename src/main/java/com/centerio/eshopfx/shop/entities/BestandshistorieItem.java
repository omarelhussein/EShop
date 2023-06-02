package com.centerio.eshopfx.shop.entities;

import shop.persistence.CSVSerializable;
import shop.utils.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BestandshistorieItem implements Serializable, CSVSerializable {
    private Artikel artikel;
    private int bestand;
    private LocalDateTime datum;
    private boolean istKauf;

    public BestandshistorieItem(int bestand, boolean istKauf, Artikel artikel) {
        this.bestand = bestand;
        this.datum = LocalDateTime.now();
        this.istKauf = istKauf;
        this.artikel = artikel;
    }

    public BestandshistorieItem() {}

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

    @Override
    public String toCSVString() {
        return bestand + "#" + datum + "#" + istKauf;
    }

    @Override
    public void fromCSVString(String csv) {
        // split ist eine Methode der Klasse String, die einen String anhand eines Trennzeichens in ein Array von Strings aufteilt.
        // bsp: "Hallo#Welt#!" -> split("#") -> ["Hallo", "Welt", "!"]
        String[] parts = csv.split("#");
        if (parts.length == 0) return;
        try {
            this.bestand = Integer.parseInt(parts[0]);
            this.datum = LocalDateTime.parse(parts[1]);
            this.istKauf = Boolean.parseBoolean(parts[2]);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("CSV-String ist ungültig: " + csv);
        }
    }
}



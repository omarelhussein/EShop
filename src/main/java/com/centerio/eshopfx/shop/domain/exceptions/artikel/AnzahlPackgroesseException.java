package com.centerio.eshopfx.shop.domain.exceptions.artikel;

import com.centerio.eshopfx.shop.entities.Massenartikel;

public class AnzahlPackgroesseException extends Exception{
    public AnzahlPackgroesseException (int anzahl, Massenartikel artikel) {
        super("Die angegebene Anzahl" + anzahl + " bezüglich des Massenartikels" + artikel.getBezeichnung() +
                "lässt sich nicht durch dessen Packgröße" + artikel.getPackgroesse() + "teilen.");
    }
}

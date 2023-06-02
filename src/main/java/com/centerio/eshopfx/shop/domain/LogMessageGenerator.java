package com.centerio.eshopfx.shop.domain;

import shop.entities.Artikel;
import shop.entities.Ereignis;

public class LogMessageGenerator {

    public static String generateLogMessage(Ereignis ereignis) {
        var builder = new StringBuilder();
        var object = ereignis.getObject();
        var ereignisTyp = ereignis.getEreignisTyp();

        switch (ereignisTyp) {
            case BESTANDAENDERUNG -> {
                if (object instanceof Artikel artikel) {
                    builder.append("Bestand von Artikel ").append(artikel.getBezeichnung())
                            .append(" mit Artikelnummer ").append(artikel.getArtNr())
                            .append(" wurde auf ").append(artikel.getBestand()).append(" geändert.");
                }
            }
            case LOGIN -> builder.append("Nutzer hat sich eingeloggt.");
            case LOGOUT -> builder.append("Nutzer hat sich ausgeloggt.");
            case ARTIKEL_ANZEIGEN -> {
                if (object instanceof Artikel artikel) {
                    builder.append("Artikel ").append(artikel.getBezeichnung())
                            .append(" mit Artikelnummer ").append(artikel.getArtNr())
                            .append(" wurde angezeigt.");
                } else if (object instanceof String) {
                    builder.append("Es wurde nach Artikel mit dem Begriff \"").append(object).append("\" gesucht.");
                } else if (object instanceof Integer) {
                    builder.append("Es wurden ").append((int) object).append(" Artikel angezeigt.");
                } else {
                    builder.append("Es wurden alle Artikel angezeigt.");
                }
            }
            case ARTIKEL_SUCHEN ->
                    builder.append("Es wurde nach Artikel mit dem Begriff \"").append(object.toString()).append("\" gesucht.");
            case KAUF -> builder.append("Es wurde ").append(((String)((Artikel)object).getBezeichnung())).append(" gekauft.").append("\n").append("Neuer Bestand: ").append(ereignis.getBestand());
            case MITARBEITER_SUCHEN ->
                    builder.append("Es wurde nach Mitarbeiter").append((String) object).append(" gesucht.");
            case ARTIKEL_ANLEGEN -> {
                if (object instanceof Artikel artikel) {
                    builder.append("\n")
                            .append("Artikel ").append(artikel.getBezeichnung())
                            .append(" mit Artikelnummer ").append(artikel.getArtNr())
                            .append(" wurde mit Bestand: " + ereignis.getBestand() + "Stk. angelegt.");
                }
            }
        }
        return builder.toString();
    }

}

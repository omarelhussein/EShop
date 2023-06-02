package com.centerio.eshopfx.shop.utils;

import shop.domain.ArtikelService;
import shop.domain.EreignisService;
import shop.entities.Artikel;
import shop.entities.Massenartikel;
import shop.entities.enums.EreignisTyp;
import shop.entities.enums.KategorieEreignisTyp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Die SeedingUtils-Klasse ist eine Hilfsklasse, die bei der Initialisierung der Anwendung verwendet wird,
 * um eine Anfangsgruppe von Artikeln zu erzeugen (auch als "seeding" bekannt).
 * Diese Klasse erstellt eine Liste von Artikeln und fügt sie über den ArtikelService zur Anwendung hinzu.
 */
public class SeedingUtils {

    private final ArtikelService artikelService;

    /**
     * Konstruktor für die SeedingUtils-Klasse. Initialisiert eine Instanz des ArtikelServices (bzw. andere Services, falls vorhanden)
     * und ruft die Methode zum Erzeugen der initialen Artikel auf.
     */
    public SeedingUtils() throws IOException {
        this.artikelService = ArtikelService.getInstance();
        seedArtikel();
    }

    public void seedArtikel() {
        List<Artikel> artikelList = new ArrayList<>();
        artikelList.add(new Artikel(127, "Hose", 19.99, 10));
        artikelList.add(new Artikel(128, "Hemd", 29.99, 10));
        artikelList.add(new Artikel(129, "Schuhe", 39.99, 10));
        artikelList.add(new Artikel(130, "Socken", 9.99, 10));
        artikelList.add(new Artikel(131, "Red Bull", 1.99, 10));
        artikelList.add(new Artikel(132, "Kaffee", 2.99, 10));
        artikelList.add(new Artikel(133, "Schwarztee", 3.99, 10));
        artikelList.add(new Artikel(134, "Grüntee", 3.99, 10));
        artikelList.add(new Artikel(135, "Red Bull Watermelon", 1.99, 10));
        artikelList.add(new Massenartikel(137, "Red Bull (4er)", 1.99, 10, 4));
        for (Artikel artikel : artikelList) {
            artikelService.addArtikel(artikel);
            EreignisService.getInstance().addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANLEGEN, artikel, true);
        }
    }

}

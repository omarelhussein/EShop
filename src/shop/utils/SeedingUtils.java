package shop.utils;

import shop.domain.ArtikelService;
import shop.domain.EreignisService;
import shop.entities.Artikel;

import java.util.ArrayList;
import java.util.List;

public class SeedingUtils {

    private final ArtikelService artikelService;

    public SeedingUtils() {
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
        for (Artikel artikel : artikelList) {
            artikelService.addArtikel(artikel);
            EreignisService.getInstance().artikelAddEreignis(artikel);
        }
    }

}

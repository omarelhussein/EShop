package shop.persistence;

import shop.domain.ArtikelService;
import shop.entities.Artikel;
import shop.entities.Massenartikel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class FilePersistenceManager {

    private String string;
    private ArtikelService artikelService;

    public FilePersistenceManager() {
        artikelService = ArtikelService.getInstance();
    }


    public void artikelspeichern() throws IOException {
        var dieliste = artikelService.getArtikelList();
        Iterator<Artikel> iter = dieliste.iterator();
        while (iter.hasNext()) {
            Artikel artikel = iter.next();
            if (artikel instanceof Massenartikel) {
                this.string = artikel.getArtNr() + ";" + artikel.getBezeichnung() + ";" + artikel.getBestand() +
                        ";" + artikel.getPreis() + ";" + artikel.getBestandshistorie() + ";" + +((Massenartikel) artikel).getPackgroesse();
                try (FileWriter fw = new FileWriter("Bestand.txt", true)) {
                    fw.write(string);
                }
            } else {
                this.string = artikel.getArtNr() + ";" + artikel.getBezeichnung() + ";" + artikel.getBestand() +
                        ";" + artikel.getPreis() + artikel.getBestandshistorie();
                try (FileWriter fw = new FileWriter("Bestand.txt")) {
                    fw.write(string);
                }
            }
        }
    }
}

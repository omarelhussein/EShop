package shop.persistence;

import shop.domain.ArtikelService;
import shop.entities.Artikel;
import shop.entities.Massenartikel;

import java.io.*;
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
        try (FileWriter fw = new FileWriter("Bestand.txt")) {
            BufferedWriter bw = new BufferedWriter(fw);
            while (iter.hasNext()) {
                Artikel artikel = iter.next();
                if (artikel instanceof Massenartikel) {
                    this.string = artikel.getArtNr() + ";" + artikel.getBezeichnung() + ";" + artikel.getPreis() +
                            ";" + artikel.getBestand() + ";" + artikel.getBestandshistorie() + ";" + +((Massenartikel) artikel).getPackgroesse();
                } else {
                    this.string = artikel.getArtNr() + ";" + artikel.getBezeichnung() + ";" + artikel.getBestand() +
                            ";" + artikel.getPreis() + ";" + artikel.getBestandshistorie();
                    }
                bw.write(string);
                bw.newLine();
            }
            bw.close();
        }
    }
}

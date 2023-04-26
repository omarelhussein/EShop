package shop.domain;

import shop.domain.exceptions.artikel.ArtikelBereitsVorhandenException;
import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.entities.Artikel;

import java.util.List;

public class ShopAPI {

    // Artikeln
    private final ArtikelService artikelService;

    public ShopAPI() {
        artikelService = new ArtikelService();
    }

    public void addArtikel(Artikel artikel) throws ArtikelBereitsVorhandenException {
        artikelService.addArtikel(artikel);
    }

    public void removeArtikel(Artikel artikel) throws ArtikelNichtGefundenException {
        artikelService.removeArtikel(artikel);
    }

    public List<Artikel> sucheArtikelByName(String queryString) {
        return artikelService.sucheArtikelByName(queryString);
    }

    public Artikel sucheArtikelByArtNr(int artNr) {
        return artikelService.sucheArtikelByArtNr(artNr);
    }

    public List<Artikel> alleArtikelAusgeben() {
        return artikelService.alleArtikelAusgeben();
    }

    public Artikel getArtikelByArtNr(int artikelNr) {
        return artikelService.getArtikelByArtNr(artikelNr);
    }

    public boolean artikelVergleichen(Artikel artikel1, Artikel artikel2) {
        return artikelService.artikelVergleichen(artikel1, artikel2);
    }

}

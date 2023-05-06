package shop.domain;

import shop.domain.exceptions.artikel.ArtikelBereitsVorhandenException;
import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.entities.Artikel;
import shop.entities.Kunde;
import shop.entities.Person;

import java.util.List;

public class ShopAPI {

    // Artikeln
    private final ArtikelService artikelService;
    private final PersonenService personenService;

    public ShopAPI() {
        artikelService = new ArtikelService();
        personenService = new PersonenService();
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

    public Person login(String nutzername, String passwort) {
        return new Kunde(1,
                "Max Mustermann",
                "Musterstraße 1",
                "max",
                "123",
                "max@mustermann.com");
    }

    public Person registrieren(String name, String adresse, String nutzername, String passwort, String email) {
        return new Kunde(1, name, adresse, nutzername, passwort, email);
    }

}

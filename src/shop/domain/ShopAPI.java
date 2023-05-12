package shop.domain;

import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.domain.exceptions.personen.PersonVorhandenException;
import shop.entities.Artikel;
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

    public void addArtikel(Artikel artikel) {
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

    public List<Artikel> getArtikelList() {
        return artikelService.getArtikelList();
    }

    public Artikel getArtikelByArtNr(int artikelNr) {
        return artikelService.getArtikelByArtNr(artikelNr);
    }

    public List<Artikel> getArtikelByQuery(String query) {
        return artikelService.getArtikelByQuery(query);
    }

    public boolean artikelVergleichen(Artikel artikel1, Artikel artikel2) {
        return artikelService.artikelVergleichen(artikel1, artikel2);
    }

    public Person login(String nutzername, String passwort) {
        return personenService.login(nutzername, passwort);
    }


    public Person registrieren(Person person) throws PersonVorhandenException {
        return personenService.registerPerson(person);
    }

    public int getNaechsteId() {
        return personenService.getNaechsteId();
    }

    public boolean istEmailVerfuegbar(String email) {
        return personenService.istEmailVerfuegbar(email);
    }
}

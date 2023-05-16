package shop.domain;

import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.domain.exceptions.personen.PersonVorhandenException;
import shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import shop.domain.exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import shop.entities.Artikel;
import shop.entities.Kunde;
import shop.entities.Person;
import shop.entities.Warenkorb;

import java.util.List;

public class ShopAPI {

    // Artikeln
    private final ArtikelService artikelService;
    private final PersonenService personenService;
    private final WarenkorbService warenkorbService;
    private Person eingeloggterNutzer;


    public ShopAPI() {
        artikelService = ArtikelService.getInstance();
        personenService = new PersonenService();
        warenkorbService = WarenkorbService.getInstance();
    }

    public void addArtikel(Artikel artikel) {
        artikelService.addArtikel(artikel);
        EreignisService.getInstance().artikelAddEreignis(artikel);
    }

    public void removeArtikel(int artikelNr) throws ArtikelNichtGefundenException {
        var artikel = artikelService.getArtikelByArtNr(artikelNr);
        artikelService.removeArtikel(artikel);
        EreignisService.getInstance().artikelRemoveEreignis(artikel);
    }

    public List<Artikel> getArtikelList() {
        EreignisService.getInstance().getArtikelListEreignis();
        return artikelService.getArtikelList();
    }

    public List<Artikel> getArtikelByQuery(String query) {
        EreignisService.getInstance().getArtikelByArtQueryEreignis(query);
        return artikelService.sucheArtikelByQuery(query);
    }

    public Warenkorb getWarenkorb() {
        return warenkorbService.getWarenkorb();
    }

    public double getWarenkorbGesamtpreis() {
        return getWarenkorb().getGesamtSumme();
    }

    public boolean addArtikelToWarenkorb(int artikelNr, int anzahl)
            throws BestandUeberschrittenException, ArtikelNichtGefundenException {
        return warenkorbService.legeArtikelImWarenkorb(artikelNr, anzahl);
    }

    public void aendereArtikelAnzahlImWarenkorb(int artikelNr, int anzahl)
            throws BestandUeberschrittenException, ArtikelNichtGefundenException,
            WarenkorbArtikelNichtGefundenException {
        warenkorbService.aendereWarenkorbArtikelAnzahl(artikelNr, anzahl);
    }

    public Person login(String nutzername, String passwort) {
        return personenService.login(nutzername, passwort);
    }


    public Person registrieren(Person person) throws PersonVorhandenException {
        return personenService.registerPerson(person);
    }

    public int getNaechstePersId() {
        return personenService.getNaechsteId();
    }

    public int getNaechsteArtikelId() {
        return artikelService.getNaechsteId();
    }

    public boolean istEmailVerfuegbar(String email) {
        return personenService.istEmailVerfuegbar(email);
    }

    public void setEingeloggterNutzer(Person eingeloggterNutzer) {
        this.eingeloggterNutzer = eingeloggterNutzer;
        if (eingeloggterNutzer instanceof Kunde)
            warenkorbService.setAktuellerKunde((Kunde) eingeloggterNutzer);
    }

    public Person getEingeloggterNutzer() {
        return eingeloggterNutzer;
    }

}

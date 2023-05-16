package shop.domain;

import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.domain.exceptions.personen.PersonNichtGefundenException;
import shop.domain.exceptions.personen.PersonVorhandenException;
import shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import shop.domain.exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import shop.entities.*;

import java.util.ArrayList;
import java.util.List;

public class ShopAPI {

    // Artikeln
    private final ArtikelService artikelService;
    private final PersonenService personenService;
    private final WarenkorbService warenkorbService;
    private Person eingeloggterNutzer;
    private final EreignisService ereignisService;
    private final BestellService bestellService;


    public ShopAPI() {
        artikelService = ArtikelService.getInstance();
        personenService = new PersonenService();
        warenkorbService = WarenkorbService.getInstance();
        ereignisService = EreignisService.getInstance();
        bestellService = new BestellService();
    }

    public void addArtikel(Artikel artikel) {
        artikelService.addArtikel(artikel);
        ereignisService.artikelAddEreignis(artikel);
    }

    public void removeArtikel(int artikelNr) throws ArtikelNichtGefundenException {
        var artikel = artikelService.getArtikelByArtNr(artikelNr);
        artikelService.removeArtikel(artikel);
        ereignisService.artikelRemoveEreignis(artikel);
    }

    public List<Artikel> getArtikelList() {
        ereignisService.getArtikelListEreignis();
        return artikelService.getArtikelList();
    }

    public List<Artikel> getArtikelByQuery(String query) {
        ereignisService.sucheArtikelByArtQueryEreignis(query);
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
        var login = personenService.login(nutzername, passwort);
        ereignisService.getLoginEreignis();
        return login;
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

    public void artikelAktualisieren(Artikel artikel) throws ArtikelNichtGefundenException {
        artikelService.artikelAktualisieren(artikel);
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

    public List<Mitarbeiter> getMitarbeiterList() {
        return personenService.getMitarbeiter();
    }

    public List<Mitarbeiter> getMitarbeiterList(String suchbegriff) {
        return personenService.suchePersonByQuery(suchbegriff)
                .filter(Mitarbeiter.class::isInstance)
                .map(Mitarbeiter.class::cast).toList();
    }

    public void mitarbeiterLoeschen(int mitarbeiterId) throws PersonNichtGefundenException {
        if (eingeloggterNutzer.getPersNr() != mitarbeiterId) {
            personenService.removeMitarbeiter(mitarbeiterId);
            return;
        }
        throw new PersonNichtGefundenException(mitarbeiterId);
    }

    public void aendereArtikelBestand(int artikelId, int bestand) throws ArtikelNichtGefundenException {
        artikelService.aendereArtikelBestand(artikelId, bestand);
    }

    public ArrayList<Ereignis> getEreignisList() {
        return ereignisService.kundeOderMitarbeiterEreignisListe();
    }

    public void rechnungErstellen() {
        bestellService.rechnungErstellen();
    }

    public void kaufen() {
        bestellService.kaufen();
    }
}

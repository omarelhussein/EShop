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
        ereignisService.getInstance().artikelAddEreignis(artikel);
        artikelService.addArtikel(artikel);
    }

    public void removeArtikel(int artikelNr) throws ArtikelNichtGefundenException {
        var artikel = artikelService.getArtikelByArtNr(artikelNr);
        ereignisService.getInstance().artikelRemoveEreignis(artikel);
        artikelService.removeArtikel(artikel);
    }

    public List<Artikel> getArtikelList() {
        ereignisService.getInstance().getArtikelListEreignis(artikelService.getArtikelList());
        return artikelService.getArtikelList();
    }

    public List<Artikel> getArtikelByQuery(String query) {
        ereignisService.getInstance().sucheArtikelByArtQueryEreignis(artikelService.sucheArtikelByQuery(query), query);
        return artikelService.sucheArtikelByQuery(query);
    }

    public Warenkorb getWarenkorb() {
        ereignisService.getInstance().warenkorbAusgabeEreignis(warenkorbService.getWarenkorb());
        return warenkorbService.getWarenkorb();
    }

    public double getWarenkorbGesamtpreis() {
        return getWarenkorb().getGesamtSumme();
    }

    public boolean addArtikelToWarenkorb(int artikelNr, int anzahl)
            throws BestandUeberschrittenException, ArtikelNichtGefundenException {
        var placeholder = warenkorbService.legeArtikelImWarenkorb(artikelNr, anzahl);
        EreignisService.getInstance().addArtikelWarenkorbEreignis(artikelService.getInstance().getArtikelByArtNr(artikelNr));
        return placeholder;
    }

    public void aendereArtikelAnzahlImWarenkorb(int artikelNr, int anzahl)
            throws BestandUeberschrittenException, ArtikelNichtGefundenException,
            WarenkorbArtikelNichtGefundenException {
        EreignisService.getInstance().warenkorbArtikelAnzahlEreignis(warenkorbService.getWarenkorb());
        warenkorbService.aendereWarenkorbArtikelAnzahl(artikelNr, anzahl);
    }

    public Person login(String nutzername, String passwort) {
        var login = personenService.login(nutzername, passwort);
        ereignisService.getInstance().loginEreignis(login);
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
        EreignisService.getInstance().mitarbeiterAusgebenEreignis(personenService.getMitarbeiter());
        return personenService.getMitarbeiter();
    }

    public List<Mitarbeiter> getMitarbeiterList(String suchbegriff) {
        EreignisService.getInstance().mitarbeiterSuchenEreignis(personenService.suchePersonByQuery(suchbegriff)
                .filter(Mitarbeiter.class::isInstance)
                .map(Mitarbeiter.class::cast).toList(), suchbegriff);
        return personenService.suchePersonByQuery(suchbegriff)
                .filter(Mitarbeiter.class::isInstance)
                .map(Mitarbeiter.class::cast).toList();
    }

    public void mitarbeiterLoeschen(int mitarbeiterId) throws PersonNichtGefundenException {
        if (eingeloggterNutzer.getPersNr() != mitarbeiterId) {
            EreignisService.getInstance().mitarbeiterLoeschenEreignis(mitarbeiterId);
            personenService.removeMitarbeiter(mitarbeiterId);
            return;
        }
        throw new PersonNichtGefundenException(mitarbeiterId);
    }

    public void aendereArtikelBestand(int artikelId, int bestand) throws ArtikelNichtGefundenException {
        artikelService.aendereArtikelBestand(artikelId, bestand);
        EreignisService.getInstance().bestandAenderungEreignis(ArtikelService.getInstance().getArtikelByArtNr(artikelId));
    }

    public ArrayList<Ereignis> getEreignisList() {
        EreignisService.getInstance().ereignislistAusgabeEreignis(ereignisService.kundeOderMitarbeiterEreignisListe());
        return ereignisService.kundeOderMitarbeiterEreignisListe();
    }

    public String rechnungErstellen() {
        return bestellService.rechnungtoString();
    }

    public void kaufen() {
        EreignisService.getInstance().gekauftEreignis(ArtikelService.getInstance().getArtikelList());
        bestellService.kaufen();
    }

    public BestandsHistorie artikelBestandSuche(int ArtNr) throws ArtikelNichtGefundenException {
        return BestandshistorieService.getInstance().suchBestandshistorie(ArtNr);
    }
}

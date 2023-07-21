package com.centerio.eshopfx.shop.domain;

import com.centerio.eshopfx.shop.domain.exceptions.artikel.AnzahlPackgroesseException;
import com.centerio.eshopfx.shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.domain.exceptions.personen.PasswortNameException;
import com.centerio.eshopfx.shop.domain.exceptions.personen.PersonNichtGefundenException;
import com.centerio.eshopfx.shop.domain.exceptions.personen.PersonVorhandenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.RechnungNichtGefundenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.entities.*;
import com.centerio.eshopfx.shop.entities.enums.EreignisTyp;
import com.centerio.eshopfx.shop.entities.enums.KategorieEreignisTyp;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ShopAPI extends UnicastRemoteObject implements RemoteInterface, RemoteSingletonService, Serializable {

    private final ArtikelService artikelService;
    private final PersonenService personenService;
    private final WarenkorbService warenkorbService;
    private final HistorienService historienService;
    private final BestellService bestellService;
    private static transient ShopAPI instance;


    private ShopAPI() throws RemoteException {
        try {
            artikelService = ArtikelService.getInstance();
            personenService = PersonenService.getInstance();
            warenkorbService = WarenkorbService.getInstance();
            historienService = HistorienService.getInstance();
            bestellService = new BestellService();
            try {
                registrieren(new Mitarbeiter(1, "admin", "admin", "admin"));
                registrieren(new Kunde(2, "kunde", "kunde", new Adresse(
                        "Musterstrasse", "1", "2222", "Musterstadt"
                ), "kunde"));
            } catch (PersonVorhandenException e) {
                System.out.println("Standardpersonen bereits registriert.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ShopAPI getInstance() throws RemoteException {
        if (instance == null) {
            instance = new ShopAPI();
        }
        return instance;
    }

    public void speichern() {
        try {
            artikelService.save();
            personenService.save();
            historienService.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addArtikel(Artikel artikel) throws IOException {
        artikelService.addArtikel(artikel);
        historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANLEGEN, artikel, true);

    }

    public void removeArtikel(int artikelNr) throws ArtikelNichtGefundenException, IOException {
        try {
            var artikel = artikelService.getArtikelByArtNr(artikelNr);
            artikelService.removeArtikel(artikel);
            historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_LOESCHEN, artikel, true);
        } catch (ArtikelNichtGefundenException e) {
            historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_LOESCHEN, artikelNr, false);
            throw e;
        }
    }

    public List<Artikel> getArtikelList() throws IOException {
        var artikelListe = artikelService.getArtikelList();
        historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANZEIGEN, artikelService.getArtikelList().size(), artikelListe != null);
        return artikelListe;
    }

    public List<Artikel> getArtikelByQuery(String query) {
        var artikelListe = artikelService.sucheArtikelByQuery(query);
        //historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANZEIGEN, query, artikelListe != null);
        //historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_SUCHEN, query, artikelListe != null);
        return artikelListe;
    }

    public Artikel getArtikelByArtNr(int artikelNr) throws ArtikelNichtGefundenException, IOException {
        try {
            var artikel = artikelService.getArtikelByArtNr(artikelNr);
            historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANZEIGEN, artikel, true);
            return artikel;
        } catch (ArtikelNichtGefundenException e) {
            historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANZEIGEN, artikelNr, false);
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Warenkorb getWarenkorb() {
        System.out.println(UserContext.getUser().getPersNr());
        var warenkorb = warenkorbService.getWarenkorb();
        return warenkorb;
    }

    public double getWarenkorbGesamtpreis() {
        return getWarenkorb().getGesamtSumme();
    }

    public boolean addArtikelToWarenkorb(int artikelNr, int anzahl)
            throws BestandUeberschrittenException, ArtikelNichtGefundenException,
            WarenkorbArtikelNichtGefundenException, IOException, AnzahlPackgroesseException {
        try {
            var erfolg = warenkorbService.legeArtikelImWarenkorb(artikelNr, anzahl);
            HistorienService.getInstance()
                    .addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_HINZUFUEGEN, ArtikelService.getInstance().getArtikelByArtNr(artikelNr), erfolg, anzahl);
            return erfolg;
        } catch (Exception e) {
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_HINZUFUEGEN, artikelNr, false);
            throw e;
        }
    }

    public void aendereArtikelAnzahlImWarenkorb(int artikelNr, int anzahl)
            throws BestandUeberschrittenException, ArtikelNichtGefundenException,
            WarenkorbArtikelNichtGefundenException, IOException, AnzahlPackgroesseException {
        try {
            warenkorbService.aendereWarenkorbArtikelAnzahl(artikelNr, anzahl);
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_AENDERN, artikelService.getArtikelByArtNr(artikelNr), true);
        } catch (Exception e) {
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_AENDERN, artikelService.getArtikelByArtNr(artikelNr), false);
            throw e;
        }
    }

    public void entferneArtikelAnzahlImWarenkorb(int artikelNr, int anzahl)
            throws BestandUeberschrittenException, ArtikelNichtGefundenException,
            WarenkorbArtikelNichtGefundenException, IOException, AnzahlPackgroesseException {
        try {
            WarenkorbArtikel wartikel = warenkorbService.getWarenkorbArtikelByArtNr(artikelNr);
            warenkorbService.aendereWarenkorbArtikelAnzahl(artikelNr, wartikel.getAnzahl() - anzahl);
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_AENDERN, artikelService.getArtikelByArtNr(artikelNr), true);
        } catch (Exception e) {
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_AENDERN, artikelService.getArtikelByArtNr(artikelNr), false);
            throw e;
        }
    }

    public Person login(String nutzername, String passwort) throws IOException, PasswortNameException {
        var login = personenService.login(nutzername, passwort);
        UserContext.setUser(login);
        if (warenkorbService.getWarenkorb() == null && login instanceof Kunde kunde) {
            warenkorbService.neuerKorb(kunde);
        }
        HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.LOGIN, login, true);
        return login;
    }


    public Person registrieren(Person person) throws PersonVorhandenException, IOException {
        HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.MITARBEITER_ANLEGEN, person, true);
        return personenService.registerPerson(person);
    }

    public int getNaechstePersId() {
        return personenService.getNaechsteId();
    }

    public int getNaechsteArtikelId() {
        return artikelService.getNaechsteId();
    }

    public void artikelAktualisieren(Artikel artikel) throws ArtikelNichtGefundenException, IOException {
        try {
            artikelService.artikelAktualisieren(artikel);
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_AKTUALISIEREN, artikel, true);
        } catch (ArtikelNichtGefundenException e) {
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_AKTUALISIEREN, artikel, false);
            throw e;
        }
    }

    public boolean istNutzernameVerfuegbar(String nutzername) {
        return personenService.istNutzernameVerfuegbar(nutzername);
    }

    public List<Mitarbeiter> getMitarbeiterList() throws IOException {
        var mitarbeiterListe = personenService.getMitarbeiter();
        HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.MITARBEITER_ANZEIGEN, mitarbeiterListe.size(), true);
        return mitarbeiterListe;
    }

    public List<Mitarbeiter> getMitarbeiterList(String suchbegriff) throws IOException {
        HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.MITARBEITER_SUCHEN, suchbegriff, true);
        return personenService.suchePersonByQuery(suchbegriff)
                .filter(Mitarbeiter.class::isInstance)
                .map(Mitarbeiter.class::cast).toList();
    }

    public void mitarbeiterLoeschen(int mitarbeiterId) throws PersonNichtGefundenException, IOException {
        if (UserContext.getUser().getPersNr() != mitarbeiterId) {
            var person = personenService.getPersonByPersNr(mitarbeiterId);
            if (person instanceof Mitarbeiter mitarbeiter) {
                personenService.removeMitarbeiter(mitarbeiterId);
                HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.MITARBEITER_LOESCHEN, mitarbeiter, true);
                return;
            }
        }
        HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.MITARBEITER_LOESCHEN, null, false);
        throw new PersonNichtGefundenException(mitarbeiterId);
    }

    public void aendereArtikelBestand(int artikelId, int bestand) throws ArtikelNichtGefundenException, IOException {
        try {
            var erfolg = artikelService.aendereArtikelBestand(artikelId, bestand, false);
            var artikel = artikelService.getArtikelByArtNr(artikelId);
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.BESTANDAENDERUNG, artikel, erfolg);
        } catch (ArtikelNichtGefundenException e) {
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.BESTANDAENDERUNG, null, false);
            throw e;
        }
    }

    public ArrayList<Ereignis> getEreignisList() throws IOException {
       // var ereignisListe = historienService.kundeOderMitarbeiterEreignisListe();
       // HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.EREIGNIS_ANZEIGEN, ereignisListe.size(), true);
        return HistorienService.getInstance().getEreignisList();
    }

    public Rechnung erstelleRechnung() {
        return bestellService.erstelleRechnung();
    }

    public List<Rechnung> getRechnungenByKunde(int kundenNr) {
        return bestellService.getRechnungenByKunde(kundenNr);
    }

    public Rechnung getRechnungByRechnungsNr(int rechnungsNr) throws RechnungNichtGefundenException {
        return bestellService.getRechnung(rechnungsNr);
    }

    public void kaufen() throws BestandUeberschrittenException, ArtikelNichtGefundenException, IOException {
        try {
            var warenkorbListGroesse = WarenkorbService.getInstance().getWarenkorbList().size();
            bestellService.kaufen();
        } catch (Exception e) {
            HistorienService.getInstance()
                    .addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.KAUF, warenkorbService.getWarenkorb(), false);
            throw e;
        }
    }

    public List<Ereignis> sucheBestandshistorie(int artNr, int tage, Boolean istKauf)
            throws ArtikelNichtGefundenException, IOException {
        return historienService.suchBestandshistorie(artNr, tage, istKauf);
    }

    public List<Ereignis> suchPersonhistorie(int persNr, int tage) throws ArtikelNichtGefundenException, IOException {
        return historienService.suchPersonhistorie(persNr, tage, personenService.getPersonByPersNr(persNr));

    }

    public List<Ereignis> getUngefiltertePersonenhistorie() throws IOException {
        return HistorienService.getInstance().getUngefiltertPersonEreignishistorie();
    }

    public List<Ereignis> getUngefilterteArtikelhistorie() throws IOException {
        return HistorienService.getInstance().getUngefiltertArtikelEreignishistorie();
    }

    public List<Ereignis> getUngefilterteWarenkorbhistorie() throws IOException {
        return HistorienService.getInstance().getUngefiltertWarenkorbEreignishistorie();
    }

    public Person getPersonFromPersonList(int persNr) {
        return personenService.getPersonByPersNr(persNr);
    }

    public void logout() throws IOException {
        HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.LOGOUT, UserContext.getUser(), true);
        UserContext.clearUser();
    }

    public List<Person> getPersonList(){
        return personenService.getPersonList();
    }

    public void accountLoeschen() {
        try {

            personenService.removeMitarbeiter(UserContext.getUser().getPersNr());
        } catch (PersonNichtGefundenException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RemoteInterface getSingletonInstance() throws RemoteException {
        return getInstance();
    }
}

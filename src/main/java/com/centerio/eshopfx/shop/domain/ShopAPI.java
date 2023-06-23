package com.centerio.eshopfx.shop.domain;

import com.centerio.eshopfx.shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.domain.exceptions.personen.PersonNichtGefundenException;
import com.centerio.eshopfx.shop.domain.exceptions.personen.PersonVorhandenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.RechnungNichtGefundenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.entities.*;
import com.centerio.eshopfx.shop.entities.enums.EreignisTyp;
import com.centerio.eshopfx.shop.entities.enums.KategorieEreignisTyp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShopAPI {

    private final ArtikelService artikelService;
    private final PersonenService personenService;
    private final WarenkorbService warenkorbService;
    private final HistorienService historienService;
    private final BestellService bestellService;
    private static ShopAPI instance;


    private ShopAPI() {
        try {
            artikelService = ArtikelService.getInstance();
            personenService = new PersonenService();
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

    public static ShopAPI getInstance() {
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

    public void addArtikel(Artikel artikel) {
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

    public List<Artikel> getArtikelList() {
        var artikelListe = artikelService.getArtikelList();
        historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANZEIGEN, artikelService.getArtikelList().size(), artikelListe != null);
        return artikelListe;
    }

    public List<Artikel> getArtikelByQuery(String query) {
        var artikelListe = artikelService.sucheArtikelByQuery(query);
        historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANZEIGEN, query, artikelListe != null);
        historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_SUCHEN, query, artikelListe != null);
        return artikelListe;
    }

    public Artikel getArtikelByArtNr(int artikelNr) throws ArtikelNichtGefundenException {
        try {
            var artikel = artikelService.getArtikelByArtNr(artikelNr);
            historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANZEIGEN, artikel, true);
            return artikel;
        } catch (ArtikelNichtGefundenException e) {
            historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANZEIGEN, artikelNr, false);
            throw e;
        }
    }

    public Warenkorb getWarenkorb() {
        var warenkorb = warenkorbService.getWarenkorb();
        historienService.addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_ANZEIGEN, warenkorbService.getWarenkorb(), warenkorb != null);
        return warenkorb;
    }

    public double getWarenkorbGesamtpreis() {
        return getWarenkorb().getGesamtSumme();
    }

    public boolean addArtikelToWarenkorb(int artikelNr, int anzahl)
            throws BestandUeberschrittenException, ArtikelNichtGefundenException,
            WarenkorbArtikelNichtGefundenException, IOException {
        try {
            var erfolg = warenkorbService.legeArtikelImWarenkorb(artikelNr, anzahl);
            HistorienService.getInstance()
                    .addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_HINZUFUEGEN, ArtikelService.getInstance().getArtikelByArtNr(artikelNr), erfolg);
            return erfolg;
        } catch (Exception e) {
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_HINZUFUEGEN, artikelNr, false);
            throw e;
        }
    }

    public void aendereArtikelAnzahlImWarenkorb(int artikelNr, int anzahl)
            throws BestandUeberschrittenException, ArtikelNichtGefundenException,
            WarenkorbArtikelNichtGefundenException, IOException {
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
            WarenkorbArtikelNichtGefundenException, IOException {
        try {
            warenkorbService.aendereWarenkorbArtikelAnzahl(artikelNr, warenkorbService.getWarenkorbArtikelByArtNr(artikelNr).getAnzahl() - anzahl);
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_AENDERN, artikelService.getArtikelByArtNr(artikelNr), true);
        } catch (Exception e) {
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_AENDERN, artikelService.getArtikelByArtNr(artikelNr), false);
            throw e;
        }
    }

    public Person login(String nutzername, String passwort) throws IOException {
        var login = personenService.login(nutzername, passwort);
        if (login == null) {
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.LOGIN, null, false);
            return null;
        }
        UserContext.setUser(login);
        if (warenkorbService.getWarenkorb() == null && login instanceof Kunde kunde) {
            warenkorbService.neuerKorb(kunde);
        }
        HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.LOGIN, login, true);
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
        var ereignisListe = historienService.kundeOderMitarbeiterEreignisListe();
        HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.EREIGNIS_ANZEIGEN, ereignisListe.size(), true);
        return ereignisListe;
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

    public Person getPersonFromPersonList(int persNr) {
        return personenService.getPersonByPersNr(persNr);
    }

    public void logout() throws IOException {
        UserContext.clearUser();
        HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.LOGOUT, null, true);
    }

    public List<Person> getPersonList(){
        return personenService.getPersonList();
    }

    public void accountLoeschen() {
        try {
            personenService.removeMitarbeiter(UserContext.getUser().getPersNr());
        } catch (PersonNichtGefundenException e) {
            throw new RuntimeException(e);
        }
    }
}

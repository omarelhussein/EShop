package shop.domain;

import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.domain.exceptions.personen.PersonNichtGefundenException;
import shop.domain.exceptions.personen.PersonVorhandenException;
import shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import shop.domain.exceptions.warenkorb.RechnungNichtGefundenException;
import shop.domain.exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import shop.entities.*;
import shop.entities.enums.EreignisTyp;
import shop.entities.enums.KategorieEreignisTyp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShopAPI {

    private final ArtikelService artikelService;
    private final PersonenService personenService;
    private final WarenkorbService warenkorbService;
    private final EreignisService ereignisService;
    private final BestellService bestellService;



    public ShopAPI() throws IOException {
        artikelService = ArtikelService.getInstance();
        personenService = new PersonenService();
        warenkorbService = WarenkorbService.getInstance();
        ereignisService = EreignisService.getInstance();
        bestellService = new BestellService();
    }

    public void speichern() {
        try {
            artikelService.save();
            personenService.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addArtikel(Artikel artikel) {
        artikelService.addArtikel(artikel);
        ereignisService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANLEGEN, artikel, true);

    }

    public void removeArtikel(int artikelNr) throws ArtikelNichtGefundenException, IOException {
        try {
            var artikel = artikelService.getArtikelByArtNr(artikelNr);
            artikelService.removeArtikel(artikel);
            ereignisService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_LOESCHEN, artikel, true);
        } catch (ArtikelNichtGefundenException e) {
            ereignisService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_LOESCHEN, artikelNr, false);
            throw e;
        }
    }

    public List<Artikel> getArtikelList() {
        var artikelListe = artikelService.getArtikelList();
        ereignisService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANZEIGEN, artikelService.getArtikelList().size(), artikelListe != null);
        return artikelListe;
    }

    public List<Artikel> getArtikelByQuery(String query) {
        var artikelListe = artikelService.sucheArtikelByQuery(query);
        ereignisService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANZEIGEN, query, artikelListe != null);
        ereignisService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_SUCHEN, query, artikelListe != null);
        return artikelListe;
    }

    public Artikel getArtikelByArtNr(int artikelNr) throws ArtikelNichtGefundenException {
        try {
            var artikel = artikelService.getArtikelByArtNr(artikelNr);
            ereignisService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANZEIGEN, artikel, true);
            return artikel;
        } catch (ArtikelNichtGefundenException e) {
            ereignisService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANZEIGEN, artikelNr, false);
            throw e;
        }
    }

    public Warenkorb getWarenkorb() {
        var warenkorb = warenkorbService.getWarenkorb();
        ereignisService.addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_ANZEIGEN, warenkorbService.getWarenkorb(), warenkorb != null);
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
            EreignisService.getInstance()
                    .addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_HINZUFUEGEN, ArtikelService.getInstance().getArtikelByArtNr(artikelNr), erfolg);
            return erfolg;
        } catch (Exception e) {
            EreignisService.getInstance().addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_HINZUFUEGEN, artikelNr, false);
            throw e;
        }
    }

    public void aendereArtikelAnzahlImWarenkorb(int artikelNr, int anzahl)
            throws BestandUeberschrittenException, ArtikelNichtGefundenException,
            WarenkorbArtikelNichtGefundenException {
        try {
            warenkorbService.aendereWarenkorbArtikelAnzahl(artikelNr, anzahl);
            EreignisService.getInstance().addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_AENDERN, artikelService.getArtikelByArtNr(artikelNr), true);
        } catch (Exception e) {
            EreignisService.getInstance().addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_AENDERN, artikelService.getArtikelByArtNr(artikelNr), false);
            throw e;
        }
    }

    public void login(String nutzername, String passwort) {
        var login = personenService.login(nutzername, passwort);
        if (login == null) {
            EreignisService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.LOGIN, null, false);
            return;
        }
        UserContext.setUser(login);
        if (warenkorbService.getWarenkorb() == null && login instanceof Kunde kunde) {
            warenkorbService.neuerKorb(kunde);
        }
        EreignisService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.LOGIN, login, true);
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
            EreignisService.getInstance().addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_AKTUALISIEREN, artikel, true);
        } catch (ArtikelNichtGefundenException e) {
            EreignisService.getInstance().addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_AKTUALISIEREN, artikel, false);
            throw e;
        }
    }

    public boolean istNutzernameVerfuegbar(String nutzername) {
        return personenService.istNutzernameVerfuegbar(nutzername);
    }

    public List<Mitarbeiter> getMitarbeiterList() {
        var mitarbeiterListe = personenService.getMitarbeiter();
        EreignisService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.MITARBEITER_ANZEIGEN, mitarbeiterListe.size(), true);
        return mitarbeiterListe;
    }

    public List<Mitarbeiter> getMitarbeiterList(String suchbegriff) {
        EreignisService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.MITARBEITER_SUCHEN, suchbegriff, true);
        return personenService.suchePersonByQuery(suchbegriff)
                .filter(Mitarbeiter.class::isInstance)
                .map(Mitarbeiter.class::cast).toList();
    }

    public void mitarbeiterLoeschen(int mitarbeiterId) throws PersonNichtGefundenException {
        if (UserContext.getUser().getPersNr() != mitarbeiterId) {
            var person = personenService.getPersonByPersNr(mitarbeiterId);
            if (person instanceof Mitarbeiter mitarbeiter) {
                personenService.removeMitarbeiter(mitarbeiterId);
                EreignisService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.MITARBEITER_LOESCHEN, mitarbeiter, true);
                return;
            }
        }
        EreignisService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.MITARBEITER_LOESCHEN, null, false);
        throw new PersonNichtGefundenException(mitarbeiterId);
    }

    public void aendereArtikelBestand(int artikelId, int bestand) throws ArtikelNichtGefundenException, IOException {
        try {
            var erfolg = artikelService.aendereArtikelBestand(artikelId, bestand, false);
            var artikel = artikelService.getArtikelByArtNr(artikelId);
            EreignisService.getInstance().addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.BESTANDAENDERUNG, artikel, erfolg);
        } catch (ArtikelNichtGefundenException e) {
            EreignisService.getInstance().addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.BESTANDAENDERUNG, null, false);
            throw e;
        }
    }

    public ArrayList<Ereignis> getEreignisList() {
        var ereignisListe = ereignisService.kundeOderMitarbeiterEreignisListe();
        EreignisService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.EREIGNIS_ANZEIGEN, ereignisListe.size(), true);
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
            EreignisService.getInstance()
                    .addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.KAUF, warenkorbService.getWarenkorb(), false);
            throw e;
        }
    }

    public List<Ereignis> sucheBestandshistorie(int artNr, int tage, Boolean istKauf)
            throws ArtikelNichtGefundenException, IOException {
        return ereignisService.suchBestandshistorie(artNr, tage, istKauf);
    }

    public List<Ereignis> suchPersonhistorie(int persNr) throws ArtikelNichtGefundenException, IOException {
        return ereignisService.suchPersonhistorie(personenService.getPersonByPersNr(persNr));

    }
    public void logout() {
        UserContext.clearUser();
        EreignisService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.LOGOUT, null, true);
    }
}

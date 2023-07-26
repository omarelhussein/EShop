package domain;


import entities.*;
import entities.enums.EreignisTyp;
import entities.enums.KategorieEreignisTyp;
import exceptions.artikel.AnzahlPackgroesseException;
import exceptions.artikel.ArtikelNichtGefundenException;
import exceptions.personen.PasswortNameException;
import exceptions.personen.PersonNichtGefundenException;
import exceptions.personen.PersonVorhandenException;
import exceptions.warenkorb.BestandUeberschrittenException;
import exceptions.warenkorb.RechnungNichtGefundenException;
import exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ShopAPIImpl implements ShopAPI {

    private final ArtikelService artikelService;
    private final PersonenService personenService;
    private final WarenkorbService warenkorbService;
    private final HistorienService historienService;
    private final BestellService bestellService;
    private final List<ShopEventListener> listeners;


    public ShopAPIImpl() throws RemoteException {
        try {
            artikelService = ArtikelService.getInstance();
            personenService = PersonenService.getInstance();
            warenkorbService = WarenkorbService.getInstance();
            historienService = HistorienService.getInstance();
            bestellService = new BestellService();
            listeners = new ArrayList<>();
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

    public void speichern() throws RemoteException {
        try {
            artikelService.save();
            fireArtikelChangedEvent();
            personenService.save();
            historienService.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addArtikel(Artikel artikel) throws IOException {
        artikelService.addArtikel(artikel);
        historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANLEGEN, artikel, true);
        fireArtikelChangedEvent();
    }

    public void removeArtikel(int artikelNr) throws ArtikelNichtGefundenException, IOException {
        try {
            var artikel = artikelService.getArtikelByArtNr(artikelNr);
            artikelService.aendereArtikelBestand(artikelNr, 0, false);
            historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_LOESCHEN, artikel, true);
            fireArtikelChangedEvent();
        } catch (ArtikelNichtGefundenException e) {
            historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_LOESCHEN, artikelNr, false);
            throw e;
        }
    }

    public List<Artikel> getArtikelList() throws IOException {
        var artikelListe = artikelService.getArtikelList();
        return artikelListe;
    }

    public List<Artikel> getArtikelByQuery(String query) throws RemoteException {
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
        return warenkorbService.getWarenkorb();
    }

    public double getWarenkorbGesamtpreis() {
        return getWarenkorb().getGesamtSumme();
    }

    public boolean addArtikelToWarenkorb(int artikelNr, int anzahl)
            throws BestandUeberschrittenException, ArtikelNichtGefundenException,
            WarenkorbArtikelNichtGefundenException, IOException, AnzahlPackgroesseException {
        try {
            var erfolg = warenkorbService.legeArtikelImWarenkorb(artikelNr, anzahl);
            System.out.println("USER LEGT ARTIKEL IM WARENKORB: " + UserContext.getUser().getNutzername());
            System.out.println("THREAD: " + Thread.currentThread().getName());
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
            warenkorbService.aendereWarenkorbArtikelAnzahl(artikelNr, anzahl, false);
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
            WarenkorbArtikel warenkorbArtikel = warenkorbService.getWarenkorbArtikelByArtNr(artikelNr);
            warenkorbService.aendereWarenkorbArtikelAnzahl(artikelNr, warenkorbArtikel.getAnzahl() - anzahl, false);
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_AENDERN, artikelService.getArtikelByArtNr(artikelNr), true);
        } catch (Exception e) {
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.WARENKORB_EREIGNIS, EreignisTyp.WARENKORB_AENDERN, artikelService.getArtikelByArtNr(artikelNr), false);
            throw e;
        }
    }

    public synchronized int getWarenkorbArtikelAnzahl(int artNr) {
        WarenkorbArtikel warenkorbArtikel = warenkorbService.getWarenkorbArtikelByArtNr(artNr);
        if (warenkorbArtikel == null) return 0;
        return warenkorbArtikel.getAnzahl();
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
        if (!person.getNutzername().equals("admin") && !person.getNutzername().equals("kunde")) {
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.MITARBEITER_ANLEGEN, person, true);
        }
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
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_AKTUALISIEREN, artikelService.getArtikelByArtNr(artikel.getArtNr()), true);
            artikelService.artikelAktualisieren(artikel);
            fireArtikelChangedEvent();
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
            var artikel = artikelService.getArtikelByArtNr(artikelId);
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.BESTANDAENDERUNG, artikel, true);
            artikelService.aendereArtikelBestand(artikelId, bestand, false);
            fireArtikelChangedEvent();
        } catch (ArtikelNichtGefundenException e) {
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.BESTANDAENDERUNG, null, false);
            throw e;
        }
    }

    public List<Ereignis> getEreignisList() throws IOException {
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
            bestellService.kaufen();
            fireArtikelChangedEvent();
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

    @Override
    public void addEreignis(KategorieEreignisTyp ereignisKategorie, EreignisTyp ereignisTyp, Object obj, boolean erfolg, int anzahl) throws IOException {
        HistorienService.getInstance().addEreignis(ereignisKategorie, ereignisTyp, obj, erfolg, anzahl);
    }

    @Override
    public void addEreignis(KategorieEreignisTyp ereignisKategorie, EreignisTyp ereignisTyp, Object obj, boolean erfolg) throws IOException {
        HistorienService.getInstance().addEreignis(ereignisKategorie, ereignisTyp, obj, erfolg);
    }

    public List<Person> getPersonList() {
        return personenService.getPersonList();
    }

    public void accountLoeschen() {
        try {

            personenService.removeMitarbeiter(UserContext.getUser().getPersNr());
        } catch (PersonNichtGefundenException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addShopEventListener(ShopEventListener listener) throws RemoteException {
        listeners.add(listener);
    }

    @Override
    public void removeShopEventListener(ShopEventListener listener) throws RemoteException {
        listeners.remove(listener);
    }

    /**
     * Hier wird der Event an alle Listener geschickt, dass sich die ArtikelListe geändert hat.
     * Das wird benötigt, damit die GUI aktualisiert wird.
     * <p>
     * Das passiert bei Kauf, Bestandänderung, Artikel hinzufügen, Artikel löschen usw.
     *
     * @throws RemoteException wenn ein Fehler auftritt
     */
    public void fireArtikelChangedEvent() throws RemoteException {
        System.out.println("fireArtikelChangedEvent");
        System.out.println("listeners: " + listeners.size());
        for (ShopEventListener listener : listeners) {
            new Thread(() -> {
                try {
                    listener.handleArtikelListChanged();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public List<ShopEventListener> getShopEventListeners() {
        return listeners;
    }

    public void setUserContext(Person user) throws RemoteException {
        UserContext.setUser(user);
    }

}

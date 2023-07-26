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

    /**
     * speichert die aktuellen Listen in Dateiform ab
     * @throws RemoteException
     */
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

    /**
     * fügt den angegebenen Artikel der Artikelliste hinzu
     * @param artikel
     * @throws IOException
     */
    public void addArtikel(Artikel artikel) throws IOException {
        artikelService.addArtikel(artikel);
        historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANLEGEN, artikel, true);
        fireArtikelChangedEvent();
    }

    /**
     * entfernt den Artikel mit der angegebenen Artikelnummer aus der Artikelliste
     * @param artikelNr
     * @throws ArtikelNichtGefundenException
     * @throws IOException
     */
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

    /**
     * gibt die Liste aller Artikel wieder aka das Lager
     * @return
     * @throws IOException
     */
    public List<Artikel> getArtikelList() throws IOException {
        var artikelListe = artikelService.getArtikelList();
        return artikelListe;
    }

    /**
     * returnt eine Liste mit Artikeln dessen Bezeichnung den angegebenen String/ dessen Artikelnummer
     * mit diesem übereinstimmt beinhalten.
     * @param query
     * @return
     * @throws RemoteException
     */
    public List<Artikel> getArtikelByQuery(String query) throws RemoteException {
        var artikelListe = artikelService.sucheArtikelByQuery(query);
        //historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_ANZEIGEN, query, artikelListe != null);
        //historienService.addEreignis(KategorieEreignisTyp.ARTIKEL_EREIGNIS, EreignisTyp.ARTIKEL_SUCHEN, query, artikelListe != null);
        return artikelListe;
    }

    /**
     * gibt den Artikel aus der Artikelliste mit der angegebenen Artikelnummer wieder
     * @param artikelNr
     * @return
     * @throws ArtikelNichtGefundenException
     * @throws IOException
     */
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

    /**
     * gibt den Warenkorb des aktuellen Kunden wieder
     * @return
     */
    public Warenkorb getWarenkorb() {
        return warenkorbService.getWarenkorb();
    }

    /**
     * gibt die Summe der Preise aller Artikel im Warenkorb wieder
     * @return
     */
    public double getWarenkorbGesamtpreis() {
        return getWarenkorb().getGesamtSumme();
    }

    /**
     * fügt die Anzahl des Artikels mit der angegebenen Artikelnummer dem Warenkorb hinzu
     * @param artikelNr
     * @param anzahl
     * @return
     * @throws BestandUeberschrittenException
     * @throws ArtikelNichtGefundenException
     * @throws WarenkorbArtikelNichtGefundenException
     * @throws IOException
     * @throws AnzahlPackgroesseException
     */
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

    /**
     * Verändert die Anzahl eines Artikels im Warenkorb
     * @param artikelNr
     * @param anzahl
     * @throws BestandUeberschrittenException
     * @throws ArtikelNichtGefundenException
     * @throws WarenkorbArtikelNichtGefundenException
     * @throws IOException
     * @throws AnzahlPackgroesseException
     */
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

    /**
     * entfernt die Anzahl des Artikels mit der angegebenen Artikelnummer aus dem Warenkorb
     * @param artikelNr
     * @param anzahl
     * @throws BestandUeberschrittenException
     * @throws ArtikelNichtGefundenException
     * @throws WarenkorbArtikelNichtGefundenException
     * @throws IOException
     * @throws AnzahlPackgroesseException
     */
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

    /**
     * gibt die Anzahl des Warenkorbartikels mit der angegebenen Artikelnummer wieder
     * @param artNr
     * @return
     */
    public synchronized int getWarenkorbArtikelAnzahl(int artNr) {
        WarenkorbArtikel warenkorbArtikel = warenkorbService.getWarenkorbArtikelByArtNr(artNr);
        if (warenkorbArtikel == null) return 0;
        return warenkorbArtikel.getAnzahl();
    }

    /**
     * logt den Nutzer mit dem angegebenen Nutzernamen und Passwort ein
     * @param nutzername
     * @param passwort
     * @return
     * @throws IOException
     * @throws PasswortNameException
     */
    public Person login(String nutzername, String passwort) throws IOException, PasswortNameException {
        var login = personenService.login(nutzername, passwort);
        UserContext.setUser(login);
        if (warenkorbService.getWarenkorb() == null && login instanceof Kunde kunde) {
            warenkorbService.neuerKorb(kunde);
        }
        HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.LOGIN, login, true);

        return login;
    }


    /**
     * registriert das angegebene Personenobjekt
     * @param person
     * @return
     * @throws PersonVorhandenException
     * @throws IOException
     */
    public Person registrieren(Person person) throws PersonVorhandenException, IOException {
        if (!person.getNutzername().equals("admin") && !person.getNutzername().equals("kunde")) {
            HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.MITARBEITER_ANLEGEN, person, true);
        }
        return personenService.registerPerson(person);
    }

    /**
     * gibt die nächstfreie Personennummer wieder
     * @return
     */
    public int getNaechstePersId() {
        return personenService.getNaechsteId();
    }

    /**
     * gibt die nächstfreie Artikelnummer wieder
     * @return
     */
    public int getNaechsteArtikelId() {
        return artikelService.getNaechsteId();
    }

    /**
     * aktuallisiert die Werte des angegebenen Artikels in der Artikelliste mit den Werten des angegebenen Artikels
     * @param artikel
     * @throws ArtikelNichtGefundenException
     * @throws IOException
     */
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

    /**
     * überprüft ob der angegebene Nutzername verfügbar ist
     * @param nutzername
     * @return
     */
    public boolean istNutzernameVerfuegbar(String nutzername) {
        return personenService.istNutzernameVerfuegbar(nutzername);
    }

    /**
     * gibt die Liste aller Mitarbeiter wieder
     * @return
     * @throws IOException
     */
    public List<Mitarbeiter> getMitarbeiterList() throws IOException {
        var mitarbeiterListe = personenService.getMitarbeiter();
        HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.MITARBEITER_ANZEIGEN, mitarbeiterListe.size(), true);
        return mitarbeiterListe;
    }

    /**
     * gibt die Liste aller Mitarbeiter dessen Name/Nutzername den angegebenen String beinhalten wieder
     * @param suchbegriff
     * @return
     * @throws IOException
     */
    public List<Mitarbeiter> getMitarbeiterList(String suchbegriff) throws IOException {
        HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.MITARBEITER_SUCHEN, suchbegriff, true);
        return personenService.suchePersonByQuery(suchbegriff)
                .filter(Mitarbeiter.class::isInstance)
                .map(Mitarbeiter.class::cast).toList();
    }

    /**
     * löscht den Mitarbeiter mit der angegebenen Personennummer
     * @param mitarbeiterId
     * @throws PersonNichtGefundenException
     * @throws IOException
     */
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

    /**
     * ersetzt den Bestand des Artikels mit der angegebenen Artikelnummer mit dem angegebenen Brstand
     * @param artikelId
     * @param bestand
     * @throws ArtikelNichtGefundenException
     * @throws IOException
     */
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

    /**
     * gibt die Liste aller Ereignisse wieder
     * @return
     * @throws IOException
     */
    public List<Ereignis> getEreignisList() throws IOException {
        // var ereignisListe = historienService.kundeOderMitarbeiterEreignisListe();
        // HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.EREIGNIS_ANZEIGEN, ereignisListe.size(), true);
        return HistorienService.getInstance().getEreignisList();
    }

    /**
     * ersellt ein Rechnungsobjekt und returnt dieses
     * @return
     */
    public Rechnung erstelleRechnung() {
        return bestellService.erstelleRechnung();
    }

    /**
     * gibt die Liste aller Rechnungen eines Kunden mit der angegebenen Personennummer wieder
     * @param kundenNr
     * @return
     */
    public List<Rechnung> getRechnungenByKunde(int kundenNr) {
        return bestellService.getRechnungenByKunde(kundenNr);
    }

    /**
     * gibt die Rechnung mit der angegebenen Rechnungsnummer aus der Rechnungsliste wieder
     * @param rechnungsNr
     * @return
     * @throws RechnungNichtGefundenException
     */
    public Rechnung getRechnungByRechnungsNr(int rechnungsNr) throws RechnungNichtGefundenException {
        return bestellService.getRechnung(rechnungsNr);
    }

    /**
     * kauft den Warenkorb des aktuellen Kunden und aktuallisiert die Artikeltabelle aller Clients
     * @throws BestandUeberschrittenException
     * @throws ArtikelNichtGefundenException
     * @throws IOException
     */
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

    /**
     * gibt eine Liste von Ereignissen des Artikels mit der angegebenen Artikelnummer der letzten angebenen Tage an
     * @param artNr
     * @param tage
     * @param istKauf
     * @return
     * @throws ArtikelNichtGefundenException
     * @throws IOException
     */
    public List<Ereignis> sucheBestandshistorie(int artNr, int tage, Boolean istKauf)
            throws ArtikelNichtGefundenException, IOException {
        return historienService.suchBestandshistorie(artNr, tage, istKauf);
    }

    /**
     * gibt eine Liste von Ereignissen der Person mit der angegebenen Personennummer der letzten angebenen Tage an
     * @param persNr
     * @param tage
     * @return
     * @throws ArtikelNichtGefundenException
     * @throws IOException
     */
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

    /**
     * gibt das Personenobjekt der Person mit der angegebenen Personennummer wieder
     * @param persNr
     * @return
     */
    public Person getPersonFromPersonList(int persNr) {
        return personenService.getPersonByPersNr(persNr);
    }

    /**
     * loggt den eingeloggten Nutzer aus
     * @throws IOException
     */
    public void logout() throws IOException {
        HistorienService.getInstance().addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.LOGOUT, UserContext.getUser(), true);
        UserContext.clearUser();
    }

    /**
     * Fügt ein Artikelereignis mit den angegebenen Parametern der Ereignisliste hinzu
     * @param ereignisKategorie
     * @param ereignisTyp
     * @param obj
     * @param erfolg
     * @param anzahl
     * @throws IOException
     */
    @Override
    public void addEreignis(KategorieEreignisTyp ereignisKategorie, EreignisTyp ereignisTyp, Object obj, boolean erfolg, int anzahl) throws IOException {
        HistorienService.getInstance().addEreignis(ereignisKategorie, ereignisTyp, obj, erfolg, anzahl);
    }

    /**
     * Fügt ein Ereignis mit den angegebenen Parametern der Ereignisliste hinzu
     * @param ereignisKategorie
     * @param ereignisTyp
     * @param obj
     * @param erfolg
     * @throws IOException
     */
    @Override
    public void addEreignis(KategorieEreignisTyp ereignisKategorie, EreignisTyp ereignisTyp, Object obj, boolean erfolg) throws IOException {
        HistorienService.getInstance().addEreignis(ereignisKategorie, ereignisTyp, obj, erfolg);
    }

    /**
     * gibt die Liste aller Personen wieder
     * @return
     */
    public List<Person> getPersonList() {
        return personenService.getPersonList();
    }

    /**
     * löscht den Account des eingeloggten Mitarbeiters
     */
    public void accountLoeschen() {
        try {

            personenService.removeMitarbeiter(UserContext.getUser().getPersNr());
        } catch (PersonNichtGefundenException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * fügt der Liste der Listener einen Listener hinzu
     * @param listener
     * @throws RemoteException
     */
    @Override
    public void addShopEventListener(ShopEventListener listener) throws RemoteException {
        listeners.add(listener);
    }

    /**
     * löscht den angegebenen Listener aus der Listenerliste
     * @param listener
     * @throws RemoteException
     */
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

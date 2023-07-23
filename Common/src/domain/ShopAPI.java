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
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface ShopAPI extends Remote {

    void speichern() throws RemoteException;

    void addArtikel(Artikel artikel) throws IOException, RemoteException;

    void removeArtikel(int artikelNr) throws ArtikelNichtGefundenException, IOException, RemoteException;

    List<Artikel> getArtikelList() throws IOException, RemoteException;

    List<Artikel> getArtikelByQuery(String query) throws RemoteException;

    Artikel getArtikelByArtNr(int artikelNr) throws ArtikelNichtGefundenException, IOException, RemoteException;

    Warenkorb getWarenkorb() throws RemoteException;

    double getWarenkorbGesamtpreis() throws RemoteException;

    boolean addArtikelToWarenkorb(int artikelNr, int anzahl) throws BestandUeberschrittenException, ArtikelNichtGefundenException, WarenkorbArtikelNichtGefundenException, IOException, AnzahlPackgroesseException, RemoteException;

    void aendereArtikelAnzahlImWarenkorb(int artikelNr, int anzahl) throws BestandUeberschrittenException, ArtikelNichtGefundenException, WarenkorbArtikelNichtGefundenException, IOException, AnzahlPackgroesseException, RemoteException;

    void entferneArtikelAnzahlImWarenkorb(int artikelNr, int anzahl) throws BestandUeberschrittenException, ArtikelNichtGefundenException, WarenkorbArtikelNichtGefundenException, IOException, AnzahlPackgroesseException, RemoteException;

    Person login(String nutzername, String passwort) throws IOException, PasswortNameException, RemoteException;

    Person registrieren(Person person) throws PersonVorhandenException, IOException, RemoteException;

    int getNaechstePersId() throws RemoteException;

    int getNaechsteArtikelId() throws RemoteException;

    void artikelAktualisieren(Artikel artikel) throws ArtikelNichtGefundenException, IOException, RemoteException;

    boolean istNutzernameVerfuegbar(String nutzername) throws RemoteException;

    List<Mitarbeiter> getMitarbeiterList() throws IOException, RemoteException;

    List<Mitarbeiter> getMitarbeiterList(String suchbegriff) throws IOException, RemoteException;

    void mitarbeiterLoeschen(int mitarbeiterId) throws PersonNichtGefundenException, IOException, RemoteException;

    void aendereArtikelBestand(int artikelId, int bestand) throws ArtikelNichtGefundenException, IOException, RemoteException;

    ArrayList<Ereignis> getEreignisList() throws IOException, RemoteException;

    void addEreignis(KategorieEreignisTyp ereignisKategorie, EreignisTyp ereignisTyp, Object obj, boolean erfolg, int anzahl) throws IOException, RemoteException;
    void addEreignis(KategorieEreignisTyp ereignisKategorie, EreignisTyp ereignisTyp, Object obj, boolean erfolg) throws IOException, RemoteException;

    Rechnung erstelleRechnung() throws RemoteException;

    List<Rechnung> getRechnungenByKunde(int kundenNr) throws RemoteException;

    Rechnung getRechnungByRechnungsNr(int rechnungsNr) throws RechnungNichtGefundenException, RemoteException;

    void kaufen() throws BestandUeberschrittenException, ArtikelNichtGefundenException, IOException, RemoteException;

    List<Ereignis> sucheBestandshistorie(int artNr, int tage, Boolean istKauf) throws ArtikelNichtGefundenException, IOException, RemoteException;

    List<Ereignis> suchPersonhistorie(int persNr, int tage) throws ArtikelNichtGefundenException, IOException, RemoteException;

    List<Ereignis> getUngefiltertePersonenhistorie() throws IOException, RemoteException;

    List<Ereignis> getUngefilterteArtikelhistorie() throws IOException, RemoteException;

    List<Ereignis> getUngefilterteWarenkorbhistorie() throws IOException, RemoteException;

    Person getPersonFromPersonList(int persNr) throws RemoteException;

    void logout() throws IOException, RemoteException;

    List<Person> getPersonList() throws RemoteException;

    void accountLoeschen() throws RemoteException;
}
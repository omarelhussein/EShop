package shop.ui.cui;

import shop.domain.ShopAPI;
import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.domain.exceptions.personen.PersonNichtGefundenException;
import shop.domain.exceptions.personen.PersonVorhandenException;
import shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import shop.domain.exceptions.warenkorb.RechnungNichtGefundenException;
import shop.domain.exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import shop.entities.*;
import shop.utils.SeedingUtils;
import shop.utils.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class EShopCUI {

    private final ShopAPI shopAPI;
    private final EShopCUIMenue cuiMenue;
    private final EShopCUIMenue.Kunde kundenMenue;
    private final EShopCUIMenue.Mitarbeiter mitarbeiterMenue;
    private final Scanner in;

    public EShopCUI() {
        this.cuiMenue = new EShopCUIMenue();
        shopAPI = new ShopAPI();
        in = new Scanner(System.in);
        kundenMenue = new EShopCUIMenue.Kunde();
        mitarbeiterMenue = new EShopCUIMenue.Mitarbeiter();
        new SeedingUtils(); // Nur initialisieren, die Daten werden automatisch angelegt
    }

    /**
     * Liest die Eingaben des Nutzers in der Console
     */
    private String eingabe() throws IOException {
        return in.nextLine();
    }

    private void loginRegistriereKunde() throws IOException {
        kundenMenue.loginRegistrierenAusgabe();
        String eingabe = eingabe();
        switch (eingabe) {
            case "1" -> login(false);
            case "2" -> kundeRegistrieren();
            case "b" -> run();
            default -> cuiMenue.falscheEingabeAusgabe();
        }
        if (UserContext.getUser() == null) {
            loginRegistriereKunde();
        } else {
            kundenMenueActions();
        }
    }

    private void loginMitarbeiter() throws IOException {
        mitarbeiterMenue.loginAusgabe();
        String eingabe = eingabe();
        switch (eingabe) {
            case "1" -> login(true);
            case "b" -> run();
            default -> cuiMenue.falscheEingabeAusgabe();
        }
        if (UserContext.getUser() != null &&
            UserContext.getUser() instanceof Mitarbeiter) {
            mitarbeiterMenueActions();
        } else {
            loginMitarbeiter();
        }
    }

    private void mitarbeiterMenueActions() {
        mitarbeiterMenue.menueAusgabe();
        String eingabe;
        try {
            eingabe = eingabe();
        } catch (IOException e) {
            System.out.println("Fehler bei der Eingabe!");
            mitarbeiterMenueActions();
            return;
        }
        switch (eingabe) {
            case "1" -> lagerverwaltungAusgabe();
            case "2" -> personalverwaltungAusgabe();
            case "3" -> ereignisListAusgeben();
            case "x" -> logout();
            case "q" -> exit();
            default -> cuiMenue.falscheEingabeAusgabe();
        }
        mitarbeiterMenueActions();
    }

    private void personalverwaltungAusgabe() {
        mitarbeiterMenue.personalverwaltungAusgabe();
        String eingabe;
        try {
            eingabe = eingabe();
        } catch (IOException e) {
            System.out.println("Fehler bei der Eingabe!");
            lagerverwaltungAusgabe();
            return;
        }
        switch (eingabe) {
            case "1" -> mitarbeiterListeAusgeben(shopAPI.getMitarbeiterList());
            case "2" -> mitarbeiterSuchenAusgabe();
            case "3" -> mitarbeiterRegistrieren();
            case "4" -> mitarbeiterLoeschen();
            case "b" -> mitarbeiterMenueActions();
            default -> cuiMenue.falscheEingabeAusgabe();
        }
        personalverwaltungAusgabe();
    }

    private void lagerverwaltungAusgabe() {
        mitarbeiterMenue.lagerverwaltungAusgabe();
        String eingabe;
        try {
            eingabe = eingabe();
            switch (eingabe) {
                case "1" -> artikelListeAusgeben(shopAPI.getArtikelList());
                case "2" -> artikelSuchen();
                case "3" -> artikelAnlegen();
                case "4" -> artikelLoeschen();
                case "5" -> artikelBearbeiten();
                case "6" -> artikelBestandAendern();
                case "7" -> artikelBestandhistorieSuchen();
                case "b" -> mitarbeiterMenueActions();
                default -> cuiMenue.falscheEingabeAusgabe();
            }
        } catch (IOException e) {
            System.out.println("Fehler bei der Eingabe!");
            lagerverwaltungAusgabe();
            return;
        }
        lagerverwaltungAusgabe();
    }

    public void artikelBestandhistorieSuchen() throws IOException {
        try {
            System.out.print("Geben sie die ID des Artikels ein, von welchem sie die Bestandshistorie ansehen wollen.\n> ");
            int suchId = Integer.parseInt(eingabe());
            System.out.print("Geben sie die Anzahl der Tage ein, die sie zurückblicken wollen (optional).\n> ");
            String tage = eingabe();
            System.out.print("Geben sie an, ob sie nur Käufe (k) oder Ein-/Auslagerungen (e) sehen wollen (optional).\n> ");
            String istKauf = eingabe();
            artikelBestandListeAusgeben(suchId, tage.isEmpty() ? 0 : Integer.parseInt(tage), istKaufFromString(istKauf));
        } catch (NumberFormatException e) {
            System.out.println("Ungültige Eingabe!");
        }
    }

    private void artikelBestandListeAusgeben(int artNr, int tage, Boolean istKauf) {
        try {
            var bestandshistorieGruppiert = shopAPI.sucheBestandshistorie(artNr, tage, istKauf)
                    .stream().collect(Collectors.groupingBy(ArtikelHistorie::artikel)); // gruppiert nach Artikel
            for (var gruppierung : bestandshistorieGruppiert.entrySet()) {
                var artikel = gruppierung.getKey();
                System.out.println(
                        "Bestandshistorie für Artikel \"" + artikel.getBezeichnung()
                        + "\" mit der Artikelnummer \"" + artikel.getArtNr() + "\":\n"
                );
                for (var artikelHistorie : gruppierung.getValue()) {
                    System.out.println(artikelHistorie.bestandshistorieItem().toString());
                }
            }
            System.out.println();
        } catch (ArtikelNichtGefundenException e) {
            System.out.println(e.getMessage());
            lagerverwaltungAusgabe();
        }
    }


    private void mitarbeiterListeAusgeben(List<Mitarbeiter> mitarbeiterListe) {
        if (mitarbeiterListe.isEmpty()) {
            System.out.println("Keine Mitarbeiter gefunden!");
            return;
        }
        System.out.println("Mitarbeiter:");
        for (Mitarbeiter mitarbeiter : mitarbeiterListe) {
            System.out.println(mitarbeiter.toString());
        }
        System.out.println();
    }

    private void mitarbeiterSuchenAusgabe() {
        System.out.print("Suchbegriff:\n> ");
        String suchbegriff = in.nextLine();
        mitarbeiterListeAusgeben(shopAPI.getMitarbeiterList(suchbegriff));
    }

    private void mitarbeiterRegistrieren() {
        System.out.print("Name:\n> ");
        String name = in.nextLine();
        System.out.print("Nutzername:\n> ");
        String nutzername = in.nextLine();
        System.out.print("Passwort:\n> ");
        String passwort = in.nextLine();
        try {
            var id = shopAPI.getNaechstePersId();
            shopAPI.registrieren(new Mitarbeiter(id, nutzername, name, passwort));
        } catch (PersonVorhandenException e) {
            System.out.println("Mitarbeiter konnte nicht angelegt werden! Erneut versuchen? (j/n)\n> ");
            String eingabe = in.nextLine();
            if (eingabe.equals("j")) {
                mitarbeiterRegistrieren();
            }
        }
    }

    private void mitarbeiterLoeschen() {
        System.out.print("Mitarbeiter-ID (0 zum Abbrechen):\n> ");
        try {
            int mitarbeiterId = Integer.parseInt(in.nextLine());
            if (mitarbeiterId == 0) {
                return;
            }
            shopAPI.mitarbeiterLoeschen(mitarbeiterId);
            System.out.println("Mitarbeiter erfolgreich gelöscht!");
        } catch (PersonNichtGefundenException e) {
            System.out.println("Mitarbeiter nicht gefunden!");
            mitarbeiterLoeschen();
        } catch (NumberFormatException e) {
            System.out.println("Fehler bei der Eingabe!");
            mitarbeiterLoeschen();
        }
        System.out.println();
    }

    private void artikelAnlegen() throws IOException, NumberFormatException {
        System.out.print("Bezeichnung:\n> ");
        String bezeichnung = eingabe();
        System.out.print("Preis (Einzelstückpreis bei Massenartikeln):\n> ");
        try {
            double preis = Double.parseDouble(eingabe());
            System.out.print("Bestand: (Anzahl der Packs bei Massenartikeln)\n> ");
            int bestand = Integer.parseInt(eingabe());

            if (massenArtikelAusgabe()) {
                System.out.print("Packgröße:\n> ");
                int pgroesse = Integer.parseInt(eingabe());
                var artikel = new Massenartikel(shopAPI.getNaechsteArtikelId(), bezeichnung, preis, bestand * pgroesse, pgroesse);
                shopAPI.addArtikel(artikel);
                System.out.println("Massenartikel erfolgreich angelegt! Artikel-ID: " + artikel.getArtNr() + "\n");
            } else {
                var artikel = new Artikel(shopAPI.getNaechsteArtikelId(), bezeichnung, preis, bestand);
                shopAPI.addArtikel(artikel);
                System.out.println("Artikel erfolgreich angelegt! Artikel-ID: " + artikel.getArtNr() + "\n");
            }
        } catch (NumberFormatException e) {
            System.out.println("Fehler beim Format");
            lagerverwaltungAusgabe();
        }
    }

    private boolean massenArtikelAusgabe() throws IOException {
        System.out.print("Massenartikel?(j/n)\n> ");
        String eingabe;
        try {
            eingabe = eingabe();
        } catch (IOException e) {
            System.out.println("Fehler bei der Eingabe!");
            return massenArtikelAusgabe();
        }
        switch (eingabe) {
            case "j":
                return true;
            case "n":
                return false;
            default:
                System.out.println("Bitte wiederhole die Eingabe. \n");
                return massenArtikelAusgabe();
        }

        /*
        if(eingabe().equals("j")) {
            return true;
        }
        if (eingabe().equals("n")) {
            return false;
        }
        else {
            System.out.print("Bitte wiederhole die Eingabe. \n");
            return Massenart();
        }
         */
    }


    private void artikelBearbeiten() throws IOException, NumberFormatException {
        System.out.print("Artikel-ID:\n> ");
        try {
            int artikelId = Integer.parseInt(in.nextLine());
            System.out.print("Neue Bezeichnung (optional):\n> ");
            String bezeichnung = nullIfEmpty(eingabe());
            System.out.print("Neuer Preis (optional):\n> ");
            String preis = nullIfEmpty(eingabe());
            System.out.print("Neuer Bestand (optional):\n> ");
            String bestand = nullIfEmpty(eingabe());

            double neuerPreis = Double.parseDouble(preis == null ? "-1" : preis);
            int neuerBestand = Integer.parseInt(bestand == null ? "-1" : bestand);
            if (massenArtikelAusgabe()) {
                System.out.print("Packgröße (optional):\n> ");
                String packgr = nullIfEmpty(eingabe());
                shopAPI.artikelAktualisieren(
                        new Massenartikel(artikelId, bezeichnung, neuerPreis, neuerBestand, Integer.parseInt(packgr == null ? "-1" : packgr))
                );
            } else {
                shopAPI.artikelAktualisieren(
                        new Artikel(artikelId, bezeichnung, neuerPreis, neuerBestand)
                );
            }
        } catch (ArtikelNichtGefundenException | NumberFormatException e) {
            System.out.println("Artikel nicht gefunden oder Eingabe Falsch!");
            lagerverwaltungAusgabe();
        }
        System.out.println("Artikel erfolgreich bearbeitet!\n");
    }

    private void artikelBestandAendern() throws IOException, NumberFormatException {
        System.out.print("Artikel-ID:\n> ");
        try {
            int artikelId = Integer.parseInt(in.nextLine());
            System.out.print("Neuer Bestand:\n> ");
            int bestand = Integer.parseInt(in.nextLine());
            shopAPI.aendereArtikelBestand(artikelId, bestand);
        } catch (ArtikelNichtGefundenException | NumberFormatException e) {
            System.out.println("Artikel nicht gefunden oder Eingabe falsch!");
            lagerverwaltungAusgabe();
        }
    }

    private void artikelLoeschen() throws IOException {
        System.out.print("Artikel-ID:\n> ");
        try {
            int artikelId = Integer.parseInt(eingabe());
            shopAPI.removeArtikel(artikelId);
        } catch (ArtikelNichtGefundenException e) {
            System.out.println("Artikel nicht gefunden, erneut versuchen? (j/n)");
            String eingabe = in.nextLine();
            if (eingabe.equals("j")) {
                artikelLoeschen();
                System.out.println("Artikel erfolgreich gelöscht!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Bitte geben Sie eine gültige Artikel-ID ein!");
            lagerverwaltungAusgabe();
        }
        System.out.println("Artikel erfolgreich gelöscht!");
    }

    private void artikelListeAusgeben(List<Artikel> artikelListe) {
        for (Artikel artikel : artikelListe) {
            System.out.println(artikel.toString());
        }
        System.out.println("\ni) Gesamtanzahl der ausgegebenen Artikel: " + artikelListe.size() + "\n");
    }

    private void ereignisListAusgeben() {
        var ereignisListe = shopAPI.getEreignisList();
        if (ereignisListe.isEmpty()) {
            System.out.println("Keine Ereignisse vorhanden!");
            return;
        }
        System.out.println(StringUtils.lineSeparator(20, " ~"));
        for (Ereignis ereignis : ereignisListe) {
            System.out.println(ereignis.toString());
        }
    }

    private boolean login(boolean istMitarbeiter) {
        try {
            System.out.println();
            System.out.print("Bitte geben Sie Ihr Nutzername ein:\n> ");
            var nutzername = eingabe();
            System.out.print("Bitte geben Sie Ihr Passwort ein:\n> ");
            var passwort = eingabe();
            shopAPI.login(nutzername, passwort);
            if (UserContext.getUser() == null) {
                System.out.println("Nutzername oder Passwort falsch!");
                return false;
            } else if (istMitarbeiter && UserContext.getUser() instanceof Kunde) {
                System.out.println("Sie sind nicht berechtigt sich in diesem Bereich einzuloggen!");
                return false;
            } else {
                System.out.println("Erfolgreich eingeloggt!\n");
                return true;
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Eingabe: " + e.getMessage());
            return false;
        }
    }

    private void logout() {
        shopAPI.logout();
        System.out.println("Erfolgreich ausgeloggt!");
        try {
            run();
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Eingabe: " + e.getMessage());
        }
    }

    private void kundeRegistrieren() {
        try {
            System.out.println();
            System.out.print("Bitte geben Sie Ihren Namen ein:\n> ");
            var name = eingabe();
            var nutzernameVerfuegbar = false;
            var nutzername = "";
            do {
                System.out.print("Bitte geben Sie Ihr Nutzername ein:\n> ");
                nutzername = eingabe();
                nutzernameVerfuegbar = shopAPI.istNutzernameVerfuegbar(nutzername);
                if (!nutzernameVerfuegbar) {
                    System.out.println("Nutzername bereits vergeben! Bitte versuchen Sie es erneut.");
                    Thread.sleep(500); // Damit der Nutzer die Fehlermeldung lesen kann, wird kurz gewartet
                }
            } while (!nutzernameVerfuegbar);
            System.out.print("Bitte geben Sie Ihr Passwort ein:\n> ");
            var passwort = eingabe();

            var registrierterNutzer = shopAPI.registrieren(new Kunde(
                    shopAPI.getNaechstePersId(),
                    nutzername,
                    name,
                    adresseAusgabe(),
                    passwort
            ));
            UserContext.setUser(registrierterNutzer);
            System.out.println("Erfolgreich registriert!");
            return;
        } catch (PersonVorhandenException e) {
            System.out.println("Nutzername bereits vergeben!");
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Eingabe: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        kundeRegistrieren();
    }

    private Adresse adresseAusgabe() throws IOException {
        System.out.print("Adresse:\nBitte geben Sie Ihre Straße ein:\n ");
        var Strasse = eingabe();
        System.out.print("Bitte geben Sie Ihre Hausnummer ein:\n> ");
        var Hausnummer = eingabe();
        System.out.print("Bitte geben Sie Ihre Postleitzahl ein:\n> ");
        var Postleitzahl = eingabe();
        System.out.print("Bitte geben Sie Ihren Herkunftsort ein:\n> ");
        var Stadt = eingabe();

        System.out.print("\nBitte überprüfen sie ihre Eingabe:\n\n" +
                         Strasse + " " + Hausnummer + " " + Postleitzahl + " " + Stadt + "\n\n" +
                         "Ist diese Adresse richtig?(j/n)\n> ");
        if (eingabe().equals("j")) {
            return (new Adresse(Strasse, Hausnummer, Postleitzahl, Stadt));
        } else return adresseAusgabe();
    }

    private void kundenMenueActions() {
        kundenMenue.menueAusgabe();
        try {
            var eingabe = eingabe();
            switch (eingabe) {
                case "1" -> artikelListeAusgeben(shopAPI.getArtikelList());
                case "2" -> artikelSuchen();
                case "3" -> warenkorbAnzeigen();
                case "4" -> warenkorbArtikelHinzufuegen();
                case "5" -> warenkorbBearbeiten();
                case "6" -> warenkorbBestellen();
                case "r" -> rechnungListAusgeben();
                case "e" -> ereignisListAusgeben();
                case "x" -> logout();
                case "q" -> exit();
                default -> cuiMenue.falscheEingabeAusgabe();
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Eingabe: " + e.getMessage());
        }
        kundenMenueActions();
    }

    private void rechnungListAusgeben() {
        var rechnungen = shopAPI.getRechnungenByKunde(UserContext.getUser().getPersNr());
        if (rechnungen.isEmpty()) {
            System.out.println("Sie haben noch keine Rechnungen. Erst wenn Sie etwas bestellt haben, werden hier Rechnungen angezeigt.");
            return;
        }
        System.out.println("\nRechnungen:");
        for (Rechnung rechnung : rechnungen) {
            System.out.println("\"" + rechnung.getRechnungsTitel() + "\" - Erstellt am: " + StringUtils.formatDate(rechnung.getRechnungsDatum()));
        }
        System.out.print("\nWelche Rechnung möchten Sie sich ansehen? (Bitte geben Sie die Rechnungsnummer ein, 0 zum Abbrechen)\n> ");
        try {
            var eingabe = eingabe();
            if (eingabe.equals("0")) {
                return;
            }
            var rechnung = shopAPI.getRechnungByRechnungsNr(Integer.parseInt(eingabe));
            System.out.println("\n" + rechnung.toString());
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Eingabe: " + e.getMessage());
        } catch (RechnungNichtGefundenException e) {
            System.out.println(e.getMessage());
        }
    }

    private void warenkorbBestellen() {
        if (shopAPI.getWarenkorb().istLeer()) {
            System.out.println("Warenkorb ist leer! Bitte zuerst Artikel hinzufügen und dann bestellen!");
            return;
        }
        System.out.println("\nRechnung wird erstellt...");
        try {
            Thread.sleep(500); // Laden simulieren
        } catch (InterruptedException e) {
            // Ignorieren
        }
        System.out.print(shopAPI.erstelleRechnung().toString());
        System.out.println("\nRechnung erfolgreich erstellt!");
        // kauf bestätigen
        System.out.print("\nBitte bestätigen Sie den Kauf. (j/n)\n> ");
        try {
            var eingabe = eingabe();
            if (eingabe.equals("j")) {
                shopAPI.kaufen();
                System.out.println("Warenkorb erfolgreich bestellt! Vielen Dank für Ihren Einkauf!");
            } else {
                System.out.println("Kauf abgebrochen!");
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Eingabe: " + e.getMessage());
        } catch (BestandUeberschrittenException | ArtikelNichtGefundenException e) {
            System.out.println(e.getMessage());
        }
    }

    private void warenkorbBearbeiten() {
        try {
            System.out.print("\nBitte geben Sie die Artikel-ID ein. (0 zum Abbrechen)\n> ");
            var artikelId = Integer.parseInt(eingabe());
            if (artikelId == 0) return;
            System.out.print("Bitte geben Sie die neue Anzahl ein. (0 zum Löschen)\n> ");
            var anzahl = Integer.parseInt(eingabe());
            shopAPI.aendereArtikelAnzahlImWarenkorb(artikelId, anzahl);
            System.out.println("Warenkorb Artikel erfolgreich geändert!");
        } catch (ArtikelNichtGefundenException | BestandUeberschrittenException |
                 WarenkorbArtikelNichtGefundenException e) {
            System.out.println(e.getMessage());
            warenkorbBearbeiten();
        } catch (NumberFormatException e) {
            System.out.println("Bitte geben Sie eine gültige Artikel-ID ein!");
            warenkorbBearbeiten();
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Eingabe: " + e.getMessage());
            warenkorbBearbeiten();
        }
    }

    private void warenkorbArtikelHinzufuegen() {
        try {
            System.out.print("\nBitte geben Sie die Artikel-ID ein. (0 zum Abbrechen)\n> ");
            var artikelId = Integer.parseInt(eingabe());
            if (artikelId == 0) return;
            System.out.print("Bitte geben Sie die Anzahl ein: (Anzahl der Packs bei Massenartikeln)\n> ");
            var anzahl = Integer.parseInt(eingabe());
            var erfolg = shopAPI.addArtikelToWarenkorb(artikelId, anzahl);
            if (erfolg) {
                System.out.println("Artikel erfolgreich hinzugefügt!");
            } else {
                System.out.println("Artikel konnte leider nicht hinzugefügt werden!");
            }
        } catch (ArtikelNichtGefundenException | BestandUeberschrittenException |
                 WarenkorbArtikelNichtGefundenException e) {
            System.out.println(e.getMessage());
            warenkorbArtikelHinzufuegen();
        } catch (NumberFormatException e) {
            System.out.println("Bitte geben Sie eine gültige Artikel-ID ein!");
            warenkorbArtikelHinzufuegen();
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Eingabe: " + e.getMessage());
            warenkorbArtikelHinzufuegen();
        }
    }

    private void artikelSuchen() throws IOException {
        System.out.print("Bitte geben Sie den Namen oder die Nummer des Artikels ein:\n> ");
        var artikelQuery = eingabe();
        var artikelList = shopAPI.getArtikelByQuery(artikelQuery);
        if (artikelList.isEmpty()) {
            System.out.println("Keine Artikel gefunden!");
        } else {
            artikelListeAusgeben(artikelList);
        }
    }

    private void warenkorbAnzeigen() {
        var warenkorb = shopAPI.getWarenkorb();
        if (warenkorb.getWarenkorbArtikelList().isEmpty()) {
            System.out.println("Warenkorb ist leer! \n");
        } else {
            for (WarenkorbArtikel warenkorbArtikel : warenkorb.getWarenkorbArtikelList()) {
                System.out.println(warenkorbArtikel.toString());
            }
            System.out.println("\ni) Gesamtanzahl der Artikel im Warenkorb: " + warenkorb.getAnzahlArtikel());
            System.out.println("i) Gesamtsumme: " + shopAPI.getWarenkorbGesamtpreis() + "€ \n");
        }
    }

    private void run() throws IOException {
        cuiMenue.bereichAuswahlAusgabe();
        String eingabe = eingabe();
        switch (eingabe) {
            case "1" -> loginRegistriereKunde();
            case "2" -> loginMitarbeiter();
            case "q" -> exit();
            default -> {
                cuiMenue.falscheEingabeAusgabe();
                run();
            }
        }
    }

    private void exit() {
        System.out.println("Auf Wiedersehen!");
        System.exit(0);
    }

    private String nullIfEmpty(String string) {
        return string.trim().isEmpty() ? null : string;
    }

    private Boolean istKaufFromString(String string) {
        if (string.equals("k")) return true;
        if (string.equals("e")) return false;
        return null;
    }

    public static void main(String[] args) throws IOException {
        EShopCUI cui = new EShopCUI();
        try {
            cui.shopAPI.registrieren(new Mitarbeiter(1, "admin", "admin", "admin"));
            cui.shopAPI.registrieren(new Kunde(2, "kunde", "kunde", new Adresse(
                    "Musterstrasse", "1", "2222", "Musterstadt"
            ), "kunde"));
        } catch (PersonVorhandenException e) {
            throw new RuntimeException(e);
        }
        cui.run();
    }

}

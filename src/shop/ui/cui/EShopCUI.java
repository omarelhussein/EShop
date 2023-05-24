package shop.ui.cui;

import shop.domain.EreignisService;
import shop.domain.ShopAPI;
import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.domain.exceptions.personen.PersonNichtGefundenException;
import shop.domain.exceptions.personen.PersonVorhandenException;
import shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import shop.domain.exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import shop.entities.*;
import shop.utils.SeedingUtils;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class EShopCUI {

    private final ShopAPI shopAPI;
    private final EShopCUIMenue cuiMenue;
    private final EShopCUIMenue.Kunde kundenMenue;
    private final EShopCUIMenue.Mitarbeiter mitarbeiterMenue;
    private final Scanner in;
    private final SeedingUtils seedingUtils;

    public EShopCUI() {
        this.cuiMenue = new EShopCUIMenue();
        shopAPI = new ShopAPI();
        in = new Scanner(System.in);
        kundenMenue = new EShopCUIMenue.Kunde();
        mitarbeiterMenue = new EShopCUIMenue.Mitarbeiter();
        seedingUtils = new SeedingUtils();
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
        if (shopAPI.getEingeloggterNutzer() == null) {
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
        if (shopAPI.getEingeloggterNutzer() != null &&
                shopAPI.getEingeloggterNutzer() instanceof Mitarbeiter) {
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
            personalverwaltungAusgabe();
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
        System.out.print("E-Mail:\n> ");
        String email = in.nextLine();
        System.out.print("Passwort:\n> ");
        String passwort = in.nextLine();
        try {
            var id = shopAPI.getNaechstePersId();
            shopAPI.registrieren(new Mitarbeiter(id, email, name, passwort));
        } catch (PersonVorhandenException e) {
            System.out.println("Mitarbeiter konnte nicht angelegt werden! Erneut versuchen? (j/n)\n> ");
            String eingabe = in.nextLine();
            if (eingabe.equals("j")) {
                mitarbeiterRegistrieren();
            }
        }
    }

    private void mitarbeiterLoeschen() {
        System.out.print("Mitarbeiter-ID:\n> ");
        try {
            int mitarbeiterId = Integer.parseInt(in.nextLine());
            shopAPI.mitarbeiterLoeschen(mitarbeiterId);
            System.out.println("Mitarbeiter erfolgreich gelöscht!");
        } catch (PersonNichtGefundenException e) {
            System.out.println("Mitarbeiter nicht gefunden!");
        }
        System.out.println();
    }

    private void artikelAnlegen() throws IOException {
        System.out.print("Bezeichnung:\n> ");
        String bezeichnung = eingabe();
        System.out.print("Preis:\n> ");
        double preis = Double.parseDouble(eingabe());
        System.out.print("Bestand: (Anzahl der Packs bei Massenartikeln)\n> ");
        int bestand = Integer.parseInt(eingabe());

        if (Massenart()) {
            System.out.print("Packgröße:\n> ");
            int pgroesse = Integer.parseInt(eingabe());
            var artikel = new Massenartikel(shopAPI.getNaechsteArtikelId(), bezeichnung, preis, bestand*pgroesse, pgroesse);
            shopAPI.addArtikel(artikel);
            System.out.println("Massenartikel erfolgreich angelegt! Artikel-ID: " + artikel.getArtNr() + "\n");
        } else {
            var artikel = new Artikel(shopAPI.getNaechsteArtikelId(), bezeichnung, preis, bestand);
            shopAPI.addArtikel(artikel);
            System.out.println("Artikel erfolgreich angelegt! Artikel-ID: " + artikel.getArtNr() + "\n");
        }
    }

    private boolean Massenart() throws IOException {
        System.out.print("Massenartikel?(ja/nein)\n> ");
        if(eingabe().equals("ja")) {
            return true;
        } else if (eingabe().equals("nein")) {
            return false;
        } else {
            System.out.print("Bitte wiederhole die Eingabe. \n");
            return Massenart();
        }

    }

    private void artikelBearbeiten() throws IOException {
        System.out.print("Artikel-ID:\n> ");
        try {
            int artikelId = Integer.parseInt(in.nextLine());
            System.out.print("Neue Bezeichnung (optional):\n> ");
            String bezeichnung = nullIfEmpty(eingabe());
            System.out.print("Neuer Preis (optional):\n> ");
            String preis = nullIfEmpty(eingabe());
            System.out.print("Neuer Bestand (optional):\n> ");
            String bestand = nullIfEmpty(eingabe());

            if (Massenart()) {
                System.out.print("Packgröße (optional):\n> ");
                String packgr = nullIfEmpty(eingabe());
                shopAPI.artikelAktualisieren(
                        new Massenartikel(
                                artikelId,
                                bezeichnung,
                                Double.parseDouble(preis == null ? "-1" : preis),
                                Integer.parseInt(bestand == null ? "-1" : bestand),
                                Integer.parseInt(packgr == null ? "-1" : packgr)
                        )
                );
            } else {
                shopAPI.artikelAktualisieren(
                        new Artikel(
                                artikelId,
                                bezeichnung,
                                Double.parseDouble(preis == null ? "-1" : preis),
                                Integer.parseInt(bestand == null ? "-1" : bestand)
                        )
                );
            }
        } catch (ArtikelNichtGefundenException e) {
            System.out.println("Artikel nicht gefunden!");
        }
        System.out.println("Artikel erfolgreich bearbeitet!\n");
    }

    private void artikelBestandAendern() throws IOException {
        System.out.print("Artikel-ID:\n> ");
        try {
            int artikelId = Integer.parseInt(in.nextLine());
            System.out.print("Neuer Bestand:\n> ");
            int bestand = Integer.parseInt(in.nextLine());
            shopAPI.aendereArtikelBestand(artikelId, bestand);
        } catch (ArtikelNichtGefundenException e) {
            System.out.println("Artikel nicht gefunden!");
        }
    }

    private void artikelLoeschen() throws IOException {
        System.out.print("Artikel-ID:\n> ");
        try {
            int artikelId = Integer.parseInt(eingabe());
            shopAPI.removeArtikel(artikelId);
        } catch (ArtikelNichtGefundenException e) {
            System.out.println("Artikel nicht gefunden, ernuet versuchen? (j/n)");
            String eingabe = in.nextLine();
            if (eingabe.equals("j")) {
                artikelLoeschen();
            }
        } catch (NumberFormatException e) {
            System.out.println("Bitte geben Sie eine gültige Artikel-ID ein!");
            artikelLoeschen();
        }
        System.out.println("Artikel erfolgreich gelöscht!");
    }

    private void artikelListeAusgeben(List<Artikel> artikelListe) {
        for (Artikel artikel : artikelListe) {
            System.out.println(artikel.toString());
        }
        System.out.println("Gesamtanzahl der ausgegebenen Artikel: " + artikelListe.size() + "\n");
    }

    private void ereignisListAusgeben() {
        var ereignisListe = shopAPI.getEreignisList();
        if (ereignisListe.isEmpty()) {
            System.out.println("Keine Ereignisse vorhanden!");
            return;
        }
        for (Ereignis ereignis : ereignisListe) {
            System.out.println(ereignis.toString());
        }
    }

    private boolean login(boolean istMitarbeiter) {
        try {
            System.out.println();
            System.out.print("Bitte geben Sie Ihren Nutzernamen ein:\n> ");
            var nutzername = eingabe();
            System.out.print("Bitte geben Sie Ihr Passwort ein:\n> ");
            var passwort = eingabe();
            shopAPI.setEingeloggterNutzer(shopAPI.login(nutzername, passwort));
            if (shopAPI.getEingeloggterNutzer() == null) {
                System.out.println("Nutzername oder Passwort falsch!");
                return false;
            } else if (istMitarbeiter && shopAPI.getEingeloggterNutzer() instanceof Kunde) {
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
        shopAPI.setEingeloggterNutzer(null);
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
            var emailVerfuegbar = false;
            var email = "";
            do {
                System.out.print("Bitte geben Sie Ihre E-Mail-Adresse ein:\n> ");
                email = eingabe();
                emailVerfuegbar = shopAPI.istEmailVerfuegbar(email);
                if (!emailVerfuegbar) {
                    System.out.println("Email bereits vergeben! Bitte versuchen Sie es erneut.");
                    Thread.sleep(500); // Damit der Nutzer die Fehlermeldung lesen kann, wird kurz gewartet
                }
            } while (!emailVerfuegbar);
            System.out.print("Bitte geben Sie Ihr Passwort ein:\n> ");
            var passwort = eingabe();
            var adresseValid = false;
            do {
                System.out.print("Bitte geben Sie Ihre Adresse ein (Straße Hausnummer Postleitzahl Stadt):\n> ");
                var adresse = eingabe();
                var adresseSplit = adresse.split(" ");
                adresseValid = adresseSplit.length == 4;
                if (!adresseValid) {
                    System.out.println("Fehlerhafte Eingabe! Bitte versuchen Sie es erneut.");
                }
            } while (!adresseValid);
            var registrierterNutzer = shopAPI.registrieren(new Kunde(
                    shopAPI.getNaechstePersId(),
                    email,
                    name,
                    new Adresse("Musterstrasse", "1", "2222", "Musterstadt"),
                    passwort
            ));
            shopAPI.setEingeloggterNutzer(registrierterNutzer);
            EreignisService.getInstance().setPerson(registrierterNutzer);
            System.out.println("Erfolgreich registriert!");
            return;
        } catch (PersonVorhandenException e) {
            System.out.println("Email bereits vergeben!");
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Eingabe: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        kundeRegistrieren();
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
                case "7" -> ereignisListAusgeben();
                case "x" -> logout();
                case "q" -> exit();
                default -> cuiMenue.falscheEingabeAusgabe();
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Eingabe: " + e.getMessage());
        }
        kundenMenueActions();
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
        System.out.print(shopAPI.rechnungErstellen());
        System.out.println("Rechnung erfolgreich erstellt!");
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
        } catch (ArtikelNichtGefundenException | BestandUeberschrittenException e) {
            System.out.println(e.getMessage());
            warenkorbArtikelHinzufuegen();
        } catch (NumberFormatException e) {
            System.out.println("Bitte geben Sie eine gültige Artikel-ID ein!");
            warenkorbArtikelHinzufuegen();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            System.out.println("Gesamtanzahl der Artikel im Warenkorb: " + warenkorb.getAnzahlArtikel());
            System.out.println("Gesamtsumme: " + shopAPI.getWarenkorbGesamtpreis() + "€ \n");
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

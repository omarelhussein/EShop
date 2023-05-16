package shop.ui.cui;

import shop.domain.ShopAPI;
import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
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
            case "3" -> run();
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
            case "2" -> cuiMenue.bereichAuswahlAusgabe();
            default -> cuiMenue.falscheEingabeAusgabe();
        }
        if (shopAPI.getEingeloggterNutzer() == null ||
                !(shopAPI.getEingeloggterNutzer() instanceof Mitarbeiter)) {
            loginRegistriereKunde();
        } else {
            mitarbeiterMenueActions();
        }
    }

    private void mitarbeiterMenueActions() throws IOException {
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
            case "2" -> artikelLoeschen();
            case "3" -> artikelSuchen();
            case "4" -> artikelListeAusgeben(shopAPI.getArtikelList());
            case "x" -> logout();
            case "q" -> exit();
            default -> cuiMenue.falscheEingabeAusgabe();
        }
        mitarbeiterMenueActions();
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
                default -> cuiMenue.falscheEingabeAusgabe();
            }
        } catch (IOException e) {
            System.out.println("Fehler bei der Eingabe!");
            lagerverwaltungAusgabe();
            return;
        }
        lagerverwaltungAusgabe();
    }

    private void artikelAnlegen() throws IOException {
        System.out.print("Bezeichnung:\n> ");
        String bezeichnung = eingabe();
        System.out.print("Preis:\n> ");
        double preis = Double.parseDouble(eingabe());
        System.out.print("Bestand:\n> ");
        int bestand = Integer.parseInt(eingabe());
        var artikel = new Artikel(shopAPI.getNaechsteArtikelId(), bezeichnung, preis, bestand);
        shopAPI.addArtikel(artikel);
        System.out.println("Artikel erfolgreich angelegt! Artikel-ID: " + artikel.getArtNr() + "\n");
    }

    private void artikelLoeschen() throws IOException {
        System.out.print("Artikel-ID:\n>");
        try {
            int artikelId = Integer.parseInt(eingabe());
            shopAPI.removeArtikel(artikelId);
        } catch (ArtikelNichtGefundenException e) {
            System.out.println("Artikel nicht gefunden, bitte erneut versuchen!");
            artikelLoeschen();
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

    private void logout() throws IOException {
        shopAPI.setEingeloggterNutzer(null);
        System.out.println("Erfolgreich ausgeloggt!");
        run();
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
                case "x" -> logout();
                case "q" -> exit();
                default -> cuiMenue.falscheEingabeAusgabe();
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Eingabe: " + e.getMessage());
        }
        kundenMenueActions();
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
            System.out.print("Bitte geben Sie die Anzahl ein:\n> ");
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
        var warenkorbArtikelList = shopAPI.getWarenkorb().getWarenkorbArtikelList();
        if (warenkorbArtikelList.isEmpty()) {
            System.out.println("Warenkorb ist leer! \n");
        } else {
            for (WarenkorbArtikel warenkorbArtikel : warenkorbArtikelList) {
                System.out.println(warenkorbArtikel.toString());
            }
            System.out.println("Gesamtanzahl der Artikel im Warenkorb: " + warenkorbArtikelList.size());
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

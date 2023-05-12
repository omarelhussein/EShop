package shop.ui.cui;

import shop.domain.ShopAPI;
import shop.domain.exceptions.personen.PersonVorhandenException;
import shop.entities.*;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class EShopCUI {

    private final ShopAPI shopAPI;
    private final EShopCUIMenue cuiMenue;
    private final EShopCUIMenue.Kunde kundenMenue;
    private final EShopCUIMenue.Mitarbeiter mitarbeiterMenue;
    private final Scanner in;
    private Person eingeloggterNutzer;

    public EShopCUI() {
        this.cuiMenue = new EShopCUIMenue();
        shopAPI = new ShopAPI();
        in = new Scanner(System.in);
        kundenMenue = new EShopCUIMenue.Kunde();
        mitarbeiterMenue = new EShopCUIMenue.Mitarbeiter();
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
        if (eingeloggterNutzer == null) {
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
        if (eingeloggterNutzer == null || !(eingeloggterNutzer instanceof Mitarbeiter)) {
            loginRegistriereKunde();
        } else {
            mitarbeiterMenueActions();
        }
    }

    private void mitarbeiterMenueActions() {
        mitarbeiterMenue.menueAusgabe();
        String eingabe = null;
        try {
            eingabe = eingabe();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*switch (eingabe) {
            case "1" -> artikelAnlegen();
            case "2" -> artikelLoeschen();
            case "3" -> artikelSuchen();
            case "4" -> artikelListeAusgeben();
            case "5" -> logout();
            default -> cuiMenue.falscheEingabeAusgabe();
        }*/ // TODO: Implement cases and sub-cases
        mitarbeiterMenueActions();
    }

    private boolean login(boolean istMitarbeiter) {
        try {
            System.out.println("Bitte geben Sie Ihren Nutzernamen ein:");
            var nutzername = eingabe();
            System.out.println("Bitte geben Sie Ihr Passwort ein:");
            var passwort = eingabe();
            eingeloggterNutzer = shopAPI.login(nutzername, passwort);
            if (eingeloggterNutzer == null) {
                System.out.println("Nutzername oder Passwort falsch!");
                return false;
            } else if (istMitarbeiter && eingeloggterNutzer instanceof Kunde) {
                System.out.println("Sie sind nicht berechtigt sich in diesem Bereich einzuloggen!");
                return false;
            } else {
                System.out.println("Erfolgreich eingeloggt!");
                return true;
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Eingabe: " + e.getMessage());
            return false;
        }
    }

    private void logout() throws IOException {
        eingeloggterNutzer = null;
        System.out.println("Erfolgreich ausgeloggt!");
        run();
    }

    private void kundeRegistrieren() {
        try {
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
            eingeloggterNutzer = shopAPI.registrieren(new Kunde(
                    shopAPI.getNaechsteId(),
                    email,
                    name,
                    new Adresse("Musterstrasse", "1", "2222", "Musterstadt"),
                    passwort
            ));
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
                case "1" -> artikelListAusgeben(shopAPI.getArtikelList());
                case "2" -> artikelSuchen();
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Eingabe: " + e.getMessage());
        }
        kundenMenueActions();
    }

    private void artikelSuchen() throws IOException {
        System.out.print("Bitte geben Sie den Namen oder die Artikelnummer des Artikels ein:\n> ");
        var artikelQuery = eingabe();
        var artikelList = shopAPI.getArtikelByQuery(artikelQuery);
        if (artikelList.isEmpty()) {
            System.out.println("Keine Artikel gefunden!");
        } else {
            artikelListAusgeben(artikelList);
        }
    }

    private void artikelListAusgeben(List<Artikel> artikelList) {
        for (Artikel artikel : artikelList) {
            System.out.println(artikel.toString());
        }
        System.out.println();
    }

    private void run() throws IOException {
        cuiMenue.bereichAuswahlAusgabe();
        String eingabe = eingabe();
        switch (eingabe) {
            case "1" -> loginRegistriereKunde();
            case "2" -> loginMitarbeiter();
            case "q" -> {
                System.out.println("Auf Wiedersehen!");
                System.exit(0);
            }
            default -> {
                cuiMenue.falscheEingabeAusgabe();
                run();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        EShopCUI cui = new EShopCUI();
        cui.shopAPI.addArtikel(new Artikel(127, "Hose", 19.99, 10));
        cui.shopAPI.addArtikel(new Artikel(128, "Hemd", 29.99, 10));
        cui.shopAPI.addArtikel(new Artikel(129, "Schuhe", 39.99, 10));
        cui.shopAPI.addArtikel(new Artikel(130, "Socken", 9.99, 10));
        cui.shopAPI.addArtikel(new Artikel(131, "Red Bull", 1.99, 10));
        cui.shopAPI.addArtikel(new Artikel(132, "Kaffee", 2.99, 10));
        cui.shopAPI.addArtikel(new Artikel(133, "Tee", 3.99, 10));
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

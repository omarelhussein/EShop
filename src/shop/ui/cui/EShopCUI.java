package shop.ui.cui;

import shop.domain.ShopAPI;
import shop.entities.Person;

import java.io.IOException;
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
            case "1" -> {
                if (login()) {
                    kundenMenue.menueAusgabe();
                } else {
                    loginRegistriereKunde();
                }
            }
            case "2" -> {
                if (registrieren()) {
                    kundenMenue.menueAusgabe();
                } else {
                    loginRegistriereKunde();
                }
            }
            case "3" -> cuiMenue.bereichAuswahlAusgabe();
            default -> {
                cuiMenue.falscheEingabeAusgabe();
                loginRegistriereKunde();
            }
        }
    }

    private void loginMitarbeiter() throws IOException {
        mitarbeiterMenue.loginAusgabe();
        String eingabe = eingabe();
        switch (eingabe) {
            case "1" -> {
                if (login()) {
                    mitarbeiterMenue.menueAusgabe();
                } else {
                    loginMitarbeiter();
                }
            }
            case "2" -> cuiMenue.bereichAuswahlAusgabe();
            default -> {
                cuiMenue.falscheEingabeAusgabe();
                loginMitarbeiter();
            }
        }
    }

    private boolean login() {
        try {
            System.out.println("Bitte geben Sie Ihren Nutzernamen ein:");
            var nutzername = eingabe();
            System.out.println("Bitte geben Sie Ihr Passwort ein:");
            var passwort = eingabe();
            eingeloggterNutzer = shopAPI.login(nutzername, passwort);
            if (eingeloggterNutzer == null) {
                System.out.println("Nutzername oder Passwort falsch!");
                return false;
            } else {
                System.out.println("Erfolgreich eingeloggt!");
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void logout() {
        eingeloggterNutzer = null;
        System.out.println("Erfolgreich ausgeloggt!");
        System.exit(0);
    }

    private boolean registrieren() {
        try {
            System.out.println("Bitte geben Sie Ihren Namen ein:");
            var name = eingabe();
            System.out.println("Bitte geben Sie Ihre Adresse ein:");
            var adresse = eingabe();
            System.out.println("Bitte geben Sie Ihren Nutzernamen ein:");
            var nutzername = eingabe();
            System.out.println("Bitte geben Sie Ihr Passwort ein:");
            var passwort = eingabe();
            System.out.println("Bitte geben Sie Ihre E-Mail-Adresse ein:");
            var email = eingabe();
            eingeloggterNutzer = shopAPI.registrieren(name, adresse, nutzername, passwort, email);
            if (eingeloggterNutzer == null) {
                System.out.println("Nutzername bereits vergeben!");
                return false;
            } else {
                System.out.println("Erfolgreich registriert!");
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void run() throws IOException {
        cuiMenue.bereichAuswahlAusgabe();
        String eingabe = eingabe();
        switch (eingabe) {
            case "1" -> loginRegistriereKunde();
            case "2" -> loginMitarbeiter();
            case "3" -> System.exit(0);
            default -> {
                cuiMenue.falscheEingabeAusgabe();
                run();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        EShopCUI cui = new EShopCUI();
        cui.run();
    }

}

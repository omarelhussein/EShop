package shop.ui.cui;

import shop.domain.ShopAPI;
import shop.entities.Kunde;
import shop.entities.Mitarbeiter;
import shop.entities.Person;
import shop.ui.cui.enums.AuswahlTyp;

import java.io.IOException;
import java.util.Scanner;

public class EShopCUIMenueActions {

    private Person eingeloggterNutzer;
    private final ShopAPI shopAPI;

    private final Scanner in;

    public EShopCUIMenueActions() {
        in = new Scanner(System.in);
        this.shopAPI = new ShopAPI();
    }

    /**
     * Liest die Eingaben des Nutzers in der Console
     */
    private String eingabe() throws IOException {
        return in.nextLine();
    }

    public boolean login() {
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

    public void logout() {
        eingeloggterNutzer = null;
        System.out.println("Erfolgreich ausgeloggt!");
        System.exit(0);
    }

    public boolean registrieren() {
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

    public String getKeyCombined(String key) {
        return getAuswahlTypByNutzerLoginStatus().getKeyPrefix() + key;
    }

    public AuswahlTyp getAuswahlTypByNutzerLoginStatus() {
        if (eingeloggterNutzer instanceof Kunde) {
            return AuswahlTyp.KUNDE;
        } else if (eingeloggterNutzer instanceof Mitarbeiter) {
            return AuswahlTyp.MITARBEITER;
        } else {
            return AuswahlTyp.START;
        }
    }

}

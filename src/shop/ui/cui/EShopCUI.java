package shop.ui.cui;

import shop.domain.ShopAPI;
import shop.entities.Artikel;

import java.util.List;
import java.util.Scanner;
import java.io.IOException;
public class EShopCUI {

    private ShopAPI shopAPI;
    private Scanner in;

    public EShopCUI() {
        shopAPI = new ShopAPI();
        in = new Scanner(System.in);
    }
    // eingaben
    /**
     * Liest die Eingaben des Nutzers in der Console
     */
    private String eingabe() throws IOException {
        return in.nextLine();
    }

    // ausgaben
    /**
     * gibt ein einfaches Menü aus
     * !MUSS NOCH ÜBERARBEITET WERDEN. PLAN IST ES ERST ZWISCHEN ARTIKEL, KUNDEN UND MITARBEITER BEFEHLEN
     * WECHSELN ZU KÖNNEN. INDEM MAN ZUERST 1. 2. 3. WÄHLT!
     */
    private void gibMenueEinfach() {
        System.out.print("Befehle: \n  Alle Artikel ausgeben:  'a'");
        System.out.print("         \n  Alle Kunden ausgeben:  'k'");
        System.out.print("         \n  Alle Mitarbeiter ausgeben:  'm'");
        System.out.print("         \n  Artikel löschen: 'l'");
        System.out.print("         \n  Kunde entfernen: 'o'");
        System.out.print("         \n  Mitarbeiter entfernen: 'p'");
        System.out.print("         \n  Artikel einfügen: 'e'");
        System.out.print("         \n  Kunden einfügen: 'y'");
        System.out.print("         \n  Mitarbeiter einfügen: 'r'");
        System.out.print("         \n  Artikel suchen:  'w'");
        System.out.print("         \n  Kunden suchen:  's'");
        System.out.print("         \n  Mitarbeiter suchen:  'b'");
        System.out.print("         \n  ---------------------");
        System.out.println("         \n  Beenden:        'q'");
        System.out.print("> ");
        System.out.flush();
    }
    // verarbeitung
    /**
     * Verarbeitet die Eingabe des Nutzers
     * !UNVOLLSTÄNDIG UND MUSS NACH ÜBERARBEITUNG VON "gibMenueEinfach()" ÜBERARBEITET WERDEN!
     */

    private void verarbeitungEingabe(String input) {
        List<Artikel> ListeAnArtikeln;
        switch (input) {
            case "a":
                ListeAnArtikeln = shopAPI.alleArtikelAusgeben();
                break;
            case "k":

                break;
            case "m":

        }
    }

    /**
     * Startet das Menue für die Befehle und gibt daraufhin den
     * Input des Nutzers in die methode "verarbeitungEingabe()" weiter.
     *
     * Wird so lange ausgeführt bis der Nutzer "q" eingibt um die while Schleife zu unterbrechen.
     * !MUSS NOCH ÜBERARBEITET WERDEN WENN DIE MENUES ÜBERARBEITET WERDEN!
     */
    public void run() {
        // Variable für Eingaben von der Konsole
        String input = "";

        // Hauptschleife der Benutzungsschnittstelle
        do {
            gibMenueEinfach();
            try {
                input = eingabe();
                verarbeitungEingabe(input);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } while (!input.equals("q"));
    }

    public static void main(String[] args) {
        EShopCUI cui;
        try {
            cui = new EShopCUI("EShop");
            cui.run();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

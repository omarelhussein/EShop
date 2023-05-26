package shop.ui.cui;

public class EShopCUIMenue {


    public void bereichAuswahlAusgabe() {
        System.out.println("\nBitte wählen Sie einen Bereich aus:");
        System.out.println("1) Kunden");
        System.out.println("2) Mitarbeiter");
        System.out.println("q) Beenden");
        System.out.print("> ");
    }

    static class Mitarbeiter {
        public void menueAusgabe() {
            System.out.println("Bitte wählen Sie eine Option aus:");
            System.out.println("1) Lagerverwaltung");
            //System.out.println("2) Kundenverwaltung");
            System.out.println("2) Personalverwaltung");
            System.out.println("3) Ereignisse");
            //System.out.println("5) Konto");
            System.out.println("x) Abmelden");
            System.out.println("q) Beenden");
            System.out.print("> ");
        }

        public void lagerverwaltungAusgabe() {
            System.out.println("Bitte wählen Sie eine Option aus:");
            System.out.println("1) Artikel anzeigen");
            System.out.println("2) Artikel suchen");
            System.out.println("3) Artikel hinzufügen");
            System.out.println("4) Artikel löschen");
            System.out.println("5) Artikel bearbeiten");
            System.out.println("6) Artikelbestand bearbeiten");
            System.out.println("7) Artikelbestandshistorie anzeigen");
            System.out.println("b) Zurück");
            System.out.print("> ");
        }

        public void personalverwaltungAusgabe() {
            System.out.println("Bitte wählen Sie eine Option aus:");
            System.out.println("1) Mitarbeiter anzeigen");
            System.out.println("2) Mitarbeiter suchen");
            System.out.println("3) Mitarbeiter hinzufügen");
            System.out.println("4) Mitarbeiter löschen");
            System.out.println("b) Zurück");
            System.out.print("> ");
        }

        public void loginAusgabe() {
            System.out.println("Bitte wählen Sie eine Option aus:");
            System.out.println("1) Login");
            System.out.println("b) Zurück");
            System.out.print("> ");
        }

    }

    static class Kunde {
        public void menueAusgabe() {
            System.out.println("Bitte wählen Sie eine Option aus:");
            System.out.println("1) Artikel anzeigen");
            System.out.println("2) Artikel suchen");
            System.out.println("3) Warenkorb anzeigen");
            System.out.println("4) Zum Warenkorb hinzufügen");
            System.out.println("5) Warenkorb bearbeiten");
            System.out.println("6) Bestellen");
            System.out.println("7) Ereignisse");
            //System.out.println("8) Bestellungen anzeigen"); // TODO: Bestellungen anzeigen
            //System.out.println("7) Konto anzeigen");
            //System.out.println("8) Konto bearbeiten");
            System.out.println("x) Abmelden");
            System.out.println("q) Beenden");
            System.out.print("> ");
        }

        public void loginRegistrierenAusgabe() {
            System.out.println("\nBitte wählen Sie eine Option aus:");
            System.out.println("1) Login");
            System.out.println("2) Registrieren");
            System.out.println("b) Zurück");
            System.out.print("> ");
        }

    }

    public void falscheEingabeAusgabe() {
        System.out.println("Falsche Eingabe, bitte versuchen Sie es erneut.\n");
    }

}


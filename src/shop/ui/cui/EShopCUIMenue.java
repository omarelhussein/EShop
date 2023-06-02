package shop.ui.cui;

import shop.utils.StringUtils;

/**
 * Die EShopCUIMenue Klasse dient dazu, Menüs im CUI für den E-Shop darzustellen.
 * Sie enthält mehrere Methoden zum Ausgeben verschiedener Menüoptionen für unterschiedliche Anwendungsbereiche und Benutzertypen.
 * <p>
 * Darüber hinaus beinhaltet sie zwei statische innere Klassen, Mitarbeiter und Kunde, welche spezifische Methoden
 * für die jeweiligen Benutzerrollen bereitstellen. Diese Methoden repräsentieren die verschiedenen Aktionen, die
 * Mitarbeiter und Kunden ausführen können, wie z.B. die Anzeige von Artikeln, die Verwaltung von Lagerbeständen oder
 * die Anzeige des Warenkorbs.
 */
public class EShopCUIMenue {

    public void bereichAuswahlAusgabe() {
        System.out.println(StringUtils.lineSeparator());
        System.out.println("Bitte wählen Sie einen Bereich aus:\n");
        System.out.println("[ 1 ]\tKunden");
        System.out.println("[ 2 ]\tMitarbeiter");
        System.out.println("[ q ]\tBeenden");
        System.out.println(StringUtils.lineSeparator("-"));
        System.out.print("> ");
    }

    static class Mitarbeiter {
        public void menueAusgabe() {
            System.out.println(StringUtils.lineSeparator());
            System.out.println("Bitte wählen Sie eine Option aus:\n");
            System.out.println("[ 1 ]\tLagerverwaltung");
            System.out.println("[ 2 ]\tPersonalverwaltung");
            System.out.println("[ 3 ]\tEreignisse");
            System.out.println("[ x ]\tAbmelden");
            System.out.println("[ q ]\tBeenden");
            System.out.println(StringUtils.lineSeparator("-"));
            System.out.print("> ");
            //System.out.println("2) Kundenverwaltung");
            //System.out.println("5) Konto");
        }

        public void lagerverwaltungAusgabe() {
            System.out.println(StringUtils.lineSeparator());
            System.out.println("Bitte wählen Sie eine Option aus:\n");
            System.out.println("[ 1 ]\tArtikel anzeigen");
            System.out.println("[ 2 ]\tArtikel suchen");
            System.out.println("[ 3 ]\tArtikel hinzufügen");
            System.out.println("[ 4 ]\tArtikel löschen");
            System.out.println("[ 5 ]\tArtikel bearbeiten");
            System.out.println("[ 6 ]\tArtikelbestand bearbeiten");
            System.out.println("[ 7 ]\tArtikelbestandshistorie anzeigen");
            System.out.println("[ b ]\tZurück");
            System.out.println(StringUtils.lineSeparator("-"));
            System.out.print("> ");
        }

        public void personalverwaltungAusgabe() {
            System.out.println(StringUtils.lineSeparator());
            System.out.println("Bitte wählen Sie eine Option aus:\n");
            System.out.println("[ 1 ]\tMitarbeiter anzeigen");
            System.out.println("[ 2 ]\tMitarbeiter suchen");
            System.out.println("[ 3 ]\tMitarbeiter hinzufügen");
            System.out.println("[ 4 ]\tMitarbeiter löschen");
            System.out.println("[ 5 ]\tPersonaktivitäten anschauen");
            System.out.println("[ b ]\tZurück");
            System.out.println(StringUtils.lineSeparator("-"));
            System.out.print("> ");
        }

        public void loginAusgabe() {
            System.out.println(StringUtils.lineSeparator());
            System.out.println("Bitte wählen Sie eine Option aus:\n");
            System.out.println("[ 1 ]\tLogin");
            System.out.println("[ b ]\tZurück");
            System.out.println(StringUtils.lineSeparator("-"));
            System.out.print("> ");
        }

    }

    static class Kunde {
        public void menueAusgabe() {
            System.out.println(StringUtils.lineSeparator());
            System.out.println("Bitte wählen Sie eine Option aus:\n");
            System.out.println("[ 1 ]\tArtikel anzeigen");
            System.out.println("[ 2 ]\tArtikel suchen");
            System.out.println("[ 3 ]\tWarenkorb anzeigen");
            System.out.println("[ 4 ]\tZum Warenkorb hinzufügen");
            System.out.println("[ 5 ]\tWarenkorb bearbeiten");
            System.out.println("[ 6 ]\tBestellen");
            System.out.println("[ r ]\tRechnungen Anzeigen");
            System.out.println("[ e ]\tEreignisse");
            //System.out.println("8) Bestellungen anzeigen"); // TODO: Bestellungen anzeigen
            //System.out.println("7) Konto anzeigen");
            //System.out.println("8) Konto bearbeiten");
            System.out.println("[ x ]\tAbmelden");
            System.out.println("[ q ]\tBeenden");
            System.out.println(StringUtils.lineSeparator("-"));
            System.out.print("> ");
        }

        public void loginRegistrierenAusgabe() {
            System.out.println(StringUtils.lineSeparator());
            System.out.println("Bitte wählen Sie eine Option aus:\n");
            System.out.println("[ 1 ]\tLogin");
            System.out.println("[ 2 ]\tRegistrieren");
            System.out.println("[ b ]\tZurück");
            System.out.println(StringUtils.lineSeparator("-"));
            System.out.print("> ");
        }

    }

    public void falscheEingabeAusgabe() {
        System.out.println("Falsche Eingabe, bitte versuchen Sie es erneut.\n");
    }

}


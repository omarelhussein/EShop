package shop.ui.cui;

import shop.ui.cui.enums.AuswahlTyp;

import java.util.HashMap;
import java.util.Map;

public class EShopCUIMenue {

    private final EShopCUIMenueActions menueActions;

    public EShopCUIMenue() {
        this.menueActions = new EShopCUIMenueActions();
    }

    private void bereichAuswahlAusgabe() {
        System.out.println("Bitte wählen Sie einen Bereich aus:");
        System.out.println("1) Kunden");
        System.out.println("2) Mitarbeiter");
        System.out.println("q) Beenden");
        System.out.print("> ");
    }

    static class Mitarbeiter {
        private void menueAusgabe() {
            System.out.println("Bitte wählen Sie eine Option aus:");
            System.out.println("1) Lagerverwaltung");
            System.out.println("2) Kundenverwaltung");
            System.out.println("3) Personalverwaltung");
            System.out.println("4) Ereignisse");
            System.out.println("5) Konto");
            System.out.println("6) Zurück");
            System.out.print("> ");
        }

        private void lagerverwaltungAusgabe() {
            System.out.println("Bitte wählen Sie eine Option aus:");
            System.out.println("11) Artikel anzeigen");
            System.out.println("21) Artikel suchen");
            System.out.println("31) Artikel hinzufügen");
            System.out.println("41) Artikel löschen");
            System.out.println("51) Artikel bearbeiten");
            System.out.println("61) Artikel Bestand bearbeiten");
        }

        private void loginAusgabe() {
            System.out.println("Bitte wählen Sie eine Option aus:");
            System.out.println("11) Login");
            System.out.println("22) Zurück");
            System.out.print("> ");
        }

    }

    static class Kunde {
        private void menueAusgabe() {
            System.out.println("Bitte wählen Sie eine Option aus:");
            System.out.println("1) Artikel");
            System.out.println("2) Warenkorb");
            System.out.println("3) Konto");
            System.out.println("4) Zurück");
            System.out.print("> ");
        }

        private void artikelAusgabe() {
            System.out.println("Bitte wählen Sie eine Option aus:");
            System.out.println("11) Artikel anzeigen");
            System.out.println("21) Artikel suchen");
        }

        private void loginRegistrierenAusgabe() {
            System.out.println("Bitte wählen Sie eine Option aus:");
            System.out.println("11) Login");
            System.out.println("22) Registrieren");
            System.out.println("33) Zurück");
            System.out.print("> ");
        }

    }

    public void getAuswahlByKey(String key) {
        var auswahlMap = getAuswahlMap();
        var keyKombiniert = menueActions.getKeyCombined(key);
        var menue = auswahlMap.get(keyKombiniert);
        menue.run();
    }

    private Map<String, Runnable> getAuswahlMap() {
        var kundenMenue = new Kunde();
        var mitarbeiterMenue = new Mitarbeiter();
        Map<String, Runnable> map = new HashMap<>();
        map.put("s0", this::bereichAuswahlAusgabe);
        map.put("s1", kundenMenue::loginRegistrierenAusgabe);
        map.put("s11", () -> {
            if (menueActions.login()) {
                kundenMenue.menueAusgabe();
            } else {
                kundenMenue.loginRegistrierenAusgabe();
            }
        });
        map.put("s22", () -> {
            if (menueActions.registrieren()) {
                kundenMenue.menueAusgabe();
            } else {
                kundenMenue.loginRegistrierenAusgabe();
            }
        });
        map.put("s2", mitarbeiterMenue::loginAusgabe);
        map.put("k0", kundenMenue::menueAusgabe);
        map.put("k1", kundenMenue::artikelAusgabe);
        map.put("m0", mitarbeiterMenue::menueAusgabe);
        map.put("m1", mitarbeiterMenue::lagerverwaltungAusgabe);
        map.put("sq", menueActions::logout);
        map.put("mq", menueActions::logout);
        map.put("kq", menueActions::logout);
        return map;
    }

}


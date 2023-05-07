package shop.ui.cui;

import java.io.IOException;
import java.util.Scanner;

public class EShopCUI{

    private final EShopCUIMenue cuiMenue;
    private final Scanner in;

    public EShopCUI() {
        this.cuiMenue = new EShopCUIMenue();
        in = new Scanner(System.in);
    }

    /**
     * Liest die Eingaben des Nutzers in der Console
     */
    private String eingabe() throws IOException {
        return in.nextLine();
    }

    public void run() {
        try {
            var auswahl = eingabe();
            cuiMenue.getAuswahlByKey(auswahl);
            if (!auswahl.equals("q"))
                run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        EShopCUI cui;
        cui = new EShopCUI();
        cui.cuiMenue.getAuswahlByKey("0");
        cui.run();
    }

}

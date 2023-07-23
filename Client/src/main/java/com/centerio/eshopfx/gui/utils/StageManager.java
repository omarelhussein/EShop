package com.centerio.eshopfx.gui.utils;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Die Klasse StageManager verwaltet das Hauptfenster (Stage) von JavaFX und bietet Methoden zum Wechseln der Szenen.
 */
public class StageManager {

    private static StageManager instance;
    private Stage stage;

    private StageManager() {
    }

    /**
     * Gibt die Instanz von StageManager zurück.
     *
     * @return Die Instanz von StageManager.
     */
    public static StageManager getInstance() {
        if (instance == null) {
            instance = new StageManager();
        }
        return instance;
    }

    /**
     * Gibt die Hauptbühne (Stage) der Anwendung zurück.
     *
     * @return Die Hauptbühne.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Setzt die Hauptbühne (Stage) für die Anwendung.
     *
     * @param stage Die Hauptbühne.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Wechselt die Szene der Hauptbühne zur angegebenen Szene. Die Szene wird aus einer FXML-Datei geladen,
     * wie in der Methode start der Klasse com.centerio.eshopfx.EShopApplication. Ein neuer FXMLLoader wird erstellt, der die FXML-Datei lädt.
     * Schließlich wird die Wurzelkomponente der Szene auf die Wurzelkomponente der neuen Szene gesetzt (Die Wurzel
     * in dem Fall, um die Größe und Event Listener der Stage bei einem Szenenwechsel nicht zu verändern).
     *
     * @param sceneName Der Name der FXML-Datei für die neue Szene.
     * @param root      Ob die Wurzelkomponente der neuen Szene auf die Wurzelkomponente der aktuellen Szene gesetzt werden soll.@
     */
    public void switchScene(String sceneName, boolean root) {
        try {
            URL path = getClass().getResource("/com/centerio/eshopfx/" + sceneName);
            FXMLLoader fxmlLoader = new FXMLLoader(path);
            if (root) {
                stage.getScene().setRoot(fxmlLoader.load());
            } else {
                stage.setScene(fxmlLoader.load());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void switchScene(String sceneName) {
        switchScene(sceneName, true);
    }

}

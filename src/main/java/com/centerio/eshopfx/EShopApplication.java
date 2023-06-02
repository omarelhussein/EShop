package com.centerio.eshopfx;

import com.centerio.eshopfx.shop.ui.gui.utils.SceneRoutes;
import com.centerio.eshopfx.shop.ui.gui.utils.StageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class EShopApplication extends Application {


    private final StageManager stageManager = StageManager.getInstance();

    @Override
    public void start(Stage stage) {
        // Stage ist die Hauptkomponente einer JavaFX-Anwendung. In Java Swing ist das die JFrame-Klasse.
        stageManager.setStage(stage);
        try {
            // UI-Elemente werden in FXML-Dateien definiert. Diese werden dann in JavaFX geladen und angezeigt.
            URL url = getClass().getResource(SceneRoutes.LOGIN_VIEW);
            // FXMLLoader ist eine Klasse, die FXML-Dateien lädt und in JavaFX-Objekte umwandelt.
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            // Scene ist die Wurzelkomponente einer JavaFX-Anwendung. In Java Swing ist das die JPanel-Klasse.
            // Ein JFrame kann mehrere JPanels enthalten, eine Stage kann mehrere Scenes enthalten.
            Scene scene = new Scene(fxmlLoader.load());
            // CSS-Dateien können in JavaFX geladen werden, um das Aussehen der UI-Elemente zu verändern.
            // Das ist eine Möglichkeit, um das Aussehen der Anwendung zu verändern. Die kann man global für alle UI-Elemente machen, oder nur für einzelne.
            URL css = getClass().getResource("css/view.css");
            if (css != null) {
                // Die CSS-Datei wird der Scene hinzugefügt.
                scene.getStylesheets().add(css.toExternalForm());
            }
            // Das Schließen der Anwendung wird hier behandelt. Ähnlich wie in Java Swing mit setDefaultCloseOperation.
            stage.setOnCloseRequest(e -> {
                e.consume();
                System.out.println("Closing");
                stage.close();
            });
            // Die Stage kann nicht vergrößert oder verkleinert werden.
            stage.setResizable(false);
            // Ein Titel für die Stage wird gesetzt.
            stage.setTitle("EShopFX");
            // Die Scene wird der Stage hinzugefügt.
            stage.setScene(scene);
            // Die Stage wird angezeigt.
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        // Die Anwendung wird gestartet. Diese Methode wird von der Klasse Application geerbt.
        // Die Klasse Application ist die Hauptklasse einer JavaFX-Anwendung. In Java Swing ist das die Klasse JFrame.
        launch();
    }

}
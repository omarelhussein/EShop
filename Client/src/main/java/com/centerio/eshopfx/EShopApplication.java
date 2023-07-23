package com.centerio.eshopfx;

import com.centerio.eshopfx.gui.utils.SceneRoutes;
import com.centerio.eshopfx.gui.utils.StageManager;
import domain.ShopAPI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.rmi.RemoteException;

public class EShopApplication extends Application {


    private final StageManager stageManager = StageManager.getInstance();

    @Override
    public void start(Stage stage) {
        setAppIcon();
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
            URL css = getClass().getResource("css/global.css");
            if (css != null) {
                // Die CSS-Datei wird der Scene hinzugefügt.
                scene.getStylesheets().add(css.toExternalForm());
            }
            // Das Schließen der Anwendung wird hier behandelt. Ähnlich wie in Java Swing mit setDefaultCloseOperation.
            stage.setOnCloseRequest(e -> {
                e.consume();
                System.out.println("Closing...");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Programm beenden");
                alert.setHeaderText("Sind Sie sicher, dass Sie das Programm beenden möchten?");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == javafx.scene.control.ButtonType.OK) {
                        stage.close();
                    }
                });
            });
            // Die Stage kann nicht vergrößert oder verkleinert werden.
            stage.setResizable(true);
            stage.setMinHeight(600);
            stage.setMinWidth(800);
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

    public void setAppIcon() {
        var iconUrl = getClass().getResource("static/icon.png");
        if (Taskbar.isTaskbarSupported()) {
            var taskbar = Taskbar.getTaskbar();

            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                var dockIcon = defaultToolkit.getImage(iconUrl);
                taskbar.setIconImage(dockIcon);
            }
        }
    }

    public static void main(String[] args) {
        try {
            ShopAPI shopAPI = ShopAPIClient.getShopAPI();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    shopAPI.speichern();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        // Die Anwendung wird gestartet. Diese Methode wird von der Klasse Application geerbt.
        // Die Klasse Application ist die Hauptklasse einer JavaFX-Anwendung. In Java Swing ist das die Klasse JFrame.
        launch();
    }

}
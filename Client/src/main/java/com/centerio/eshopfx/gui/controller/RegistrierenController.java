package com.centerio.eshopfx.gui.controller;

import com.centerio.eshopfx.ShopAPIClient;
import com.centerio.eshopfx.gui.utils.LoginUtils;
import com.centerio.eshopfx.gui.utils.SceneRoutes;
import com.centerio.eshopfx.gui.utils.StageManager;
import domain.ShopAPI;
import entities.Adresse;
import entities.Kunde;
import exceptions.personen.PasswortNameException;
import exceptions.personen.PersonVorhandenException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.rmi.RemoteException;

public class RegistrierenController {

    @FXML
    private Label infoLabel;

    @FXML
    private TextField usernameField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField strassenField;
    @FXML
    private TextField hausnummerField;
    @FXML
    private TextField postleitzahlField;
    @FXML
    private TextField herkunftsortField;

    private final ShopAPI shopAPI = ShopAPIClient.getShopAPI();
    private final LoginUtils loginUtils;

    public RegistrierenController() throws RemoteException {
        loginUtils = new LoginUtils();
    }

    /**
     * Methode initialize wird automatisch aufgerufen, wenn die View geladen wird.
     */
    public void initialize() {
        // Listener für den Benutzername-Textfeld
        // Listener wird aufgerufen, wenn das Textfeld den Fokus verliert (weggeklickt wird)
        usernameField.focusedProperty().addListener(observable -> {
            // wenn das Textfeld leer ist, dann wird die Methode beendet
            if (usernameField.getText().isEmpty()) return;
            // Überprüfung, ob der Benutzername bereits vergeben ist
            // wenn ja, dann wird die InfoLabel rot gefärbt und eine Meldung wird angezeigt
            try {
                if (!shopAPI.istNutzernameVerfuegbar(usernameField.getText())) {
                    infoLabel.setText("Benutzername ist bereits vergeben!");
                    infoLabel.setStyle("-fx-text-fill: red;");
                } else {
                    infoLabel.setText("Benutzername ist verfügbar!");
                    infoLabel.setStyle("-fx-text-fill: green;");
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }


    /**
     * registriert den Nutzer mit den Daten in den dafür vorgesehenen Feldern, überprüft diese Daten
     * und loggt den Nutzer direkt ein
     * @param actionEvent
     */
    public void registrieren(ActionEvent actionEvent) {
        resetRegistrierenStyles();
        if (checkField(usernameField)) {
            infoLabel.setText("Bitte geben Sie Benutzername ein!");
            return;
        }
        if (checkField(passwordField)) {
            infoLabel.setText("Bitte geben Sie Passwort ein!");
            return;
        }
        if (checkField(strassenField)) {
            infoLabel.setText("Bitte geben Sie Straßenname ein!");
            return;
        }
        if (checkField(hausnummerField)) {
            infoLabel.setText("Bitte geben Sie Hausnummer ein!");
            return;
        }
        if (checkField(postleitzahlField)) {
            infoLabel.setText("Bitte geben Sie Postleitzahl ein!");
            return;
        }
        if (checkField(herkunftsortField)) {
            infoLabel.setText("Bitte geben Sie Herkunftsort ein!");
            return;
        }

        try {
            shopAPI.registrieren(new Kunde(
                    shopAPI.getNaechstePersId(),
                    usernameField.getText(),
                    nameField.getText(),
                    new Adresse(strassenField.getText(), hausnummerField.getText(), postleitzahlField.getText(), herkunftsortField.getText()),
                    passwordField.getText()));
            loginUtils.login(usernameField.getText(), passwordField.getText());
        } catch (PersonVorhandenException e) {
            infoLabel.setText("Benutzername ist schon vorhanden!");
            usernameField.setStyle("-fx-border-color: red;");
            return;
        } catch (IOException | PasswortNameException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
        StageManager.getInstance().switchScene(SceneRoutes.KUNDE_VIEW);
    }

    /**
     * checkt ob ein Textfeld beim registrieren leer gelassen wurde und markiert dies rot
     * @param field
     * @return
     */
    private boolean checkField(TextField field) {
        if (field.getText().isEmpty()) {
            field.setStyle("-fx-border-color: red;");
            return true;
        }
        return false;
    }

    /**
     * entfernt die rote Markierung
     */
    public void resetRegistrierenStyles() {
        usernameField.setStyle("");
        passwordField.getStyleClass().remove("error");
        strassenField.setStyle("");
        hausnummerField.setStyle("");
        postleitzahlField.setStyle("");
        herkunftsortField.setStyle("");
        infoLabel.setStyle("");
    }

    /**
     * bricht den Registrierungsvorgang ab und bringt den Nutzer zum Loginmenu zurück
     */
    public void abbrechen() {
        StageManager.getInstance().switchScene(SceneRoutes.LOGIN_VIEW);
    }
}

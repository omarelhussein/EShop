package com.centerio.eshopfx.shop.ui.gui.controller;

import com.centerio.eshopfx.shop.domain.RemoteInterface;
import com.centerio.eshopfx.shop.domain.RemoteSingletonService;
import com.centerio.eshopfx.shop.domain.ShopAPI;
import com.centerio.eshopfx.shop.domain.exceptions.personen.PersonVorhandenException;
import com.centerio.eshopfx.shop.entities.Adresse;
import com.centerio.eshopfx.shop.entities.Kunde;
import com.centerio.eshopfx.shop.entities.UserContext;
import com.centerio.eshopfx.shop.ui.gui.utils.SceneRoutes;
import com.centerio.eshopfx.shop.ui.gui.utils.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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

    private Registry registry = LocateRegistry.getRegistry("localhost", 1099);
    private RemoteSingletonService singletonService = (RemoteSingletonService) registry.lookup("RemoteObject");
    private RemoteInterface shopAPI = singletonService.getSingletonInstance();

    public RegistrierenController() throws RemoteException, NotBoundException {
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
            var person = shopAPI.registrieren(new Kunde(
                    shopAPI.getNaechstePersId(),
                    usernameField.getText(),
                    nameField.getText(),
                    new Adresse(strassenField.getText(), hausnummerField.getText(), postleitzahlField.getText(), herkunftsortField.getText()),
                    passwordField.getText()));
            UserContext.setUser(person);
        } catch (PersonVorhandenException e) {
            infoLabel.setText("Benutzername ist schon vorhanden!");
            usernameField.setStyle("-fx-border-color: red;");
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StageManager.getInstance().switchScene(SceneRoutes.KUNDE_VIEW);
    }

    private boolean checkField(TextField field) {
        if (field.getText().isEmpty()) {
            field.setStyle("-fx-border-color: red;");
            return true;
        }
        return false;
    }

    public void resetRegistrierenStyles() {
        usernameField.setStyle("");
        passwordField.getStyleClass().remove("error");
        strassenField.setStyle("");
        hausnummerField.setStyle("");
        postleitzahlField.setStyle("");
        herkunftsortField.setStyle("");
        infoLabel.setStyle("");
    }

    public void abbrechen() {
        StageManager.getInstance().switchScene(SceneRoutes.LOGIN_VIEW);
    }
}

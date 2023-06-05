package com.centerio.eshopfx.shop.ui.gui.controller;

import com.centerio.eshopfx.shop.domain.ShopAPI;
import com.centerio.eshopfx.shop.domain.exceptions.personen.PersonVorhandenException;
import com.centerio.eshopfx.shop.entities.Adresse;
import com.centerio.eshopfx.shop.entities.Kunde;
import com.centerio.eshopfx.shop.entities.UserContext;
import com.centerio.eshopfx.shop.ui.gui.utils.SceneRoutes;
import com.centerio.eshopfx.shop.ui.gui.utils.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.lang.reflect.Field;

public class RegistrierenController {

    @FXML
    private Label welcomeLabel;

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

    private final ShopAPI shopAPI = ShopAPI.getInstance();


    public void registrieren(ActionEvent actionEvent) throws PersonVorhandenException {
        resetRegistrierenStyles();
        if (FieldCheck(usernameField)) {
            welcomeLabel.setText("Bitte geben Sie Benutzername ein!");
            return;
        }
        if (FieldCheck(passwordField)) {
            welcomeLabel.setText("Bitte geben Sie Passwort ein!");
            return;
        }
        if (FieldCheck(strassenField)) {
            welcomeLabel.setText("Bitte geben Sie Straßenname ein!");
            return;
        }
        if (FieldCheck(hausnummerField)) {
            welcomeLabel.setText("Bitte geben Sie Hausnummer ein!");
            return;
        }
        if (FieldCheck(postleitzahlField)) {
            welcomeLabel.setText("Bitte geben Sie Postleitzahl ein!");
            return;
        }
        if (FieldCheck(herkunftsortField)) {
            welcomeLabel.setText("Bitte geben Sie Herkunftsort ein!");
            return;
        }

        UserContext.setUser(shopAPI.registrieren(new Kunde(
                shopAPI.getNaechstePersId(),
                usernameField.getText(),
                nameField.getText(),
                new Adresse(strassenField.getText(), hausnummerField.getText(), postleitzahlField.getText(), herkunftsortField.getText()),
                passwordField.getText())));

        StageManager.getInstance().switchScene(SceneRoutes.HOME_VIEW);
    }

    private boolean FieldCheck(TextField field) {
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

    }

    public void abbrechen() {
        StageManager.getInstance().switchScene(SceneRoutes.LOGIN_VIEW);
    }
}

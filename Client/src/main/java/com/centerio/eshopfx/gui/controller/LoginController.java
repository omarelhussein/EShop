package com.centerio.eshopfx.gui.controller;

import com.centerio.eshopfx.ShopAPIClient;
import com.centerio.eshopfx.gui.utils.LoginUtils;
import com.centerio.eshopfx.gui.utils.SceneRoutes;
import com.centerio.eshopfx.gui.utils.StageManager;
import domain.ShopAPI;
import entities.Mitarbeiter;
import entities.enums.EreignisTyp;
import entities.enums.KategorieEreignisTyp;
import exceptions.personen.PasswortNameException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.rmi.RemoteException;


public class LoginController {

    // @FXML wird benötigt, damit JavaFX weiß, dass es sich um ein UI-Element handelt.
    // Name der Variable muss mit der ID in der login-view.fxml Datei übereinstimmen.
    @FXML
    private Label statusLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private ToggleButton showPasswordToggle;

    private final ShopAPI shopAPI = ShopAPIClient.getShopAPI();

    private final LoginUtils loginUtils;

    public LoginController() throws RemoteException {
        loginUtils = new LoginUtils();
    }

    public void initialize() {
        showPasswordHandler();
    }

    /**
     * Methode login wird automatisch mit der UI verknüpft. Dafür muss man nur in der login-view.fxml Datei gehen,
     * welches sich unter resources/com/centerio/eshopfx befindet.
     */
    public void login() throws IOException {
        resetLoginStyles();
        if (usernameField.getText().isEmpty()) {
            // man kann auch direkt CSS-Styles in JavaFX setzen
            usernameField.getStyleClass().add("text-field-error");
            statusLabel.setText("Bitte geben Sie Benutzername ein!");
            statusLabel.getStyleClass().add("label-error");
            return;
        }
        if (passwordField.getText().isEmpty()) {
            // oder man fügt CSS-Styles in einer CSS-Klasse und fügt diese Klasse zu dem UI-Element hinzu
            passwordField.getStyleClass().add("text-field-error");
            statusLabel.setText("Bitte geben Sie Passwort ein!");
            statusLabel.getStyleClass().add("label-error");
            return;
        }
        try {
            var login = loginUtils.login(usernameField.getText(), passwordField.getText());
            if (login instanceof Mitarbeiter) {
                StageManager.getInstance().switchScene(SceneRoutes.MITARBEITER_VIEW);
            } else {
                StageManager.getInstance().switchScene(SceneRoutes.KUNDE_VIEW);
            }
        } catch (PasswortNameException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        } finally {
            statusLabel.setText("Login fehlgeschlagen!");
            statusLabel.getStyleClass().add("label-error");
            shopAPI.addEreignis(KategorieEreignisTyp.PERSONEN_EREIGNIS, EreignisTyp.LOGIN, null, false);
        }
    }

    public void showPasswordHandler() {
        // ToggleButton mit Auge-Icon austauschen:
        Image iconEye = new Image(getClass().getResourceAsStream("/com/centerio/eshopfx/static/visibility_icon.png"));
        Image iconEyeOff = new Image(getClass().getResourceAsStream("/com/centerio/eshopfx/static/visibility_off_icon.png"));
        ImageView iconEyeView = new ImageView(iconEye);
        ImageView iconEyeOffView = new ImageView(iconEyeOff);
        iconEyeView.setFitHeight(24);
        iconEyeView.setFitWidth(24);
        iconEyeOffView.setFitHeight(24);
        iconEyeOffView.setFitWidth(24);
        showPasswordToggle.setGraphic(iconEyeView);
        // Default style für den ToggleButton entfernen
        showPasswordToggle.getStyleClass().remove("toggle-button");
        // Neue CSS-Klasse für den ToggleButton hinzufügen
        showPasswordToggle.getStyleClass().add("password-eye");
        // bindBidirectional bindet zwei Properties miteinander. Wenn sich der Wert von einem Property ändert,
        // wird der Wert des anderen Properties auch geändert.
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
        showPasswordToggle.selectedProperty().addListener(observable -> {
            var isSelected = showPasswordToggle.isSelected();
            if (isSelected) {
                showPasswordToggle.setGraphic(iconEyeOffView);
                passwordField.setVisible(false);
                passwordTextField.setVisible(true);
            } else {
                showPasswordToggle.setGraphic(iconEyeView);
                passwordField.setVisible(true);
                passwordTextField.setVisible(false);
            }
        });
    }

    /**
     * lässt den Nutzer den Loginvorgang durch das Drücken des Enterkeys starten
     * @param e
     * @throws IOException
     */
    public void loginKey(KeyEvent e) throws IOException {
        if (e.getCode().equals(KeyCode.ENTER)) {
            login();
        }
    }

    /**
     * entfernt die roten MArkierungen um die Textfelder
     */
    public void resetLoginStyles() {
        usernameField.getStyleClass().remove("text-field-error");
        passwordField.getStyleClass().remove("text-field-error");
        statusLabel.setText("");
        statusLabel.getStyleClass().remove("label-error");
    }

    /**
     * wechselt zum Registrierungs-UI
     */
    public void registrieren() {
        StageManager.getInstance().switchScene(SceneRoutes.REGISTRIEREN_VIEW);
    }

}
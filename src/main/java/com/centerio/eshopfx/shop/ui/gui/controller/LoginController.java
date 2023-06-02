package com.centerio.eshopfx.shop.ui.gui.controller;

import com.centerio.eshopfx.shop.domain.ShopAPI;
import com.centerio.eshopfx.shop.ui.gui.utils.SceneRoutes;
import com.centerio.eshopfx.shop.ui.gui.utils.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class LoginController {

    // @FXML wird benötigt, damit JavaFX weiß, dass es sich um ein UI-Element handelt.
    // Name der Variable muss mit der ID in der login-view.fxml Datei übereinstimmen.
    @FXML
    private Label welcomeLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private CheckBox showPasswordCheckBox;

    private final ShopAPI shopAPI = ShopAPI.getInstance();

    public void initialize() {
        welcomeLabel.setText("Warten auf Login...");
        showPasswordHandler();
    }

    /**
     * Methode login wird automatisch mit der UI verknüpft. Dafür muss man nur in der login-view.fxml Datei gehen,
     * welches sich unter resources/com/centerio/eshopfx befindet.
     */
    public void login() {
        resetLoginStyles();
        if (usernameField.getText().isEmpty()) {
            // man kann auch direkt CSS-Styles in JavaFX setzen
            usernameField.setStyle("-fx-border-color: red;");
            welcomeLabel.setText("Bitte geben Sie Benutzername ein!");
            return;
        }
        if (passwordField.getText().isEmpty()) {
            // oder man fügt CSS-Styles in einer CSS-Klasse und fügt diese Klasse zu dem UI-Element hinzu
            passwordField.getStyleClass().add("text-field-error");
            welcomeLabel.setText("Bitte geben Sie Passwort ein!");
            return;
        }
        var login = shopAPI.login(usernameField.getText(), passwordField.getText());
        if (login != null) {
            StageManager.getInstance().switchScene(SceneRoutes.HOME_VIEW);
        } else {
            welcomeLabel.setText("Login fehlgeschlagen!");
        }
    }

    public void showPasswordHandler() {
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
        showPasswordCheckBox.selectedProperty().addListener(observable -> {
            var isSelected = showPasswordCheckBox.isSelected();
            if (isSelected) {
                passwordField.setVisible(false);
                passwordTextField.setVisible(true);
            } else {
                passwordField.setVisible(true);
                passwordTextField.setVisible(false);
            }
        });
    }

    public void loginKey(KeyEvent e) {
        if (e.getCode().equals(KeyCode.ENTER)) {
            login();
        }
    }

    public void resetLoginStyles() {
        usernameField.setStyle("");
        passwordField.getStyleClass().remove("error");
    }

}
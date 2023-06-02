package com.centerio.eshopfx.shop.ui.gui.controller;

import com.centerio.eshopfx.shop.domain.ShopAPI;
import com.centerio.eshopfx.shop.ui.gui.utils.SceneRoutes;
import com.centerio.eshopfx.shop.ui.gui.utils.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class LoginController {

    @FXML
    private Label welcomeText;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final ShopAPI shopAPI = ShopAPI.getInstance();

    /**
     * Methode login wird automatisch mit der UI verknüpft. Dafür muss man nur in der login-view.fxml Datei gehen,
     * welches sich unter resources/com/centerio/eshopfx befindet.
     */
    public void login() {
        resetLoginStyles();
        if (usernameField.getText().isEmpty()) {
            usernameField.setStyle("-fx-border-color: red;");
            welcomeText.setText("Bitte geben Sie Benutzername ein!");
            return;
        }
        if (passwordField.getText().isEmpty()) {
            passwordField.getStyleClass().add("error");
            welcomeText.setText("Bitte geben Sie Passwort ein!");
            return;
        }
        var login = shopAPI.login(usernameField.getText(), passwordField.getText());
        if (login != null) {
            StageManager.getInstance().switchScene(SceneRoutes.HOME_VIEW);
        } else {
            welcomeText.setText("Login fehlgeschlagen!");
        }
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
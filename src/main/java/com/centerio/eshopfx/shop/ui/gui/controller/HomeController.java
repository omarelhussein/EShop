package com.centerio.eshopfx.shop.ui.gui.controller;

import com.centerio.eshopfx.shop.entities.UserContext;
import com.centerio.eshopfx.shop.ui.gui.utils.SceneRoutes;
import com.centerio.eshopfx.shop.ui.gui.utils.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeController {

    @FXML
    private Label titleLabel;


    public void initialize() {
        titleLabel.setText("Willkommen, " + UserContext.getUser().getNutzername());
    }

    public void logout() {
        StageManager.getInstance().switchScene(SceneRoutes.LOGIN_VIEW);
    }

}

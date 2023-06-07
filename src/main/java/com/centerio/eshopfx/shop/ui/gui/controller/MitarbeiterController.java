package com.centerio.eshopfx.shop.ui.gui.controller;

import com.centerio.eshopfx.shop.domain.ArtikelService;
import com.centerio.eshopfx.shop.domain.ShopAPI;
import com.centerio.eshopfx.shop.entities.Artikel;
import com.centerio.eshopfx.shop.entities.Massenartikel;
import com.centerio.eshopfx.shop.ui.gui.utils.SceneRoutes;
import com.centerio.eshopfx.shop.ui.gui.utils.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class MitarbeiterController {
    @FXML
    private ListView<Artikel> artikelList;

    @FXML
    private Label massenArtikelLabel;

    @FXML
    private TextField packGroesseFeld;

    @FXML
    private CheckBox massenArtikelCheckbox;

    @FXML
    private Label artikelBezeichnungLabel;

    @FXML
    private TextField artikelBezeichnungFeld;

    @FXML
    private Label artikelPreisLabel;

    @FXML
    private TextField artikelPreisFeld;

    @FXML
    private Label artikelBestandLabel;

    @FXML
    private TextField artikelBestandFeld;

    @FXML
    private Button addArtikelButton;
    private final ShopAPI shopAPI = ShopAPI.getInstance();
    /**
     * Diese Methode wird aufgerufen, wenn die View geladen wird.
     * Sie wird nach dem Konstruktor aufgerufen.
     * <p>
     * Hier können UI-Elemente initialisiert werden. In dem Konstruktor können UI-Elemente noch nicht initialisiert werden,
     * da diese noch nicht geladen wurden.
     */
    public void initialize() throws IOException {
        initializeArtikel();
    }

    public void save() {
        shopAPI.speichern();
    }

    public void artikelAdd() throws IOException {
        resetArtikelAddStyles();
        if(FieldCheck(artikelBezeichnungFeld)){
            return;
        }
        if(FieldCheck(artikelPreisFeld)){
            return;
        }
        if(FieldCheck(artikelBestandFeld)){
            return;
        }
        try {
            if(massenArtikelCheckbox.isSelected()){
                Massenartikel artikel = new Massenartikel(ArtikelService.getInstance().getNaechsteId(), artikelBezeichnungFeld.getText(),
                        Double.parseDouble(artikelPreisFeld.getText()), Integer.parseInt(artikelBestandFeld.getText()), Integer.parseInt(packGroesseFeld.getText()));
                ArtikelService.getInstance().getArtikelList().add(artikel);
            } else {
                Artikel artikel = new Artikel(ArtikelService.getInstance().getNaechsteId(), artikelBezeichnungFeld.getText(),
                        (Double.parseDouble(artikelPreisFeld.getText())), Integer.parseInt(artikelBestandFeld.getText()));
                ArtikelService.getInstance().getArtikelList().add(artikel);
            }
            initializeArtikel();
        }catch (RuntimeException e) {
            addArtikelButton.setStyle("-fx-border-color: red;");
        }
    }

    public void massenArtikelHandler(){
        massenArtikelCheckbox.selectedProperty().addListener(observable -> {
            var isSelected = massenArtikelCheckbox.isSelected();
            if (isSelected) {
                packGroesseFeld.setVisible(true);
            } else {
                packGroesseFeld.setVisible(false);
            }
        });
    }
    public void resetArtikelAddStyles() {
        artikelBezeichnungFeld.setStyle("");
        artikelPreisFeld.setStyle("");
        artikelBestandFeld.setStyle("");
    }
    public void initializeArtikel() throws IOException {
        artikelList.getItems().clear();
        for(Artikel artikel : ArtikelService.getInstance().getArtikelList())
            artikelList.getItems().add(artikel);
    }
    public void logout() {
        shopAPI.logout();
        StageManager.getInstance().switchScene(SceneRoutes.LOGIN_VIEW);
    }

    private boolean FieldCheck(TextField field) {
        if (field.getText().isEmpty()) {
            field.setStyle("-fx-border-color: red;");
            return true;
        }
        return false;
    }
}

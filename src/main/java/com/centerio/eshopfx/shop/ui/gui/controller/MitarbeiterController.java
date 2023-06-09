package com.centerio.eshopfx.shop.ui.gui.controller;

import com.centerio.eshopfx.shop.domain.ArtikelService;
import com.centerio.eshopfx.shop.domain.ShopAPI;
import com.centerio.eshopfx.shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.entities.Artikel;
import com.centerio.eshopfx.shop.entities.Massenartikel;
import com.centerio.eshopfx.shop.ui.gui.utils.SceneRoutes;
import com.centerio.eshopfx.shop.ui.gui.utils.StageManager;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class MitarbeiterController {
    @FXML
    TableColumn<Artikel, Integer> artikelNummerColumn;
    @FXML
    TableColumn<Artikel, String> artikelBezeichnungColumn;
    @FXML
    TableColumn<Artikel, Double> artikelPreisColumn;
    @FXML
    TableColumn<Artikel, Integer> artikelBestandColumn;
    @FXML
    TableColumn<Artikel, Integer> artikelPackgroesseColumn;

    @FXML
    private TableView<Artikel> artikelTableView;

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
    @FXML
    private Button removeArtikelButton;
    private final ShopAPI shopAPI = ShopAPI.getInstance();
    /**
     * Diese Methode wird aufgerufen, wenn die View geladen wird.
     * Sie wird nach dem Konstruktor aufgerufen.
     * <p>
     * Hier können UI-Elemente initialisiert werden. In dem Konstruktor können UI-Elemente noch nicht initialisiert werden,
     * da diese noch nicht geladen wurden.
     */
    public void initialize() throws IOException {
        initializeArtikelView();
        setArtikelInTable();
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
                ShopAPI.getInstance().addArtikel(artikel);
            } else {
                Artikel artikel = new Artikel(ArtikelService.getInstance().getNaechsteId(), artikelBezeichnungFeld.getText(),
                        (Double.parseDouble(artikelPreisFeld.getText())), Integer.parseInt(artikelBestandFeld.getText()));
                ShopAPI.getInstance().addArtikel(artikel);
            }
            setArtikelInTable();
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

    public void initializeArtikelView(){
        artikelNummerColumn = new TableColumn("Nummer");
        artikelBezeichnungColumn = new TableColumn("Bezeichnung");
        artikelPreisColumn = new TableColumn("Preis");
        artikelBestandColumn = new TableColumn("Bestand");
        artikelPackgroesseColumn = new TableColumn("Packgröße");
        artikelTableView.getColumns().addAll(artikelNummerColumn, artikelBezeichnungColumn, artikelPreisColumn, artikelBestandColumn, artikelPackgroesseColumn);
        artikelNummerColumn.setCellValueFactory(new PropertyValueFactory<Artikel, Integer>("artNr"));
        artikelBezeichnungColumn.setCellValueFactory(new PropertyValueFactory<Artikel, String>("bezeichnung"));
        artikelPreisColumn.setCellValueFactory(new PropertyValueFactory<Artikel, Double>("preis"));
        artikelBestandColumn.setCellValueFactory(new PropertyValueFactory<Artikel, Integer>("bestand"));
        artikelPackgroesseColumn.setCellValueFactory(new PropertyValueFactory<Artikel, Integer>("pgroesse"));
    }
    public void setArtikelInTable() throws IOException {
        artikelTableView.getItems().clear();
        ObservableList<Artikel> artikelObservableList = FXCollections.observableArrayList();
        for(Artikel artikel : ArtikelService.getInstance().getArtikelList()){
            artikelObservableList.add(artikel);
        }
        artikelTableView.setItems(artikelObservableList);
    }

    public void removeArtikel() throws IOException, ArtikelNichtGefundenException {
        int selectedId = artikelTableView.getSelectionModel().getSelectedIndex();
        ShopAPI.getInstance().removeArtikel(artikelTableView.getItems().get(selectedId).getArtNr());
        artikelTableView.getItems().remove(selectedId);
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

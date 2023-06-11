package com.centerio.eshopfx.shop.ui.gui.controller;

import com.centerio.eshopfx.shop.domain.ArtikelService;
import com.centerio.eshopfx.shop.domain.ShopAPI;
import com.centerio.eshopfx.shop.domain.WarenkorbService;
import com.centerio.eshopfx.shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.entities.Artikel;
import com.centerio.eshopfx.shop.entities.Massenartikel;
import com.centerio.eshopfx.shop.entities.WarenkorbArtikel;
import com.centerio.eshopfx.shop.ui.gui.utils.SceneRoutes;
import com.centerio.eshopfx.shop.ui.gui.utils.StageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class KundeController {
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
    private Button addToWarenkorbButton;
    @FXML
    private Button kaufenButton;
    @FXML
    private TableView warenkorbTableView;
    @FXML
    private TableColumn<WarenkorbArtikel, String> warenkorbArtikelStringTableColumn;
    @FXML
    private TableColumn<WarenkorbArtikel, Integer> warenkorbArtikelAnzahlTableColumn;


    private final ShopAPI shopAPI = ShopAPI.getInstance();

    /**
     * Diese Methode wird aufgerufen, wenn die View geladen wird.
     * Sie wird nach dem Konstruktor aufgerufen.
     * <p>
     * Hier können UI-Elemente initialisiert werden. In dem Konstruktor können UI-Elemente noch nicht initialisiert werden,
     * da diese noch nicht geladen wurden.
     */
    public void initialize() throws IOException {
        initializeWarenkorbView();
        setWarenkorbInTable();
        initializeArtikelView();
        setArtikelInTable();
    }

    public void save() {
        shopAPI.speichern();
    }

    public void initializeWarenkorbView() {
        warenkorbArtikelStringTableColumn = new TableColumn<WarenkorbArtikel, String>("Artikel");
        warenkorbArtikelAnzahlTableColumn = new TableColumn<WarenkorbArtikel, Integer>("Anzahl");
        warenkorbTableView.getColumns().addAll(warenkorbArtikelStringTableColumn, warenkorbArtikelAnzahlTableColumn);
        warenkorbArtikelStringTableColumn.setCellValueFactory(new PropertyValueFactory<WarenkorbArtikel, String>("artikelbezeichnung"));
        warenkorbArtikelAnzahlTableColumn.setCellValueFactory(new PropertyValueFactory<WarenkorbArtikel, Integer>("anzahl"));
    }

    public void kaufeWarenkorb() throws BestandUeberschrittenException, ArtikelNichtGefundenException, IOException {
        warenkorbTableView.getItems().clear();
        ShopAPI.getInstance().kaufen();
        setWarenkorbInTable();
        setArtikelInTable();
    }

    public void setWarenkorbInTable() throws IOException {
        warenkorbTableView.getItems().clear();
        ObservableList<WarenkorbArtikel> warenkorbObservableList = FXCollections.observableArrayList();
        for (WarenkorbArtikel warenkorbartikel : WarenkorbService.getInstance().getWarenkorb().getWarenkorbArtikelList()) {
            warenkorbObservableList.add(warenkorbartikel);
        }
        warenkorbTableView.setItems(warenkorbObservableList);
    }

    public void initializeArtikelView() {
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
        artikelPackgroesseColumn.setCellValueFactory(new PropertyValueFactory<Artikel, Integer>("packgroesse"));
    }

    public void setArtikelInTable() throws IOException {
        artikelTableView.getItems().clear();
        ObservableList<Artikel> artikelObservableList = FXCollections.observableArrayList();
        for (Artikel artikel : ArtikelService.getInstance().getArtikelList()) {
            artikelObservableList.add(artikel);
        }
        artikelTableView.setItems(artikelObservableList);
    }

    public void logout() {
        shopAPI.logout();
        StageManager.getInstance().switchScene(SceneRoutes.LOGIN_VIEW);
    }

    public void toWarenkorb() throws IOException {
        try {
            int selectedId = artikelTableView.getSelectionModel().getSelectedIndex();
            if (selectedId >= 0) {
                Artikel artikel = artikelTableView.getItems().get(selectedId);
                ShopAPI.getInstance().addArtikelToWarenkorb(artikel.getArtNr(), 1);
                setWarenkorbInTable();
            } else {
                addToWarenkorbButton.setStyle("-fx-border-color: red;");
            }
        } catch (IOException | ArtikelNichtGefundenException | BestandUeberschrittenException |
                 WarenkorbArtikelNichtGefundenException e) {
            addToWarenkorbButton.setStyle("-fx-border-color: red;");
        }
    }

    private boolean FieldCheck(TextField field) {
        if (field.getText().isEmpty()) {
            field.setStyle("-fx-border-color: red;");
            return true;
        }
        return false;
    }
}

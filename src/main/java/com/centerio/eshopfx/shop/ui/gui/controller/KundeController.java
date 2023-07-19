package com.centerio.eshopfx.shop.ui.gui.controller;

import com.centerio.eshopfx.shop.domain.*;
import com.centerio.eshopfx.shop.domain.exceptions.artikel.AnzahlPackgroesseException;
import com.centerio.eshopfx.shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.entities.*;
import com.centerio.eshopfx.shop.entities.enums.EreignisTyp;
import com.centerio.eshopfx.shop.ui.gui.concerns.ArtikelTableConcern;
import com.centerio.eshopfx.shop.ui.gui.concerns.WarenkorbTableConcern;
import com.centerio.eshopfx.shop.ui.gui.utils.SceneRoutes;
import com.centerio.eshopfx.shop.ui.gui.utils.StageManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;

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
    @FXML
    private TableColumn<WarenkorbArtikel, Double> warenkorbArtikelPreisTableColumn;
    @FXML
    private Button warenkorbEntfernenButton;
    @FXML
    private TextField warenkorbAnzahlField;
    @FXML
    private TextField artikelAnzahlField;
    @FXML
    private Label gesamtPreis;
    @FXML
    private TextField suchField;
    @FXML
    private Button ClearButton;

    Registry registry = LocateRegistry.getRegistry("LocalHost", 1099);

    private final RemoteInterface shopAPI = (RemoteInterface) registry.lookup("RemoteObject");

    public KundeController() throws RemoteException, NotBoundException {
    }

    /**
     * Diese Methode wird aufgerufen, wenn die View geladen wird.
     * Sie wird nach dem Konstruktor aufgerufen.
     * <p>
     * Hier können UI-Elemente initialisiert werden. In dem Konstruktor können UI-Elemente noch nicht initialisiert werden,
     * da diese noch nicht geladen wurden.
     */
    public void initialize() {
        try {
            ArtikelTableConcern artikelTableConcern = new ArtikelTableConcern(artikelNummerColumn, artikelBezeichnungColumn, artikelPreisColumn, artikelBestandColumn, artikelPackgroesseColumn, artikelTableView, suchField, artikelAnzahlField, addToWarenkorbButton, ClearButton);
            WarenkorbTableConcern warenkorbTableConcern = new WarenkorbTableConcern(warenkorbArtikelStringTableColumn, warenkorbArtikelAnzahlTableColumn, warenkorbArtikelPreisTableColumn, warenkorbTableView, warenkorbEntfernenButton, warenkorbAnzahlField, kaufenButton, gesamtPreis);
            artikelTableConcern.initializeArtikelView();
            artikelTableConcern.setArtikelInTable();
            artikelTableConcern.setKundeEventHandlerForArtikel(warenkorbTableConcern);
            warenkorbTableConcern.initializeWarenkorbView();
            warenkorbTableConcern.setWarenkorbInTable();
            warenkorbTableConcern.setEventHandlerForWarenkorb();
        } catch(IOException e) {
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void save() throws RemoteException {
        shopAPI.speichern();
    }

    public void logout() throws IOException {
        shopAPI.logout();
        StageManager.getInstance().switchScene(SceneRoutes.LOGIN_VIEW);
    }
}

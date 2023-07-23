package com.centerio.eshopfx.gui.controller;

import com.centerio.eshopfx.ShopAPIClient;
import com.centerio.eshopfx.gui.components.ArtikelTable;
import com.centerio.eshopfx.gui.components.WarenkorbTable;
import com.centerio.eshopfx.gui.utils.LoginUtils;
import com.centerio.eshopfx.gui.utils.SceneRoutes;
import com.centerio.eshopfx.gui.utils.StageManager;
import domain.ShopAPI;
import entities.Artikel;
import entities.UserContext;
import entities.WarenkorbArtikel;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

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
    private TableView warenkorbTable;
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

    private final ShopAPI shopAPI;

    public KundeController() throws RemoteException, NotBoundException {
        shopAPI = ShopAPIClient.getShopAPI();
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
            ArtikelTable artikelTable = new ArtikelTable(artikelNummerColumn,
                    artikelBezeichnungColumn,
                    artikelPreisColumn,
                    artikelBestandColumn,
                    artikelPackgroesseColumn,
                    artikelTableView,
                    suchField,
                    artikelAnzahlField,
                    addToWarenkorbButton,
                    ClearButton);
            WarenkorbTable warenkorbTableView = new WarenkorbTable(warenkorbArtikelStringTableColumn,
                    warenkorbArtikelAnzahlTableColumn,
                    warenkorbArtikelPreisTableColumn,
                    warenkorbTable,
                    warenkorbEntfernenButton,
                    warenkorbAnzahlField,
                    kaufenButton,
                    gesamtPreis);
            artikelTable.initializeArtikelView();
            artikelTable.setArtikelInTable();
            artikelTable.setKundeEventHandlerForArtikel(warenkorbTableView);
            warenkorbTableView.initializeWarenkorbView();
            warenkorbTableView.setWarenkorbInTable();
            warenkorbTableView.setEventHandlerForWarenkorb();
        } catch (IOException e) {
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void logout() {
        // Nutzer muss (noch) nichts speichern, daher direkt zur Login-View wechseln
        // Speicherung ist derzeit nur bei der Admin-View notwendig
        try {
            new LoginUtils().logout();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StageManager.getInstance().switchScene(SceneRoutes.LOGIN_VIEW);
    }
}

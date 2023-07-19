package com.centerio.eshopfx.shop.ui.gui.controller;

import com.centerio.eshopfx.shop.domain.*;
import com.centerio.eshopfx.shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.domain.exceptions.personen.PersonVorhandenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.entities.*;
import com.centerio.eshopfx.shop.ui.gui.concerns.ArtikelTableConcern;
import com.centerio.eshopfx.shop.ui.gui.concerns.EreignisTableConcern;
import com.centerio.eshopfx.shop.ui.gui.concerns.PersonenTableConcern;
import com.centerio.eshopfx.shop.ui.gui.utils.SceneRoutes;
import com.centerio.eshopfx.shop.ui.gui.utils.StageManager;
import javafx.beans.Observable;
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
import org.w3c.dom.Text;


import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

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
    private TableColumn<Ereignis, Integer> ereignisPersNrTableColumn;
    @FXML
    private TableColumn<Ereignis, String> ereignisPersNameTableColumn;
    @FXML
    private TableColumn<Ereignis, String> ereignisArtTableColumn;
    @FXML
    private TableColumn<Ereignis, String> ereignisObjektTableColumn;
    @FXML
    private TableColumn<Ereignis, String> ereignisDatumTableColumn;
    @FXML
    private TableColumn<Ereignis, String> ereignisBestandTableColumn;

    @FXML
    private TableView ereignisTableView;

    @FXML
    private TableColumn<Person, String> personTypColumn;
    @FXML
    private TableColumn<Person, String> nutzernameColumn;
    @FXML
    private TableColumn<Person, String> nameColumn;
    @FXML
    private TableColumn<Person, Integer> personNummerColumn;

    @FXML
    private TableView<Artikel> artikelTableView;

    @FXML
    private TableView<Person> personenTableView;

    @FXML
    private Label massenArtikelLabel;

    @FXML
    private TextField packGroesseFeld;

    @FXML
    private CheckBox massenArtikelCheckbox;

    @FXML
    private TextField artikelBezeichnungFeld;


    @FXML
    private TextField artikelPreisFeld;


    @FXML
    private TextField artikelBestandFeld;

    @FXML
    private TabPane mitarbeiterPane = new TabPane();

    @FXML
    private Button addArtikelButton;


    @FXML
    private TextField mitarbeiterNameField;

    @FXML
    private TextField nutzernameField;

    @FXML
    private TextField passwortField;

    @FXML
    private Button registerMitarbeiterButton;

    @FXML
    private Button editArtikelButton;

    @FXML
    private Button removeArtikelButton;
    @FXML
    private TextField suchField;
    @FXML
    private ComboBox<String> dropDownEreignisse;
    @FXML
    private Button bestandshistorieSuchenButton;

    @FXML
    private Button ClearButton;
    Registry registry = LocateRegistry.getRegistry("LocalHost", 1099);

    private final RemoteInterface shopAPI = (RemoteInterface) registry.lookup("RemoteObject");

    private Tab selctedTab = mitarbeiterPane.getSelectionModel().getSelectedItem();

    public MitarbeiterController() throws RemoteException, NotBoundException {
    }

    /**
     * Diese Methode wird aufgerufen, wenn die View geladen wird.
     * Sie wird nach dem Konstruktor aufgerufen.
     * <p>
     * Hier können UI-Elemente initialisiert werden. In dem Konstruktor können UI-Elemente noch nicht initialisiert werden,
     * da diese noch nicht geladen wurden.
     */
    public void initialize() throws IOException, NotBoundException {
        ArtikelTableConcern artikelTableConcern = new ArtikelTableConcern(artikelNummerColumn, artikelBezeichnungColumn, artikelPreisColumn, artikelBestandColumn,
                artikelPackgroesseColumn, artikelTableView, suchField, artikelBezeichnungFeld, artikelPreisFeld, massenArtikelCheckbox,
                packGroesseFeld, artikelBestandFeld, addArtikelButton, editArtikelButton, removeArtikelButton, ClearButton);
        PersonenTableConcern personenTableConcern = new PersonenTableConcern(personTypColumn,nutzernameColumn,nameColumn,
                personNummerColumn,personenTableView,mitarbeiterNameField,nutzernameField, passwortField,registerMitarbeiterButton);
        EreignisTableConcern ereignisTableConcern = new EreignisTableConcern(ereignisPersNrTableColumn,
                ereignisPersNameTableColumn, ereignisArtTableColumn, ereignisObjektTableColumn, ereignisDatumTableColumn,
                ereignisBestandTableColumn, ereignisTableView, dropDownEreignisse, bestandshistorieSuchenButton);
        artikelTableConcern.initializeArtikelView();
        artikelTableConcern.setArtikelInTable();
        artikelTableConcern.setMitarbeiterEventHandlersForArtikel();
        artikelTableConcern.artikelOnClickToTextfield();
        artikelTableConcern.massenArtikelHandler();
        personenTableConcern.initializePersonView();
        personenTableConcern.setPersonInTable();
        personenTableConcern.setEventHandlerForPersonen();
        ereignisTableConcern.initializeEreignisView();
        ereignisTableConcern.setEreingisInTable();

        //initializeMitarbeiterTab();
    }

    public void save() throws RemoteException {
        shopAPI.speichern();
    }

    public void logout() throws IOException {
        shopAPI.logout();
        StageManager.getInstance().switchScene(SceneRoutes.LOGIN_VIEW);
    }

    // int mitarbeiterNr, String nutzername, String name, String passwort

    public void deleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Diese Aktion kann nicht rüchgängig gemacht werden!");
        alert.setTitle("Account löschen");
        alert.setHeaderText("Sind sie sich sicher dass sie ihren Account löschen wollen?");

        // Hinzufügen der Buttons
        ButtonType buttonTypeConfirm = new ButtonType("Bestätigen");
        ButtonType buttonTypeCancel = new ButtonType("Abbrechen");

        alert.getButtonTypes().setAll(buttonTypeConfirm, buttonTypeCancel);



        // Event Handler für den Bestätigen-Button
        alert.setOnCloseRequest(dialogEvent -> {
                    if (alert.getResult() == buttonTypeConfirm) {
                        try {
                            shopAPI.accountLoeschen();
                            logout();
                            alert.close(); // Popup-Fenster schließen
                        } catch (IOException e) {
                            System.out.println(e);
                        }
                    }
                });


        // Event Handler für den Abbrechen-Button
        alert.setOnHidden(dialogEvent -> {
            if (alert.getResult() == buttonTypeCancel) {
                alert.close(); // Popup-Fenster schließen
            }
        });

        alert.showAndWait();
    }

}

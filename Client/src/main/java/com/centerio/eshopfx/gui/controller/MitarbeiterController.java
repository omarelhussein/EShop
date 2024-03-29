package com.centerio.eshopfx.gui.controller;

import com.centerio.eshopfx.ShopAPIClient;
import com.centerio.eshopfx.gui.components.ArtikelGraphTable;
import com.centerio.eshopfx.gui.components.ArtikelTable;
import com.centerio.eshopfx.gui.components.EreignisTable;
import com.centerio.eshopfx.gui.components.PersonenTable;
import com.centerio.eshopfx.gui.utils.LoginUtils;
import com.centerio.eshopfx.gui.utils.SceneRoutes;
import com.centerio.eshopfx.gui.utils.StageManager;
import domain.ShopAPI;
import entities.Artikel;
import entities.Ereignis;
import entities.Person;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

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
    private TableView<Artikel> artikelGraphTableView;

    @FXML
    private TableView<Person> personenTableView;

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

    @FXML private LineChart<Number, Number> graph;

    @FXML private Button clearGraphButton;

    @FXML
    private Button ClearButton;
    private final ShopAPI shopAPI = ShopAPIClient.getShopAPI();
    private Tab selctedTab = mitarbeiterPane.getSelectionModel().getSelectedItem();
    private final LoginUtils loginUtils;
    private ArtikelGraphTable artikelGraphTable;

    public MitarbeiterController() throws RemoteException {
        loginUtils = new LoginUtils();
    }

    /**
     * Diese Methode wird aufgerufen, wenn die View geladen wird.
     * Sie wird nach dem Konstruktor aufgerufen.
     * <p>
     * Hier können UI-Elemente initialisiert werden. In dem Konstruktor können UI-Elemente noch nicht initialisiert werden,
     * da diese noch nicht geladen wurden.
     */
    public void initialize() throws IOException, NotBoundException {
        ArtikelTable artikelTable = new ArtikelTable(artikelNummerColumn, artikelBezeichnungColumn, artikelPreisColumn, artikelBestandColumn,
                artikelPackgroesseColumn, artikelTableView, suchField, artikelBezeichnungFeld, artikelPreisFeld, massenArtikelCheckbox,
                packGroesseFeld, artikelBestandFeld, addArtikelButton, editArtikelButton, removeArtikelButton, ClearButton);
        PersonenTable personenTable = new PersonenTable(personTypColumn, nutzernameColumn, nameColumn,
                personNummerColumn, personenTableView, mitarbeiterNameField, nutzernameField, passwortField, registerMitarbeiterButton);
        EreignisTable ereignisTable = new EreignisTable(ereignisPersNrTableColumn,
                ereignisPersNameTableColumn, ereignisArtTableColumn, ereignisObjektTableColumn, ereignisDatumTableColumn,
                ereignisBestandTableColumn, ereignisTableView, dropDownEreignisse, bestandshistorieSuchenButton);
        artikelGraphTable = new ArtikelGraphTable(artikelNummerColumn, artikelBezeichnungColumn,
                artikelGraphTableView, graph, clearGraphButton);
        artikelTable.initializeArtikelView();
        artikelTable.refreshTable();
        artikelTable.setMitarbeiterEventHandlersForArtikel();
        artikelTable.handleRowClicked();
        artikelTable.massenArtikelHandler();
        artikelGraphTable.initializeArtikelGraphView();
        artikelGraphTable.refreshTable();
        personenTable.initializePersonView();
        personenTable.setPersonInTable();
        personenTable.setEventHandlerForPersonen();
        ereignisTable.initializeEreignisView();
        ereignisTable.setEreingisInTable();
    }

    /**
     * speichert die aktuellen Listen ab
     * @throws RemoteException
     */
    public void save() throws RemoteException {
        shopAPI.speichern();
    }

    /**
     * loggt den eingeloggten Nutzer aus
     * @throws IOException
     */
    public void logout() throws IOException {
        loginUtils.logout();
//        save(); // FIXME: Eine Exception wird geworfen weil Ereignis noch kaputt ist
        StageManager.getInstance().switchScene(SceneRoutes.LOGIN_VIEW);
    }

    /**
     * löscht den Account des eingeloggten Nutzers
     */
    public void deleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Diese Aktion kann nicht rückgängig gemacht werden!");
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
                    loginUtils.logout();
                    alert.close(); // Pop-up-Fenster schließen
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        });


        // Event Handler für den Abbrechen-Button
        alert.setOnHidden(dialogEvent -> {
            if (alert.getResult() == buttonTypeCancel) {
                alert.close(); // Pop-up-Fenster schließen
            }
        });
        alert.showAndWait();
    }

    /**
     * aktuallisiert die Tabelle im Graphtab
     * @throws IOException
     */
    public void graphTableAktualisieren() throws IOException {
        artikelGraphTable.refreshTable();
    }
}

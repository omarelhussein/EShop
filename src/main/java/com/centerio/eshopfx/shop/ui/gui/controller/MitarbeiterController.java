package com.centerio.eshopfx.shop.ui.gui.controller;

import com.centerio.eshopfx.shop.domain.ArtikelService;
import com.centerio.eshopfx.shop.domain.PersonenService;
import com.centerio.eshopfx.shop.domain.ShopAPI;
import com.centerio.eshopfx.shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.domain.exceptions.personen.PersonVorhandenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.entities.*;
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
    TableColumn<Person, String> personTypColumn;
    @FXML
    TableColumn<Person, String> nutzernameColumn;
    @FXML
    TableColumn<Person, String> nameColumn;
    @FXML
    TableColumn<Person, Integer> personNummerColumn;

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
    private TabPane mitarbeiterPane = new TabPane();

    @FXML
    private Button addArtikelButton;

    @FXML
    private Label mitarbeiterNameLabel;

    @FXML
    private TextField mitarbeiterNameField;

    @FXML
    private Label nutzernameLabel;

    @FXML
    private TextField nutzernameField;

    @FXML
    private Label passwortLabel;

    @FXML
    private TextField passwortField;

    @FXML
    private Button registerMitarbeiterButton;

    @FXML
    private Button removeArtikelButton;
    private final ShopAPI shopAPI = ShopAPI.getInstance();

    private Tab selctedTab = mitarbeiterPane.getSelectionModel().getSelectedItem();

    /**
     * Diese Methode wird aufgerufen, wenn die View geladen wird.
     * Sie wird nach dem Konstruktor aufgerufen.
     * <p>
     * Hier können UI-Elemente initialisiert werden. In dem Konstruktor können UI-Elemente noch nicht initialisiert werden,
     * da diese noch nicht geladen wurden.
     */
    public void initialize() throws IOException {
        initializeArtikelView();
        initializePersonView();
        setArtikelInTable();
        setPersonInTable();
        //initializeMitarbeiterTab();
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
            clearFelder();
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

    public void initializeMitarbeiterTab(){
        Tab artikelTab = new Tab("Artikel");
        Tab mitarbeiterTab = new Tab("Personal");

        mitarbeiterPane.getTabs().add(artikelTab);
        mitarbeiterPane.getTabs().add(mitarbeiterTab);
    }

    public void initializeArtikelView(){
        artikelNummerColumn = new TableColumn("Nummer");
        artikelBezeichnungColumn = new TableColumn("Bezeichnung");
        artikelPreisColumn = new TableColumn("Preis");
        artikelBestandColumn = new TableColumn("Bestand");
        artikelPackgroesseColumn = new TableColumn("Packgröße");
        artikelTableView.getColumns().addAll(artikelNummerColumn, artikelBezeichnungColumn, artikelPreisColumn, artikelBestandColumn, artikelPackgroesseColumn);
        artikelNummerColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getArtNr()).asObject());
        artikelBezeichnungColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getBezeichnung()));
        artikelPreisColumn.setCellValueFactory(p -> new SimpleDoubleProperty(p.getValue().getPreis()).asObject());
        artikelBestandColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getBestand()).asObject());
        artikelPackgroesseColumn.setCellValueFactory(p -> {if (p.getValue() instanceof Massenartikel){
                                                            return new SimpleIntegerProperty(((Massenartikel)p.getValue()).getPackgroesse()).asObject();
                                                            }
                                                            return null;
        });
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
        shopAPI.removeArtikel(artikelTableView.getItems().get(selectedId).getArtNr());
        artikelTableView.getItems().remove(selectedId);
    }
    public void logout() throws IOException {
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
    // int mitarbeiterNr, String nutzername, String name, String passwort
    public void mitarbeiterRegistrieren() throws PersonVorhandenException {
        shopAPI.registrieren(new Mitarbeiter(shopAPI.getNaechstePersId(), nutzernameField.getText(), mitarbeiterNameField.getText(), passwortField.getText()));
        setPersonInTable();
    }

    public void initializePersonView() {
        personNummerColumn = new TableColumn("Nummer");
        personTypColumn = new TableColumn("Typ");
        nutzernameColumn = new TableColumn("Nutzername");
        nameColumn = new TableColumn("Name");
        personenTableView.getColumns().addAll(personNummerColumn, personTypColumn, nutzernameColumn, nameColumn);
        personNummerColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getPersNr()).asObject());
        personTypColumn.setCellValueFactory(p -> {
                    if (p.getValue() instanceof Kunde) {
                        return new SimpleStringProperty("Kunde");
                    }
                    return new SimpleStringProperty("Mitarbeiter");
                });
        nutzernameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getNutzername()));
        nameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getName()));
    }

    private void setPersonInTable() {
        personenTableView.getItems().clear();
        ObservableList<Person> personObservableList = FXCollections.observableArrayList();
        for(Person person : shopAPI.getPersonList()){
            personObservableList.add(person);
        }
        personenTableView.setItems(personObservableList);
    }

    public void editArtikel() throws ArtikelNichtGefundenException, IOException {
        int selectedId = artikelTableView.getSelectionModel().getSelectedIndex();
        Artikel artikel = artikelTableView.getItems().get(selectedId);
        try {
            if(selectedId >= 0) {
                if (artikel instanceof Massenartikel) {
                    if (!artikelBezeichnungFeld.getText().equals("")) {
                        artikel.setBezeichnung(artikelBezeichnungFeld.getText());
                    }
                    if (!artikelPreisFeld.getText().equals("")) {
                        artikel.setPreis(Double.parseDouble(artikelPreisFeld.getText()));
                    }
                    if (!artikelBestandFeld.getText().equals("")) {
                        artikel.setBestand(Integer.parseInt(artikelBestandFeld.getText()));
                    }
                    if (!packGroesseFeld.getText().equals("")) {
                        ((Massenartikel) artikel).setPackgroesse(Integer.parseInt(packGroesseFeld.getText()));
                    }
                } else {
                    if (!artikelBezeichnungFeld.getText().equals("")) {
                        artikel.setBezeichnung(artikelBezeichnungFeld.getText());
                    }
                    if (!artikelPreisFeld.getText().equals("")) {
                        artikel.setPreis(Double.parseDouble(artikelPreisFeld.getText()));
                    }
                    if (!artikelBestandFeld.getText().equals("")) {
                        artikel.setBestand(Integer.parseInt(artikelBestandFeld.getText()));
                    }
                }
                shopAPI.artikelAktualisieren(artikel);
                setArtikelInTable();
                clearFelder();
            } else {
                editArtikelButton.setStyle("-fx-border-color: red;");
            }
        } catch (IOException | ArtikelNichtGefundenException e) {
            editArtikelButton.setStyle("-fx-border-color: red;");
        }
    }

    private void clearFelder() {
        artikelBestandFeld.setText("");
        artikelPreisFeld.setText("");
        artikelBezeichnungFeld.setText("");
        packGroesseFeld.setText("");
    }
}
package com.centerio.eshopfx.shop.ui.gui.controller;

import com.centerio.eshopfx.shop.domain.ArtikelService;
import com.centerio.eshopfx.shop.domain.HistorienService;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.w3c.dom.Text;


import java.io.IOException;
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
    TableColumn<Artikel, Integer> artikelEreignisNummerColumn;
    @FXML
    TableColumn<Artikel, String> artikelEreignisBezeichnungColumn;
    @FXML
    TableColumn<Artikel, Double> artikelEreignisPreisColumn;
    @FXML
    TableColumn<Artikel, Integer> artikelEreignisBestandColumn;
    @FXML
    TableColumn<Artikel, Integer> artikelEreignisPackgroesseColumn;
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
    private TableView<Artikel> artikelEreignisTableView;
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
        initializeEreignisView();
        setArtikelInTable();
        setPersonInTable();
        setEreingisInTable();
        artikelOnClickToTextfield();
        massenArtikelHandler();
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
        massenArtikelCheckbox.setOnAction(observable -> {
            var isSelected = massenArtikelCheckbox.isSelected();
            if (isSelected) {
                packGroesseFeld.setVisible(true);
            } else {
                packGroesseFeld.setVisible(false);
                packGroesseFeld.setText("");
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

    public void artikelOnClickToTextfield(){
        artikelTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1){
                TablePosition<?, ?> position = artikelTableView.getSelectionModel().getSelectedCells().get(0);
                int row = position.getRow();
                ArrayList<TextField> TextFieldList = new ArrayList<TextField>();
                TextFieldList.add(artikelBezeichnungFeld);
                TextFieldList.add(artikelPreisFeld);
                TextFieldList.add(artikelBestandFeld);
                for (int i = 0; i<3; i++){
                    TableColumn<?, ?> column = artikelTableView.getColumns().get(i+1);
                    Object value = column.getCellObservableValue(row).getValue();
                    TextFieldList.get(i).setText(value.toString());
                }


            }
        });
    }
    public void initializeEreignisView(){
       /* ereignisPersNrTableColumn = new TableColumn("PersNr");
        ereignisPersNameTableColumn = new TableColumn("Name");
        ereignisArtTableColumn = new TableColumn("Ereignisart");
        ereignisDatumTableColumn = new TableColumn("Datum");
        ereignisTableView.getColumns().addAll(ereignisPersNrTableColumn, ereignisPersNameTableColumn, ereignisArtTableColumn, ereignisDatumTableColumn);
        ereignisPersNrTableColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getPerson().getPersNr()).asObject());
        ereignisPersNameTableColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getPerson().getName()));
        ereignisArtTableColumn.setCellValueFactory((p -> new SimpleStringProperty(p.getValue().getEreignisTyp().toString())));
        ereignisDatumTableColumn.setCellValueFactory((p -> new SimpleStringProperty(p.getValue().getDatum().toString()))); */

        ereignisPersNrTableColumn = new TableColumn("PersNr");
        ereignisPersNameTableColumn = new TableColumn("Name");
        ereignisArtTableColumn = new TableColumn("Ereignisart");
        ereignisObjektTableColumn = new TableColumn("Bezug");
        ereignisBestandTableColumn = new TableColumn("Bestand");
        ereignisDatumTableColumn = new TableColumn("Datum");
        ereignisTableView.getColumns().addAll(ereignisPersNrTableColumn, ereignisPersNameTableColumn, ereignisArtTableColumn, ereignisObjektTableColumn,  ereignisBestandTableColumn,  ereignisDatumTableColumn);
        ereignisPersNrTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, Integer>("persNr"));
        ereignisPersNameTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, String>("persName"));
        ereignisArtTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, String>("art"));
        ereignisObjektTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, String>("bezug"));
        ereignisBestandTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, String>("bestandString"));
        ereignisDatumTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, String>("datumString"));

        dropDownEreignisse.getItems().addAll(
                "Alle Ereignisse",
                "Artikelereignisse",
                "Personenereignisse",
                "Warenkorbereignisse",
                "Bestandshistorie"
        );
        dropDownEreignisse.setOnAction(event -> {
            String selectedOption = dropDownEreignisse.getValue();
            switch(selectedOption){
                case "Alle Ereignisse" -> {
                    resetBestandshistorieButtonHandler();
                    ereignisTableView.setOnMouseClicked(null);
                    ObservableList<Ereignis> ereignisObservableList = FXCollections.observableArrayList();
                    try {
                        for (Ereignis ereignis : HistorienService.getInstance().getEreignisList()){
                            ereignisObservableList.add(ereignis);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    ereignisTableView.setItems(ereignisObservableList);
                }
                case "Artikelereignisse" -> {
                    resetBestandshistorieButtonHandler();
                    ereignisTableView.setOnMouseClicked(null);
                    ObservableList<Ereignis> ereignisObservableList = FXCollections.observableArrayList();
                    try {
                        for (Ereignis ereignis : ShopAPI.getInstance().getUngefilterteArtikelhistorie()){
                            ereignisObservableList.add(ereignis);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    ereignisTableView.setItems(ereignisObservableList);
                }
                case "Personenereignisse" -> {
                    resetBestandshistorieButtonHandler();
                    ereignisTableView.setOnMouseClicked(null);
                    ObservableList<Ereignis> ereignisObservableList = FXCollections.observableArrayList();
                    try {
                        for (Ereignis ereignis : ShopAPI.getInstance().getUngefiltertePersonenhistorie()){
                            ereignisObservableList.add(ereignis);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    ereignisTableView.setItems(ereignisObservableList);
                }
                case "Warenkorbereignisse" -> {
                    resetBestandshistorieButtonHandler();
                    ereignisTableView.setOnMouseClicked(null);
                    ObservableList<Ereignis> ereignisObservableList = FXCollections.observableArrayList();
                    try {
                        for (Ereignis ereignis : ShopAPI.getInstance().getUngefilterteWarenkorbhistorie()){
                            ereignisObservableList.add(ereignis);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    ereignisTableView.setItems(ereignisObservableList);
                }
                case "Bestandshistorie" -> {
                    TableColumn<Artikel, Integer> ereignisArtikelNummerColumn = new TableColumn<>("Nummer");
                    TableColumn<Artikel, String> ereignisArtikelBezeichnungColumn = new TableColumn<>("Bezeichnung");
                    TableColumn<Artikel, Double> ereignisArtikelPreisColumn = new TableColumn<>("Preis");
                    TableColumn<Artikel, Integer> ereignisArtikelBestandColumn = new TableColumn<>("Bestand");
                    TableColumn<Artikel, Integer> ereignisArtikelPackgroesseColumn = new TableColumn<>("Packgröße");
                    ereignisTableView.getItems().clear();
                    ereignisTableView.getColumns().clear();
                    ereignisTableView.getColumns().addAll(ereignisArtikelNummerColumn, ereignisArtikelBezeichnungColumn, ereignisArtikelPreisColumn, ereignisArtikelBestandColumn, ereignisArtikelPackgroesseColumn);
                    ereignisArtikelNummerColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getArtNr()).asObject());
                    ereignisArtikelBezeichnungColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getBezeichnung()));
                    ereignisArtikelPreisColumn.setCellValueFactory(p -> new SimpleDoubleProperty(p.getValue().getPreis()).asObject());
                    ereignisArtikelBestandColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getBestand()).asObject());
                    ereignisArtikelPackgroesseColumn.setCellValueFactory(p -> {if (p.getValue() instanceof Massenartikel){
                        return new SimpleIntegerProperty(((Massenartikel)p.getValue()).getPackgroesse()).asObject();
                    }
                        return null;
                    });
                    ObservableList<Artikel> artikelObservableList = FXCollections.observableArrayList();
                    try {
                        for(Artikel artikel : ArtikelService.getInstance().getArtikelList()){
                            artikelObservableList.add(artikel);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    ereignisTableView.setItems(artikelObservableList);
                    bestandsHistorieSuchenButtonHandler();
                    //bestandshistorieSuchenButton.setVisible(true);
                }
            }
        });
    }

    public void resetBestandshistorieButtonHandler(){
      /*bestandshistorieLabel.setVisible(false);
        bestandshistorieTextField.setVisible(false);
        tageZurueckLabel.setVisible(false);
        tageZurueckTextField.setVisible(false);*/
        ereignisTableView.getItems().clear();
        ereignisTableView.getColumns().clear();
        ereignisTableView.getColumns().addAll(ereignisPersNrTableColumn, ereignisPersNameTableColumn, ereignisArtTableColumn, ereignisObjektTableColumn,  ereignisBestandTableColumn,  ereignisDatumTableColumn);
        ereignisPersNrTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, Integer>("persNr"));
        ereignisPersNameTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, String>("persName"));
        ereignisArtTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, String>("art"));
        ereignisObjektTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, String>("bezug"));
        ereignisBestandTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, String>("bestandString"));
        ereignisDatumTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, String>("datumString"));
        bestandshistorieSuchenButton.setVisible(false);



    }
    public void bestandsHistorieSuchenButtonHandler(){
        try {
            ereignisTableView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2){
                    int clickedRowIndex = ereignisTableView.getSelectionModel().getSelectedIndex();
                    Artikel rowData = ((Artikel)ereignisTableView.getItems().get(clickedRowIndex));
                    String firstColumnValue = "";
                    if(rowData != null){
                        TableColumn<Artikel, ?> firstColumn = (TableColumn<Artikel, ?>) ereignisTableView.getColumns().get(0);
                        if (firstColumn != null){
                            firstColumnValue = firstColumn.getCellData(rowData).toString();
                            try {
                                resetBestandshistorieButtonHandler();
                                ObservableList<Ereignis> ereignisObservableList = FXCollections.observableArrayList();
                                for(Ereignis ereignis : ShopAPI.getInstance().sucheBestandshistorie(Integer.parseInt(firstColumnValue), 0, false)){
                                    ereignisObservableList.add(ereignis);
                                }
                                ereignisTableView.setItems(ereignisObservableList);
                            } catch (ArtikelNichtGefundenException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }

            });



        } catch (Exception e) {
            bestandshistorieSuchenButton.setStyle("-fx-border-color: red;");
        }

    }
    public void setEreingisInTable() throws IOException {
        ereignisTableView.getItems().clear();
        ObservableList<Ereignis> ereignisObservableList = FXCollections.observableArrayList();
        for (Ereignis ereignis : HistorienService.getInstance().getEreignisList()){
            ereignisObservableList.add(ereignis);
        }
        ereignisTableView.setItems(ereignisObservableList);
    }

    public void setArtikelInTable() throws IOException {
        artikelTableView.getItems().clear();
        ObservableList<Artikel> artikelObservableList = FXCollections.observableArrayList();
        for(Artikel artikel : ArtikelService.getInstance().getArtikelList()){
            artikelObservableList.add(artikel);
        }
        artikelTableView.setItems(artikelObservableList);
        setEreingisInTable();
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
    public void mitarbeiterRegistrieren() throws PersonVorhandenException, IOException {
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

    private void setPersonInTable() throws IOException {
        personenTableView.getItems().clear();
        ObservableList<Person> personObservableList = FXCollections.observableArrayList();
        personObservableList.addAll(shopAPI.getPersonList());
        personenTableView.setItems(personObservableList);
        setEreingisInTable();
    }

    public void editArtikel() {
        int selectedId = artikelTableView.getSelectionModel().getSelectedIndex();
        try {
            if(selectedId >= 0) {
                Artikel artikel = artikelTableView.getItems().get(selectedId);
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
            System.out.println(e);
        }
    }

    private void clearFelder() {
        artikelBestandFeld.setText("");
        artikelPreisFeld.setText("");
        artikelBezeichnungFeld.setText("");
        packGroesseFeld.setText("");
    }

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

    public void deleteSelectedAccount() {

    }

    public void clearSuchField() {
        suchField.setText("");
        try {
            setArtikelInTable();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void clearSuchFieldKey(KeyEvent e) {
        if (e.getCode().equals(KeyCode.ESCAPE)) {
            clearSuchField();
        }
    }

    public void artikelSuchen() {
        try {
            if (!suchField.getText().equals("")) {
                artikelTableView.getItems().clear();
                ObservableList<Artikel> artikelObservableList = FXCollections.observableArrayList();
                artikelObservableList.addAll(shopAPI.getArtikelByQuery(suchField.getText()));
                artikelTableView.setItems(artikelObservableList);
            }else {
                setArtikelInTable();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

//    public void suchenKey(KeyEvent e) {
//        if (e.getCode().equals(KeyCode.ENTER)) {
//            artikelSuchen();
//        }
//    }
}

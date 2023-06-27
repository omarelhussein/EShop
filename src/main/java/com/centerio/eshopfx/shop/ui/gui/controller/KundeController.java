package com.centerio.eshopfx.shop.ui.gui.controller;

import com.centerio.eshopfx.shop.domain.ArtikelService;
import com.centerio.eshopfx.shop.domain.HistorienService;
import com.centerio.eshopfx.shop.domain.ShopAPI;
import com.centerio.eshopfx.shop.domain.WarenkorbService;
import com.centerio.eshopfx.shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.entities.*;
import com.centerio.eshopfx.shop.entities.enums.EreignisTyp;
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

    private final ShopAPI shopAPI = ShopAPI.getInstance();

    /**
     * Diese Methode wird aufgerufen, wenn die View geladen wird.
     * Sie wird nach dem Konstruktor aufgerufen.
     * <p>
     * Hier können UI-Elemente initialisiert werden. In dem Konstruktor können UI-Elemente noch nicht initialisiert werden,
     * da diese noch nicht geladen wurden.
     */
    public void initialize() {
        try {
            initializeWarenkorbView();
            setWarenkorbInTable();
            initializeArtikelView();
            setArtikelInTable();
        } catch(IOException e) {
        }

    }

    public void save() {
        shopAPI.speichern();
    }

    public void initializeWarenkorbView() {
        warenkorbArtikelStringTableColumn = new TableColumn<WarenkorbArtikel, String>("Artikel");
        warenkorbArtikelAnzahlTableColumn = new TableColumn<WarenkorbArtikel, Integer>("Anzahl");
        warenkorbArtikelPreisTableColumn = new TableColumn<WarenkorbArtikel, Double>("Einzelpreis");
        warenkorbTableView.getColumns().addAll(warenkorbArtikelStringTableColumn, warenkorbArtikelAnzahlTableColumn, warenkorbArtikelPreisTableColumn);
        warenkorbArtikelStringTableColumn.setCellValueFactory(new PropertyValueFactory<WarenkorbArtikel, String>("artikelbezeichnung"));
        warenkorbArtikelPreisTableColumn.setCellValueFactory(new PropertyValueFactory<WarenkorbArtikel, Double>("einzelpreis"));
        warenkorbArtikelAnzahlTableColumn.setCellValueFactory(new PropertyValueFactory<WarenkorbArtikel, Integer>("anzahl"));
        initializeGesamtPreis();
    }

    public void initializeGesamtPreis(){
        Warenkorb warenkorb = ShopAPI.getInstance().getWarenkorb();
        gesamtPreis.setText("Gesamtpreis: " + warenkorb.getGesamtSumme());
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
        initializeGesamtPreis();
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
        for (Artikel artikel : ArtikelService.getInstance().getArtikelList()) {
            if (artikel.getBestand()!=0) {
                artikelObservableList.add(artikel);
            }
        }
        artikelTableView.setItems(artikelObservableList);
    }

    public void logout() throws IOException {
        shopAPI.logout();
        StageManager.getInstance().switchScene(SceneRoutes.LOGIN_VIEW);
    }

    public void toWarenkorb() {
        try {
            int selectedId = artikelTableView.getSelectionModel().getSelectedIndex();
            if (selectedId >= 0) {
                Artikel artikel = artikelTableView.getItems().get(selectedId);
                try {
                    if(artikelAnzahlField.getText().isEmpty()){
                        ShopAPI.getInstance().addArtikelToWarenkorb(artikel.getArtNr(), 1);
                        setWarenkorbInTable();
                        initializeGesamtPreis();
                    } else {
                        ShopAPI.getInstance().addArtikelToWarenkorb(artikel.getArtNr(), Integer.parseInt(artikelAnzahlField.getText()));
                        setWarenkorbInTable();
                        initializeGesamtPreis();
                    }
                } catch (NumberFormatException e) {
                    addToWarenkorbButton.setStyle("-fx-border-color: red;");
                }
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

    public void rechnungErstellen() {

        try {
            if (WarenkorbService.getInstance().getWarenkorb().getAnzahlArtikel() == 0) {
                return;
            }
        } catch (IOException e) {
            return;
        }


        addToWarenkorbButton.setVisible(false);
        kaufenButton.setVisible(false);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(shopAPI.erstelleRechnung().toString());
        alert.setTitle("Rechnung");
        alert.setHeaderText("Bitte Bestätigen sie ihren Einkauf.");

        // Hinzufügen der Buttons
        ButtonType buttonTypeConfirm = new ButtonType("Bestätigen");
        ButtonType buttonTypeCancel = new ButtonType("Abbrechen");

        alert.getButtonTypes().setAll(buttonTypeConfirm, buttonTypeCancel);



        // Event Handler für den Bestätigen-Button
        alert.setOnCloseRequest(dialogEvent -> {
            if (alert.getResult() == buttonTypeConfirm) {
                try {
                    kaufeWarenkorb();
                } catch (BestandUeberschrittenException e) {
                    throw new RuntimeException(e);
                } catch (ArtikelNichtGefundenException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                addToWarenkorbButton.setVisible(true);
                kaufenButton.setVisible(true);
                alert.close(); // Popup-Fenster schließen
            }
        });

        // Event Handler für den Abbrechen-Button
        alert.setOnHidden(dialogEvent -> {
            if (alert.getResult() == buttonTypeCancel) {
                addToWarenkorbButton.setVisible(true);
                kaufenButton.setVisible(true);
                alert.close(); // Popup-Fenster schließen
            }
        });

        alert.showAndWait();
    }

    public void warenkorbEntfernen() {
        try {
            int selectedId = warenkorbTableView.getSelectionModel().getSelectedIndex();
            if (selectedId >= 0) {
                if(warenkorbAnzahlField.getText().isEmpty()) {
                    Artikel artikel = ((WarenkorbArtikel)warenkorbTableView.getItems().get(selectedId)).getArtikel();
                    ShopAPI.getInstance().aendereArtikelAnzahlImWarenkorb(artikel.getArtNr(), 0);
                    setWarenkorbInTable();
                } else {
                    Artikel artikel = ((WarenkorbArtikel)warenkorbTableView.getItems().get(selectedId)).getArtikel();
                    ShopAPI.getInstance().entferneArtikelAnzahlImWarenkorb(artikel.getArtNr(), Integer.parseInt(warenkorbAnzahlField.getText()));
                    setWarenkorbInTable();
                }
            } else {
                warenkorbEntfernenButton.setStyle("-fx-border-color: red;");
            }
        } catch (BestandUeberschrittenException | IOException | WarenkorbArtikelNichtGefundenException | ArtikelNichtGefundenException e) {
            addToWarenkorbButton.setStyle("-fx-border-color: red;");
            throw new RuntimeException(e);
        }
    }

    public void clearSuchField() {
        suchField.setText("");
        try {
            setArtikelInTable();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

    public void suchenKey(KeyEvent e) {
        if (e.getCode().equals(KeyCode.ENTER)) {
            artikelSuchen();
        }
    }
}

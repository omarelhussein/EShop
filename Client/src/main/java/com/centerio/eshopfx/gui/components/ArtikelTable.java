package com.centerio.eshopfx.gui.components;

import com.centerio.eshopfx.ShopAPIClient;
import domain.ShopAPI;
import domain.ShopEventListener;
import entities.Artikel;
import entities.Massenartikel;
import exceptions.artikel.AnzahlPackgroesseException;
import exceptions.artikel.ArtikelNichtGefundenException;
import exceptions.warenkorb.BestandUeberschrittenException;
import exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Hier wird die Tabelle für die Artikel erstellt.
 * Die Tabelle muss von UnicastRemoteObject erben, um die beim Server registrieren zu können.
 */
public class ArtikelTable extends UnicastRemoteObject implements ShopEventListener {

    private TableColumn<Artikel, Integer> artikelNummerColumn;
    private TableColumn<Artikel, String> artikelBezeichnungColumn;
    private TableColumn<Artikel, Double> artikelPreisColumn;
    private TableColumn<Artikel, Integer> artikelBestandColumn;
    private TableColumn<Artikel, Integer> artikelPackgroesseColumn;
    private TableView<Artikel> artikelTableView;
    private TextField suchField;
    private TextField artikelAnzahlField;
    private Button addToWarenkorbButton;
    private TextField artikelBezeichnungFeld;
    private TextField artikelPreisFeld;
    private CheckBox massenArtikelCheckbox;
    private TextField packGroesseFeld;
    private TextField artikelBestandFeld;
    private Button addArtikelButton;
    private Button editArtikelButton;
    private Button removeArtikelButton;

    private Button clearButton;

    private final ShopAPI shopAPI = ShopAPIClient.getShopAPI();

    public ArtikelTable(
            TableColumn<Artikel, Integer> artikelNummerColumn,
            TableColumn<Artikel, String> artikelBezeichnungColumn,
            TableColumn<Artikel, Double> artikelPreisColumn,
            TableColumn<Artikel, Integer> artikelBestandColumn,
            TableColumn<Artikel, Integer> artikelPackgroesseColumn,
            TableView<Artikel> artikelTableView,
            TextField suchField,
            TextField artikelAnzahlField,
            Button addToWarenkorbButton,
            Button clearButton
    ) throws RemoteException, NotBoundException {
        this.artikelNummerColumn = artikelNummerColumn;
        this.artikelBezeichnungColumn = artikelBezeichnungColumn;
        this.artikelPreisColumn = artikelPreisColumn;
        this.artikelBestandColumn = artikelBestandColumn;
        this.artikelPackgroesseColumn = artikelPackgroesseColumn;
        this.artikelTableView = artikelTableView;
        this.suchField = suchField;
        this.artikelAnzahlField = artikelAnzahlField;
        this.addToWarenkorbButton = addToWarenkorbButton;
        this.clearButton = clearButton;
        shopAPI.addShopEventListener(this);
    }

    public ArtikelTable(TableColumn<Artikel, Integer> artikelNummerColumn,
                        TableColumn<Artikel, String> artikelBezeichnungColumn,
                        TableColumn<Artikel, Double> artikelPreisColumn,
                        TableColumn<Artikel, Integer> artikelBestandColumn,
                        TableColumn<Artikel, Integer> artikelPackgroesseColumn,
                        TableView<Artikel> artikelTableView,
                        TextField suchField,
                        TextField artikelBezeichnungFeld,
                        TextField artikelPreisFeld,
                        CheckBox massenArtikelCheckbox,
                        TextField packGroesseFeld,
                        TextField artikelBestandFeld,
                        Button addArtikelButton,
                        Button editArtikelButton,
                        Button removeArtikelButton,
                        Button clearButton
    ) throws RemoteException {
        this.artikelNummerColumn = artikelNummerColumn;
        this.artikelBezeichnungColumn = artikelBezeichnungColumn;
        this.artikelPreisColumn = artikelPreisColumn;
        this.artikelBestandColumn = artikelBestandColumn;
        this.artikelPackgroesseColumn = artikelPackgroesseColumn;
        this.artikelTableView = artikelTableView;
        this.suchField = suchField;
        this.artikelBezeichnungFeld = artikelBezeichnungFeld;
        this.artikelPreisFeld = artikelPreisFeld;
        this.massenArtikelCheckbox = massenArtikelCheckbox;
        this.packGroesseFeld = packGroesseFeld;
        this.artikelBestandFeld = artikelBestandFeld;
        this.addArtikelButton = addArtikelButton;
        this.editArtikelButton = editArtikelButton;
        this.removeArtikelButton = removeArtikelButton;
        this.clearButton = clearButton;
        shopAPI.addShopEventListener(this);
    }

    public void initializeArtikelView() {
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
        artikelPackgroesseColumn.setCellValueFactory(p -> {
            if (p.getValue() instanceof Massenartikel) {
                return new SimpleIntegerProperty(((Massenartikel) p.getValue()).getPackgroesse()).asObject();
            }
            return null;
        });
    }

    public void setMitarbeiterEventHandlersForArtikel() {
        addArtikelButton.setOnAction(e -> {
            try {
                artikelAdd();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        editArtikelButton.setOnAction(e -> {
            editArtikel();
        });
        removeArtikelButton.setOnAction(e -> {
            try {
                removeArtikel();
            } catch (IOException | ArtikelNichtGefundenException ex) {
                throw new RuntimeException(ex);
            }
        });
        suchField.setOnKeyPressed(this::clearSuchFieldKey);
        suchField.setOnKeyTyped(e -> artikelSuchen());
        clearButton.setOnAction(e -> clearSuchField());
    }

    public void refreshTable() throws IOException {
        artikelTableView.getItems().clear();
        ObservableList<Artikel> artikelObservableList = FXCollections.observableArrayList(shopAPI.getArtikelList().stream().filter(a -> a.getBestand() != 0).toList());
        artikelTableView.setItems(artikelObservableList);
    }

    public void artikelSuchen() {
        try {
            if (!suchField.getText().isEmpty()) {
                artikelTableView.getItems().clear();
                ObservableList<Artikel> artikelObservableList = FXCollections.observableArrayList();
                artikelObservableList.addAll(shopAPI.getArtikelByQuery(suchField.getText()));
                artikelTableView.setItems(artikelObservableList);
            } else {
                refreshTable();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Kundenfunktionen
     */
    public void toWarenkorb(WarenkorbTable warenkorbTable) {
        try {
            int selectedId = artikelTableView.getSelectionModel().getSelectedIndex();
            if (selectedId >= 0) {
                Artikel artikel = artikelTableView.getItems().get(selectedId);
                try {
                    int anzahl = 1;
                    if (artikelAnzahlField.getText().isEmpty()) {
                        if (artikel instanceof Massenartikel) anzahl = ((Massenartikel) artikel).getPackgroesse();
                    } else {
                        anzahl = Integer.parseInt(artikelAnzahlField.getText());
                    }
                    shopAPI.addArtikelToWarenkorb(artikel.getArtNr(), anzahl);
                    warenkorbTable.setWarenkorbInTable();
                    warenkorbTable.initializeGesamtPreis();
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Bitte geben Sie eine Zahl ein!").showAndWait();
                } catch (AnzahlPackgroesseException | WarenkorbArtikelNichtGefundenException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Bitte wählen Sie einen Artikel aus!").showAndWait();
            }
        } catch (IOException | ArtikelNichtGefundenException | BestandUeberschrittenException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    public void setKundeEventHandlerForArtikel(WarenkorbTable warenkorbTable) {
        suchField.setOnKeyPressed(e -> clearSuchFieldKey(e));
        suchField.setOnKeyTyped(e -> {
            artikelSuchen();
        });
        clearButton.setOnAction(e -> {
            clearSuchField();
        });
        addToWarenkorbButton.setOnAction(e -> {
            toWarenkorb(warenkorbTable);
        });
    }

    /**
     * Mitarbeiterfunktionen
     */
    public void handleRowClicked() {
        artikelTableView.setOnMouseClicked(event -> {
            TablePosition<?, ?> position = artikelTableView.getSelectionModel().getSelectedCells().get(0);
            int row = position.getRow();
            Artikel artikel = artikelTableView.getItems().get(row);
            artikelBezeichnungFeld.setText(artikel.getBezeichnung());
            artikelPreisFeld.setText(String.valueOf(artikel.getPreis()));
            artikelBestandFeld.setText(String.valueOf(artikel.getBestand()));
            if (artikel instanceof Massenartikel) {
                massenArtikelCheckbox.setSelected(true);
                packGroesseFeld.setText(String.valueOf(((Massenartikel) artikel).getPackgroesse()));
                packGroesseFeld.setVisible(true);
            } else {
                massenArtikelCheckbox.setSelected(false);
                packGroesseFeld.setVisible(false);
            }
        });
    }

    public void massenArtikelHandler() {
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

    public void removeArtikel() throws IOException, ArtikelNichtGefundenException {
        int selectedId = artikelTableView.getSelectionModel().getSelectedIndex();
        shopAPI.removeArtikel(artikelTableView.getItems().get(selectedId).getArtNr());
        artikelTableView.getItems().remove(selectedId);
    }

    public void artikelAdd() throws IOException {
        resetArtikelAddStyles();
        if (isEmpty(artikelBezeichnungFeld)) {
            return;
        }
        if (isEmpty(artikelPreisFeld)) {
            return;
        }
        if (isEmpty(artikelBestandFeld)) {
            return;
        }
        try {
            if (massenArtikelCheckbox.isSelected()) {
                Massenartikel artikel = new Massenartikel(shopAPI.getNaechsteArtikelId(), artikelBezeichnungFeld.getText(),
                        Double.parseDouble(artikelPreisFeld.getText()), Integer.parseInt(artikelBestandFeld.getText()), Integer.parseInt(packGroesseFeld.getText()));
                shopAPI.addArtikel(artikel);
            } else {
                Artikel artikel = new Artikel(shopAPI.getNaechsteArtikelId(), artikelBezeichnungFeld.getText(),
                        (Double.parseDouble(artikelPreisFeld.getText())), Integer.parseInt(artikelBestandFeld.getText()));
                shopAPI.addArtikel(artikel);
            }
            refreshTable();
            clearFelder();
        } catch (RuntimeException e) {
            addArtikelButton.setStyle("-fx-border-color: red;");
        }
    }

    public void resetArtikelAddStyles() {
        artikelBezeichnungFeld.setStyle("");
        artikelPreisFeld.setStyle("");
        artikelBestandFeld.setStyle("");
    }

    private void clearFelder() {
        artikelBestandFeld.setText("");
        artikelPreisFeld.setText("");
        artikelBezeichnungFeld.setText("");
        packGroesseFeld.setText("");
    }

    private boolean isEmpty(TextField field) {
        if (field.getText().isEmpty()) {
            field.setStyle("-fx-border-color: red;");
            return true;
        }
        field.setStyle("");
        return false;
    }

    public void clearSuchField() {
        suchField.setText("");
        try {
            refreshTable();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void clearSuchFieldKey(KeyEvent e) {
        if (e.getCode().equals(KeyCode.ESCAPE)) {
            clearSuchField();
        }
    }

    public void editArtikel() {
        int selectedId = artikelTableView.getSelectionModel().getSelectedIndex();
        try {
            if (selectedId >= 0) {
                Artikel artikel = artikelTableView.getItems().get(selectedId);
                Artikel newartikel;
                if (artikel instanceof Massenartikel) {
                    newartikel = new Massenartikel(artikel.getArtNr(), artikelBezeichnungFeld.getText(), Double.parseDouble(artikelPreisFeld.getText()),
                            Integer.parseInt(artikelBestandFeld.getText()), Integer.parseInt(packGroesseFeld.getText()));
                } else {
                    newartikel = new Artikel(artikel.getArtNr(), artikelBezeichnungFeld.getText(), Double.parseDouble(artikelPreisFeld.getText()),
                            Integer.parseInt(artikelBestandFeld.getText()));
                }
                shopAPI.artikelAktualisieren(newartikel);
                refreshTable();
                clearFelder();
            } else {
                editArtikelButton.setStyle("-fx-border-color: red;");
            }
        } catch (IOException | ArtikelNichtGefundenException e) {
            editArtikelButton.setStyle("-fx-border-color: red;");
            e.printStackTrace();
        }
    }

    @Override
    public void handleArtikelListChanged() throws RemoteException {
        // put refreshTable() in Platform.runLater() to avoid IllegalStateException
        // In this case it is necessary because the method is called from another thread
        Platform.runLater(() -> {
            try {
                refreshTable();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

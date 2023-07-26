package com.centerio.eshopfx.gui.components;

import com.centerio.eshopfx.ShopAPIClient;
import domain.ShopAPI;
import entities.Artikel;
import entities.UserContext;
import entities.Warenkorb;
import entities.WarenkorbArtikel;
import exceptions.artikel.AnzahlPackgroesseException;
import exceptions.artikel.ArtikelNichtGefundenException;
import exceptions.warenkorb.BestandUeberschrittenException;
import exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class WarenkorbTable {

    private final TableView warenkorbTableView;

    private TableColumn<WarenkorbArtikel, String> warenkorbArtikelStringTableColumn;
    private TableColumn<WarenkorbArtikel, Integer> warenkorbArtikelAnzahlTableColumn;
    private TableColumn<WarenkorbArtikel, Double> warenkorbArtikelPreisTableColumn;
    private Button warenkorbEntfernenButton;
    private TextField warenkorbAnzahlField;
    private Button kaufenButton;
    private Label gesamtPreis;

    public WarenkorbTable(TableColumn<WarenkorbArtikel, String> warenkorbArtikelStringTableColumn,
                          TableColumn<WarenkorbArtikel, Integer> warenkorbArtikelAnzahlTableColumn,
                          TableColumn<WarenkorbArtikel, Double> warenkorbArtikelPreisTableColumn,
                          TableView warenkorbTableView,
                          Button warenkorbEntfernenButton,
                          TextField warenkorbAnzahlField,
                          Button kaufenButton,
                          Label gesamtPreis) throws RemoteException, NotBoundException {
        this.warenkorbArtikelStringTableColumn = warenkorbArtikelStringTableColumn;
        this.warenkorbArtikelAnzahlTableColumn = warenkorbArtikelAnzahlTableColumn;
        this.warenkorbArtikelPreisTableColumn = warenkorbArtikelPreisTableColumn;
        this.warenkorbTableView = warenkorbTableView;
        this.warenkorbEntfernenButton = warenkorbEntfernenButton;
        this.warenkorbAnzahlField = warenkorbAnzahlField;
        this.kaufenButton = kaufenButton;
        this.gesamtPreis = gesamtPreis;
    }

    public void initializeWarenkorbView() throws RemoteException {
        warenkorbArtikelStringTableColumn = new TableColumn<WarenkorbArtikel, String>("Artikel");
        warenkorbArtikelAnzahlTableColumn = new TableColumn<WarenkorbArtikel, Integer>("Anzahl");
        warenkorbArtikelPreisTableColumn = new TableColumn<WarenkorbArtikel, Double>("Einzelpreis");
        warenkorbTableView.getColumns().addAll(warenkorbArtikelStringTableColumn, warenkorbArtikelAnzahlTableColumn, warenkorbArtikelPreisTableColumn);
        warenkorbArtikelStringTableColumn.setCellValueFactory(new PropertyValueFactory<WarenkorbArtikel, String>("artikelbezeichnung"));
        warenkorbArtikelPreisTableColumn.setCellValueFactory(new PropertyValueFactory<WarenkorbArtikel, Double>("einzelpreis"));
        warenkorbArtikelAnzahlTableColumn.setCellValueFactory(new PropertyValueFactory<WarenkorbArtikel, Integer>("anzahl"));
        initializeGesamtPreis();
    }

    /**
     * Eventhandler für die Warenkorbtabelle
     */
    public void setEventHandlerForWarenkorb() {
        warenkorbEntfernenButton.setOnAction(e -> {
            warenkorbEntfernen();
        });
        kaufenButton.setOnAction(e -> {
            try {
                rechnungErstellen();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void initializeGesamtPreis() throws RemoteException {
        Warenkorb warenkorb = ShopAPIClient.getShopAPI().getWarenkorb();
        gesamtPreis.setText("Gesamtpreis: " + warenkorb.getGesamtSumme());
    }

    public void kaufeWarenkorb() throws BestandUeberschrittenException, ArtikelNichtGefundenException, IOException {
        ShopAPIClient.getShopAPI().kaufen();
        warenkorbTableView.getItems().clear();
        setWarenkorbInTable();
    }

    public void setWarenkorbInTable() throws IOException {
        warenkorbTableView.getItems().clear();
        ObservableList<WarenkorbArtikel> warenkorbObservableList = FXCollections.observableArrayList();
        warenkorbObservableList.addAll(ShopAPIClient.getShopAPI().getWarenkorb().getWarenkorbArtikelList());
        warenkorbTableView.setItems(warenkorbObservableList);
        initializeGesamtPreis();
    }

    public void warenkorbEntfernen() {
        try {
            int selectedId = warenkorbTableView.getSelectionModel().getSelectedIndex();
            if (selectedId >= 0) {
                if (warenkorbAnzahlField.getText().isEmpty()) {
                    Artikel artikel = ((WarenkorbArtikel) warenkorbTableView.getItems().get(selectedId)).getArtikel();
                    ShopAPIClient.getShopAPI().aendereArtikelAnzahlImWarenkorb(artikel.getArtNr(), 0);
                    setWarenkorbInTable();
                } else {
                    Artikel artikel = ((WarenkorbArtikel) warenkorbTableView.getItems().get(selectedId)).getArtikel();
                    ShopAPIClient.getShopAPI().entferneArtikelAnzahlImWarenkorb(artikel.getArtNr(), Integer.parseInt(warenkorbAnzahlField.getText()));
                    setWarenkorbInTable();
                }
                warenkorbAnzahlField.clear();
            } else {
                new Alert(Alert.AlertType.ERROR, "Bitte wählen Sie einen Artikel aus.").showAndWait();
            }
        } catch (BestandUeberschrittenException | IOException | WarenkorbArtikelNichtGefundenException |
                 ArtikelNichtGefundenException | AnzahlPackgroesseException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    public void rechnungErstellen() throws RemoteException {

        if (ShopAPIClient.getShopAPI().getWarenkorb().getAnzahlArtikel() == 0) {
            new Alert(Alert.AlertType.ERROR, "Bitte fügen Sie Artikel zum Warenkorb hinzu.").showAndWait();
            return;
        }


        //addToWarenkorbButton.setVisible(false);
        kaufenButton.setVisible(false);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(ShopAPIClient.getShopAPI().erstelleRechnung().toString());
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
                } catch (BestandUeberschrittenException | ArtikelNichtGefundenException | IOException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                }
                //addToWarenkorbButton.setVisible(true);
                kaufenButton.setVisible(true);
                alert.close(); // Popup-Fenster schließen
            }
        });

        // Event Handler für den Abbrechen-Button
        alert.setOnHidden(dialogEvent -> {
            if (alert.getResult() == buttonTypeCancel) {
                //addToWarenkorbButton.setVisible(true);
                kaufenButton.setVisible(true);
                alert.close(); // Popup-Fenster schließen
            }
        });

        alert.showAndWait();
    }
}

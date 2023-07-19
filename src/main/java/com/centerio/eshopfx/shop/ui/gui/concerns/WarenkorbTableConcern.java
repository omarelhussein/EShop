package com.centerio.eshopfx.shop.ui.gui.concerns;

import com.centerio.eshopfx.shop.domain.RemoteInterface;
import com.centerio.eshopfx.shop.domain.ShopAPI;
import com.centerio.eshopfx.shop.domain.WarenkorbService;
import com.centerio.eshopfx.shop.domain.exceptions.artikel.AnzahlPackgroesseException;
import com.centerio.eshopfx.shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.entities.Artikel;
import com.centerio.eshopfx.shop.entities.Warenkorb;
import com.centerio.eshopfx.shop.entities.WarenkorbArtikel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class WarenkorbTableConcern {

    private TableView warenkorbTableView;

    private TableColumn<WarenkorbArtikel, String> warenkorbArtikelStringTableColumn;
    private TableColumn<WarenkorbArtikel, Integer> warenkorbArtikelAnzahlTableColumn;
    private TableColumn<WarenkorbArtikel, Double> warenkorbArtikelPreisTableColumn;
    private Button warenkorbEntfernenButton;
    private TextField warenkorbAnzahlField;
    private Button kaufenButton;
    private Label gesamtPreis;

    Registry registry = LocateRegistry.getRegistry("LocalHost", 1099);

    private final RemoteInterface shopAPI = (RemoteInterface) registry.lookup("RemoteObject");
    public WarenkorbTableConcern(TableColumn<WarenkorbArtikel, String> warenkorbArtikelStringTableColumn,
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

    public void setEventHandlerForWarenkorb(){
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
        Warenkorb warenkorb = shopAPI.getWarenkorb();
        gesamtPreis.setText("Gesamtpreis: " + warenkorb.getGesamtSumme());
    }

    public void kaufeWarenkorb() throws BestandUeberschrittenException, ArtikelNichtGefundenException, IOException {
        warenkorbTableView.getItems().clear();
        shopAPI.kaufen();
        setWarenkorbInTable();
    }
    public void setWarenkorbInTable() throws IOException {
        warenkorbTableView.getItems().clear();
        ObservableList<WarenkorbArtikel> warenkorbObservableList = FXCollections.observableArrayList();
        for (WarenkorbArtikel warenkorbartikel : shopAPI.getWarenkorb().getWarenkorbArtikelList()) {
            warenkorbObservableList.add(warenkorbartikel);
        }
        warenkorbTableView.setItems(warenkorbObservableList);
        initializeGesamtPreis();
    }

    public void warenkorbEntfernen() {
        try {
            int selectedId = warenkorbTableView.getSelectionModel().getSelectedIndex();
            if (selectedId >= 0) {
                if(warenkorbAnzahlField.getText().isEmpty()) {
                    Artikel artikel = ((WarenkorbArtikel)warenkorbTableView.getItems().get(selectedId)).getArtikel();
                    shopAPI.aendereArtikelAnzahlImWarenkorb(artikel.getArtNr(), 0);
                    setWarenkorbInTable();
                } else {
                    Artikel artikel = ((WarenkorbArtikel)warenkorbTableView.getItems().get(selectedId)).getArtikel();
                    shopAPI.entferneArtikelAnzahlImWarenkorb(artikel.getArtNr(), Integer.parseInt(warenkorbAnzahlField.getText()));
                    setWarenkorbInTable();
                }
            } else {
                warenkorbEntfernenButton.setStyle("-fx-border-color: red;");
            }
        } catch (BestandUeberschrittenException | IOException | WarenkorbArtikelNichtGefundenException |
                 ArtikelNichtGefundenException | AnzahlPackgroesseException e) {
            warenkorbEntfernenButton.setStyle("-fx-border-color: red;");
            System.out.println(e);
        }
    }
    public void rechnungErstellen() throws RemoteException {

        try {
            if (shopAPI.getWarenkorb().getAnzahlArtikel() == 0) {
                return;
            }
        } catch (IOException e) {
            System.out.println(e);
        }


        //addToWarenkorbButton.setVisible(false);
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

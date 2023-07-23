package com.centerio.eshopfx.gui.components;

import com.centerio.eshopfx.ShopAPIClient;
import domain.ShopAPI;
import entities.Artikel;
import entities.Ereignis;
import entities.Massenartikel;
import exceptions.artikel.ArtikelNichtGefundenException;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class EreignisTable {
    private ComboBox<String> dropDownEreignisse;
    private TableColumn<Ereignis, Integer> ereignisPersNrTableColumn;

    private TableColumn<Ereignis, String> ereignisPersNameTableColumn;

    private TableColumn<Ereignis, String> ereignisArtTableColumn;

    private TableColumn<Ereignis, String> ereignisObjektTableColumn;

    private TableColumn<Ereignis, String> ereignisDatumTableColumn;

    private TableColumn<Ereignis, String> ereignisBestandTableColumn;

    private TableView ereignisTableView;

    private Button bestandshistorieSuchenButton;

    private final ShopAPI shopAPI = ShopAPIClient.getShopAPI();

    public EreignisTable(TableColumn<Ereignis, Integer> ereignisPersNrTableColumn,
                         TableColumn<Ereignis, String> ereignisPersNameTableColumn,
                         TableColumn<Ereignis, String> ereignisArtTableColumn,
                         TableColumn<Ereignis, String> ereignisObjektTableColumn,
                         TableColumn<Ereignis, String> ereignisDatumTableColumn,
                         TableColumn<Ereignis, String> ereignisBestandTableColumn,
                         TableView ereignisTableView,
                         ComboBox<String> dropDownEreignisse,
                         Button bestandshistorieSuchenButton) throws NotBoundException, RemoteException {
        this.ereignisPersNrTableColumn = ereignisPersNrTableColumn;
        this.ereignisPersNameTableColumn = ereignisPersNameTableColumn;
        this.ereignisArtTableColumn = ereignisArtTableColumn;
        this.ereignisObjektTableColumn = ereignisObjektTableColumn;
        this.ereignisDatumTableColumn = ereignisDatumTableColumn;
        this.ereignisBestandTableColumn = ereignisBestandTableColumn;
        this.ereignisTableView = ereignisTableView;
        this.dropDownEreignisse = dropDownEreignisse;
        this.bestandshistorieSuchenButton = bestandshistorieSuchenButton;


    }

    public void initializeEreignisView(){
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
                        ereignisObservableList.addAll(shopAPI.getEreignisList());
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
                        ereignisObservableList.addAll(shopAPI.getUngefilterteArtikelhistorie());
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
                        ereignisObservableList.addAll(shopAPI.getUngefiltertePersonenhistorie());
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
                        ereignisObservableList.addAll(shopAPI.getUngefilterteWarenkorbhistorie());
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
                        artikelObservableList.addAll(shopAPI.getArtikelList());
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
        ereignisTableView.getItems().clear();
        ereignisTableView.getColumns().clear();
        ereignisTableView.getColumns().addAll(ereignisPersNrTableColumn, ereignisPersNameTableColumn, ereignisArtTableColumn, ereignisObjektTableColumn,  ereignisBestandTableColumn,  ereignisDatumTableColumn);
        ereignisPersNrTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, Integer>("persNr"));
        ereignisPersNameTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, String>("persName"));
        ereignisArtTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, String>("art"));
        ereignisObjektTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, String>("bezug"));
        ereignisBestandTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, String>("bestandString"));
        ereignisDatumTableColumn.setCellValueFactory(new PropertyValueFactory<Ereignis, String>("datumString"));



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
                                ereignisObservableList.addAll(shopAPI.sucheBestandshistorie(Integer.parseInt(firstColumnValue), 0, false));
                                ereignisTableView.setItems(ereignisObservableList);
                            } catch (ArtikelNichtGefundenException | IOException e) {
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
        ereignisObservableList.addAll(shopAPI.getEreignisList());
        ereignisTableView.setItems(ereignisObservableList);
    }
}

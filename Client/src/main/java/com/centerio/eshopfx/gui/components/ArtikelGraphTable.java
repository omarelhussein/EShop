package com.centerio.eshopfx.gui.components;

import domain.ShopEventListener;

import com.centerio.eshopfx.ShopAPIClient;
import domain.ShopAPI;
import domain.ShopEventListener;
import entities.Artikel;
import entities.Ereignis;
import entities.Massenartikel;
import entities.enums.EreignisTyp;
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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.List;

public class ArtikelGraphTable {

    private TableColumn<Artikel, Integer> artikelNummerColumn;
    private TableColumn<Artikel, String> artikelBezeichnungColumn;
    private TableView<Artikel> artikelGraphTableView;
    private LineChart<Number, Number> graph;

    private final ShopAPI shopAPI = ShopAPIClient.getShopAPI();


    public ArtikelGraphTable(TableColumn<Artikel, Integer> artikelNummerColumn,
                             TableColumn<Artikel, String> artikelBezeichnungColumn,
                             TableView<Artikel> artikelGraphTableView,
                             LineChart<Number, Number> graph) throws RemoteException {
        this.artikelNummerColumn = artikelNummerColumn;
        this.artikelBezeichnungColumn = artikelBezeichnungColumn;
        this.artikelGraphTableView = artikelGraphTableView;
        this.graph = graph;
    }

    public void initializeArtikelGraphView() {
        artikelNummerColumn = new TableColumn<Artikel, Integer>("Nummer");
        artikelBezeichnungColumn = new TableColumn<Artikel, String>("Bezeichnung");
        artikelGraphTableView.getColumns().addAll(artikelNummerColumn, artikelBezeichnungColumn);
        artikelNummerColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getArtNr()).asObject());
        artikelBezeichnungColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getBezeichnung()));
    }

    public void refreshTable() throws IOException {
        artikelGraphTableView.getItems().clear();
        ObservableList<Artikel> artikelObservableList = FXCollections.observableArrayList(shopAPI.getArtikelList());
        artikelGraphTableView.setItems(artikelObservableList);
    }

    public void artikelOnClickToGraph() {
        artikelGraphTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int selectedId = artikelGraphTableView.getSelectionModel().getSelectedIndex();
                Artikel artikel = artikelGraphTableView.getItems().get(selectedId);
                if(selectedId > -1) {
                    try {
                        List<Ereignis> ereignisse = shopAPI.sucheBestandshistorie(artikel.getArtNr(), 30, false);
                        var iterator = ereignisse.iterator();
                        int i = 30;
                        XYChart.Series series = new XYChart.Series();
                        while (iterator.hasNext()) {
                            LocalDate date = LocalDate.now();
                            Ereignis ereignis = iterator.next();
                            while (date.minus(i, ChronoUnit.DAYS).isBefore(ereignis.getDatum().toLocalDate())) {
                                if (i > 0) {
                                    if (ereignis.getEreignisTyp() == EreignisTyp.ARTIKEL_ANLEGEN) {
                                        series.getData().add(new XYChart.Data(i, 0));
                                    } else {
                                        series.getData().add(new XYChart.Data(i, ereignis.getBestand()));
                                    }
                                    i--;
                                }
                            }
                        }
                        while (i > 0) {
                            series.getData().add(new XYChart.Data(i, artikel.getBestand()));
                            i--;
                        }
                        series.getData().add(new XYChart.Data(0, artikel.getBestand()));
                        graph.getData().add(series);
                        graph.getXAxis().setLabel("days ago");
                        graph.getYAxis().setLabel("Bestand");
                    } catch (ArtikelNichtGefundenException | IOException e) {
                        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                    }
                }
            }
        });

    }

}

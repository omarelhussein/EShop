package com.centerio.eshopfx.gui.components;

import com.centerio.eshopfx.ShopAPIClient;
import domain.ShopAPI;
import entities.Artikel;
import entities.Ereignis;
import entities.enums.EreignisTyp;
import exceptions.artikel.ArtikelNichtGefundenException;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.io.IOException;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ArtikelGraphTable {

    private TableColumn<Artikel, Integer> artikelNummerColumn;
    private TableColumn<Artikel, String> artikelBezeichnungColumn;
    private TableView<Artikel> artikelGraphTableView;
    private LineChart<Number, Number> graph;
    private Map<Artikel, XYChart.Series<Number, Number>> seriesMap = new HashMap<>();
    private final Set<Artikel> selectedArtikelSet = new HashSet<>();
    private final Button clearGraphButton;


    public ArtikelGraphTable(TableColumn<Artikel, Integer> artikelNummerColumn,
                             TableColumn<Artikel, String> artikelBezeichnungColumn,
                             TableView<Artikel> artikelGraphTableView,
                             LineChart<Number, Number> graph, Button clearGraphButton) throws RemoteException {
        this.artikelNummerColumn = artikelNummerColumn;
        this.artikelBezeichnungColumn = artikelBezeichnungColumn;
        this.artikelGraphTableView = artikelGraphTableView;
        this.graph = graph;
        this.clearGraphButton = clearGraphButton;
        initializeArtikelGraphView();
    }

    public void initializeArtikelGraphView() {
        // Initialize and set cell value factory for artikelNummerColumn
        artikelNummerColumn = new TableColumn<>("ArtikelNr");
        artikelNummerColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getArtNr()).asObject());

        // Initialize and set cell value factory for artikelBezeichnungColumn
        artikelBezeichnungColumn = new TableColumn<>("Bezeichnung");
        artikelBezeichnungColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getBezeichnung()));

        // Add columns to the TableView
        artikelGraphTableView.getColumns().setAll(artikelNummerColumn, artikelBezeichnungColumn);

        artikelGraphTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        PseudoClass HIGHLIGHTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("highlighted");

        artikelGraphTableView.setRowFactory(tableView -> {
            TableRow<Artikel> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem == null) {
                    row.pseudoClassStateChanged(HIGHLIGHTED_PSEUDO_CLASS, false);
                } else {
                    row.pseudoClassStateChanged(HIGHLIGHTED_PSEUDO_CLASS, selectedArtikelSet.contains(newItem));
                }
            });

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                    && event.getClickCount() == 1) {
                    Artikel clickedItem = row.getItem();
                    if (selectedArtikelSet.contains(clickedItem)) {
                        this.removeFromGraph(clickedItem);
                        selectedArtikelSet.remove(clickedItem);
                        Platform.runLater(() -> {
                            artikelGraphTableView.getSelectionModel().clearSelection();
                            artikelGraphTableView.getFocusModel().focus(-1);
                        });
                    } else {
                        this.addToGraph(clickedItem);
                        selectedArtikelSet.add(clickedItem);
                    }
                    row.pseudoClassStateChanged(HIGHLIGHTED_PSEUDO_CLASS, selectedArtikelSet.contains(clickedItem));
                    event.consume();
                }
            });

            return row;
        });

        clearGraphButton.setOnAction(event -> {
            // Clear the graph data
            graph.getData().clear();

            // Clear the selected items set
            selectedArtikelSet.clear();

            // Refresh the table view to update row highlighting
            artikelGraphTableView.refresh();
        });

    }

    /**
     * löscht die Anzeige des angegebenen Artikels im Graphen
     * @param artikel
     */
    private void removeFromGraph(Artikel artikel) {
        XYChart.Series<Number, Number> series = seriesMap.remove(artikel);
        if (series != null) {
            graph.getData().remove(series);
        }
    }

    /**
     * updatet die Tabelle im Graphtab
     * @throws IOException
     */
    public void refreshTable() throws IOException {
        artikelGraphTableView.getItems().clear();
        ObservableList<Artikel> artikelObservableList = FXCollections.observableArrayList(ShopAPIClient.getShopAPI().getArtikelList());
        artikelGraphTableView.setItems(artikelObservableList);
    }

    /**
     * fügt die Bestandsveränderungen des Artikels der letzten 30 Tage dem Graphen hinzu
     * @param artikel
     */
    private void addToGraph(Artikel artikel) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(artikel.getArtNr() + ":" + artikel.getBezeichnung());

        try {
            List<Ereignis> ereignisse = ShopAPIClient.getShopAPI().sucheBestandshistorie(artikel.getArtNr(), 30, false);
            var iterator = ereignisse.iterator();
            int i = 30;
            while (iterator.hasNext()) {
                LocalDate date = LocalDate.now();
                Ereignis ereignis = iterator.next();
                while (date.minus(i, ChronoUnit.DAYS).isBefore(ereignis.getDatum().toLocalDate())) {
                    if (i > 0) {
                        if (ereignis.getEreignisTyp() == EreignisTyp.ARTIKEL_ANLEGEN) {
                            series.getData().add(new XYChart.Data<>(i, 0));
                        } else {
                            series.getData().add(new XYChart.Data<>(i, ereignis.getBestand()));
                        }
                        i--;
                    }
                }
            }
            while (i > 0) {
                series.getData().add(new XYChart.Data<>(i, artikel.getBestand()));
                i--;
            }
            series.getData().add(new XYChart.Data<>(0, artikel.getBestand()));
            graph.getData().add(series);
            seriesMap.put(artikel, series);
        } catch (ArtikelNichtGefundenException | IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }


}

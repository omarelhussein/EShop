package com.centerio.eshopfx.shop.ui.gui.controller;

import com.centerio.eshopfx.shop.domain.ShopAPI;
import com.centerio.eshopfx.shop.entities.Artikel;
import com.centerio.eshopfx.shop.entities.Massenartikel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

// TODO: IDEE IST NOCH NICHT FERTIG. WELCHE AUSWIRKUNG WENN DIE DATEN VON HIER AUS GEÄNDERT WERDEN AUF MEHRERE VIEWS?
//  ODER SOLL JEDE VIEW EINE EIGENE SHOPAPI INSTANZ HABEN? UND IHRE EIGENE DATEN VERWALTEN?
public class ArtikelTableController {

    @FXML
    private TableView<Artikel> artikelTable;
    @FXML
    private TableColumn<Artikel, String> artikelBezeichnungColumn;
    @FXML
    private TableColumn<Artikel, Double> artikelPreisColumn;
    @FXML
    private TableColumn<Artikel, Integer> artikelBestandColumn;
    @FXML
    private TableColumn<Artikel, Boolean> massenArtikelColumn;
    @FXML
    private TableColumn<Artikel, Integer> packGroesseColumn;

    private final ShopAPI shopAPI = ShopAPI.getInstance();

    public void initialize() {
        // Wir holen uns die ObservableList von der ShopAPI.
        initialisiereArtikel();
        // Wir initialisieren die Zellen der Tabelle mit den richtigen Werten.
        initialisiereTabellenZellen();
    }

    /**
     * Initialisiert die Spalten und Zellen der Tabelle, sodass diese die richtigen Werte anzeigen.
     */
    private void initialisiereTabellenZellen() {
        // setCellValueFactory legt fest, welche Daten in der Spalte angezeigt werden sollen.
        // Simple(XXX)Property legt fest, dass die Daten in der Spalte vom Typ XXX sind.
        // Es wird von Typ "Property" und nicht die "Standard" Typen wie String, Integer, etc. verwendet, da die Tabelle
        // die Daten automatisch aktualisieren soll, wenn sich die Daten ändern. Dies wird mithilfe von "Property" erreicht.
        // Properties intern haben einen Listener, der aufgerufen wird, wenn sich die Daten ändern.
        // Properties sind also auch observable values, die mit unseren UI-Elementen funktionieren.
        // Zelle Bezeichung: String
        artikelBezeichnungColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getBezeichnung()));
        // Zelle Preis: Double
        artikelPreisColumn.setCellValueFactory(p -> new SimpleDoubleProperty(p.getValue().getPreis()).asObject());
        // Zelle Bestand: Integer
        artikelBestandColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getBestand()).asObject());
        // Zelle Massenartikel: Boolean
        massenArtikelColumn.setCellValueFactory(p -> new SimpleBooleanProperty(p.getValue() instanceof Massenartikel));
        // Zelle Packgroesse: Integer (nur wenn Massenartikel)
        packGroesseColumn.setCellValueFactory(p -> {
            if (p.getValue() instanceof Massenartikel) {
                return new SimpleIntegerProperty(((Massenartikel) p.getValue()).getPackgroesse()).asObject();
            }
            return null;
        });
    }

    public void initialisiereArtikel() {
        artikelTable.setItems(shopAPI.getArtikelList());
    }

    public TableView<Artikel> getArtikelTable() {
        return artikelTable;
    }
}

package com.centerio.eshopfx.shop.ui.gui.controller;

import com.centerio.eshopfx.shop.domain.ShopAPI;
import com.centerio.eshopfx.shop.entities.Artikel;
import com.centerio.eshopfx.shop.entities.Massenartikel;
import com.centerio.eshopfx.shop.ui.gui.utils.SceneRoutes;
import com.centerio.eshopfx.shop.ui.gui.utils.StageManager;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.BooleanStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class MitarbeiterHomeController {

    @FXML
    private TableView<Artikel> artikelTable;
    @FXML
    private final ShopAPI shopAPI = ShopAPI.getInstance();

    // ObservableList ist eine Liste, die Änderungen an den Daten an die Tabelle weitergibt.
    // Eine ObservableList kann mithilfe von FXCollections.observableArrayList() erstellt werden.
    // Sie funktioniert wie eine normale Liste, nur dass sie Änderungen an die Tabelle weitergibt,
    // mithilfe von built-in Listener.
    ObservableList<Artikel> artikelList;
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

    /**
     * Das ist eine Standardmethode, die von JavaFX aufgerufen wird.
     * Diese Methode wird aufgerufen, wenn die View geladen wird.
     * Sie wird nach dem Konstruktor aufgerufen.
     * <p>
     * Hier können UI-Elemente initialisiert werden. In dem Konstruktor können UI-Elemente noch nicht initialisiert werden,
     * da diese noch nicht geladen wurden.
     */
    public void initialize() {
        // Wir holen uns die ObservableList von der ShopAPI.
        initialisiereArtikel();
        // Wir füllen die Zellen der Tabelle mit den Daten
        // (hier wird gemappt, welche Daten in welcher Spalte angezeigt werden sollen).
        initialisiereTabellenZellen();
        // Wir initialisieren die Bearbeitung der Zellen.
        initialisiereZellBearbeitung();
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

    /**
     * Initialisiert die Tabellenzellen für die Bearbeitung.
     * <p>
     * Diese Methode konfiguriert die Zellen in den Tabellenspalten, sodass
     * sie bearbeitet werden können. Nachdem die Zellen bearbeitet wurden,
     * werden die Änderungen gespeichert mithilfe eines {@link javafx.event.EventHandler}.
     * <p>
     * Dieser EventHandler (onEditCommit) wird aufgerufen, wenn die Zelle bearbeitet und bestätigt wurde.
     * <p>
     * Am Ende wird die Tabelle aktualisiert.
     */
    private void initialisiereZellBearbeitung() {
        // setCellFactory legt fest, wie die Zellen bearbeitet werden können.
        // Standardmäßig können die Zellen nicht bearbeitet werden.
        // TextFieldTableCell.forTableColumn() legt fest, dass die Zellen mit einem Textfeld bearbeitet werden können.
        artikelBezeichnungColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        // setOnEditCommit legt fest, was passiert, wenn die Zelle bearbeitet und bestätigt wurde (mit Enter oder Tab).
        artikelBezeichnungColumn.setOnEditCommit(p -> {
            p.getRowValue().setBezeichnung(p.getNewValue());
            artikelTable.refresh();
        });

        artikelPreisColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        artikelPreisColumn.setOnEditCommit(p -> {
            p.getRowValue().setPreis(p.getNewValue());
            artikelTable.refresh();
        });

        artikelBestandColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        artikelBestandColumn.setOnEditCommit(p -> {
            p.getRowValue().setBestand(p.getNewValue());
            artikelTable.refresh();
        });

        // Hier wird eine ComboBox in der Zelle angezeigt (Dropdown).
        massenArtikelColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new BooleanStringConverter() {
            @Override
            public String toString(Boolean object) {
                return object ? "Ja" : "Nein";
            }
        }, true, false));
        massenArtikelColumn.setOnEditCommit(p -> {
            // p.getNewValue gibt den neuen Wert (Ja oder Nein von der ComboBox) als Boolean zurück.
            if (p.getNewValue()) {
                // Wenn der neue Wert Ja ist, dann wird der Artikel zu einem Massenartikel.
                Artikel oldArtikel = p.getRowValue();
                // Wir erstellen einen neuen Massenartikel mit den Daten des alten Artikels.
                oldArtikel = new Massenartikel(oldArtikel.getArtNr(),
                        oldArtikel.getBezeichnung(),
                        oldArtikel.getPreis(),
                        oldArtikel.getBestand(),
                        1);
                // Wir ersetzen den alten Artikel mit dem neuen Massenartikel.
                artikelList.set(artikelList.indexOf(p.getRowValue()), oldArtikel);
                // Wir aktualisieren die Tabelle.
                artikelTable.refresh();
            }
        });

        packGroesseColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        packGroesseColumn.setOnEditCommit(p -> {
            if (p.getRowValue() instanceof Massenartikel) {
                ((Massenartikel) p.getRowValue()).setPackgroesse(p.getNewValue());
            } else {
                // Falls kein Massenartikel, dann wird eine Fehlermeldung angezeigt.
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Fehler");
                alert.setHeaderText("Fehler beim Ändern der Packgröße");
                alert.setContentText("Die Packgröße kann nur bei Massenartikeln geändert werden.");
                alert.show();
            }
            // Falls kein Massenartikel, dann wird nichts geändert. Refresh übernimmt die Werte von der Liste.
            // In der Liste wurde nichts geändert, also wird die Tabelle die "gespeicherten" Werte anzeigen.
            artikelTable.refresh();
        });
    }

    public void save() {
        shopAPI.speichern();
    }

    public void addArtikel() {
        Artikel newArtikel = new Artikel(shopAPI.getNaechsteArtikelId());
        artikelList.add(newArtikel);
        artikelTable.getSelectionModel().select(newArtikel);
        artikelTable.scrollTo(newArtikel);
    }

    public void deleteArtikel() {
        Artikel selectedArtikel = artikelTable.getSelectionModel().getSelectedItem();
        if (selectedArtikel != null) {
            artikelList.remove(selectedArtikel);
        }
    }

    public void initialisiereArtikel() {
        artikelList = shopAPI.getArtikelList();
        artikelTable.setItems(artikelList);
    }

    public void logout() {
        shopAPI.logout();
        StageManager.getInstance().switchScene(SceneRoutes.LOGIN_VIEW);
    }

}

package com.centerio.eshopfx.shop.ui.gui.concerns;

import com.centerio.eshopfx.shop.domain.ArtikelService;
import com.centerio.eshopfx.shop.domain.RemoteInterface;
import com.centerio.eshopfx.shop.domain.ShopAPI;
import com.centerio.eshopfx.shop.domain.exceptions.artikel.AnzahlPackgroesseException;
import com.centerio.eshopfx.shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.BestandUeberschrittenException;
import com.centerio.eshopfx.shop.domain.exceptions.warenkorb.WarenkorbArtikelNichtGefundenException;
import com.centerio.eshopfx.shop.entities.Artikel;
import com.centerio.eshopfx.shop.entities.Massenartikel;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.lang.ref.Cleaner;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class ArtikelTableConcern {

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

    Registry registry = LocateRegistry.getRegistry("LocalHost", 1099);

    private final RemoteInterface shopAPI = (RemoteInterface) registry.lookup("RemoteObject");


    public ArtikelTableConcern(
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
    }

    public ArtikelTableConcern(TableColumn<Artikel, Integer> artikelNummerColumn,
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
                               ) throws RemoteException, NotBoundException {
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

    public void setMitarbeiterEventHandlersForArtikel(){
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
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ArtikelNichtGefundenException ex) {
                throw new RuntimeException(ex);
            }
        });
        suchField.setOnKeyPressed(e -> {
            clearSuchFieldKey(e);
        });
        suchField.setOnKeyTyped(e -> {
            artikelSuchen();
        });
        clearButton.setOnAction(e -> {
            clearSuchField();
        });

    }

    public void setArtikelInTable() throws IOException {
        artikelTableView.getItems().clear();
        ObservableList<Artikel> artikelObservableList = FXCollections.observableArrayList();
        for(Artikel artikel : ArtikelService.getInstance().getArtikelList()){
            artikelObservableList.add(artikel);
        }
        artikelTableView.setItems(artikelObservableList);
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

    /**
     * Kundenfunktionen
     */
    public void toWarenkorb(WarenkorbTableConcern warenkorbTableConcern) {
        try {
            int selectedId = artikelTableView.getSelectionModel().getSelectedIndex();
            if (selectedId >= 0) {
                Artikel artikel = artikelTableView.getItems().get(selectedId);
                try {
                    if(artikelAnzahlField.getText().isEmpty()){
                        int anzahl = 1;
                        if (artikel instanceof Massenartikel) anzahl = ((Massenartikel) artikel).getPackgroesse();
                        shopAPI.addArtikelToWarenkorb(artikel.getArtNr(), anzahl);
                        warenkorbTableConcern.setWarenkorbInTable();
                        warenkorbTableConcern.initializeGesamtPreis();
                    } else {
                        shopAPI.addArtikelToWarenkorb(artikel.getArtNr(), Integer.parseInt(artikelAnzahlField.getText()));
                        warenkorbTableConcern.setWarenkorbInTable();
                        warenkorbTableConcern.initializeGesamtPreis();
                    }
                } catch (NumberFormatException e) {
                    addToWarenkorbButton.setStyle("-fx-border-color: red;");
                } catch (AnzahlPackgroesseException e) {
                    System.out.println(e);
                }
            } else {
                addToWarenkorbButton.setStyle("-fx-border-color: red;");
            }
        } catch (IOException | ArtikelNichtGefundenException | BestandUeberschrittenException |
                 WarenkorbArtikelNichtGefundenException e) {
            addToWarenkorbButton.setStyle("-fx-border-color: red;");
            System.out.println(e);
        }
    }

    public void setKundeEventHandlerForArtikel(WarenkorbTableConcern warenkorbTableConcern){
        suchField.setOnKeyPressed(e -> {
            clearSuchFieldKey(e);
        });
        suchField.setOnKeyTyped(e -> {
            artikelSuchen();
        });
        clearButton.setOnAction(e -> {
            clearSuchField();
        });
        addToWarenkorbButton.setOnAction(e -> {
            toWarenkorb(warenkorbTableConcern);
        });
    }

    /**
     * Mitarbeiterfunktionen
     */
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

    public void removeArtikel() throws IOException, ArtikelNichtGefundenException {
        int selectedId = artikelTableView.getSelectionModel().getSelectedIndex();
        shopAPI.removeArtikel(artikelTableView.getItems().get(selectedId).getArtNr());
        artikelTableView.getItems().remove(selectedId);
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
                shopAPI.addArtikel(artikel);
            } else {
                Artikel artikel = new Artikel(ArtikelService.getInstance().getNaechsteId(), artikelBezeichnungFeld.getText(),
                        (Double.parseDouble(artikelPreisFeld.getText())), Integer.parseInt(artikelBestandFeld.getText()));
                shopAPI.addArtikel(artikel);
            }
            setArtikelInTable();
            clearFelder();
        }catch (RuntimeException e) {
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

    private boolean FieldCheck(TextField field) {
        if (field.getText().isEmpty()) {
            field.setStyle("-fx-border-color: red;");
            return true;
        }
        return false;
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


}



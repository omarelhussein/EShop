package com.centerio.eshopfx.gui.components;

import com.centerio.eshopfx.ShopAPIClient;
import domain.ShopAPI;
import entities.Kunde;
import entities.Mitarbeiter;
import entities.Person;
import exceptions.personen.PersonVorhandenException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class PersonenTable {

    private TableColumn<Person, String> personTypColumn;
    private TableColumn<Person, String> nutzernameColumn;
    private TableColumn<Person, String> nameColumn;
    private TableColumn<Person, Integer> personNummerColumn;
    private TableView<Person> personenTableView;
    private TextField mitarbeiterNameField;

    private TextField nutzernameField;

    private TextField passwortField;

    private Button registerMitarbeiterButton;

    private final ShopAPI shopAPI = ShopAPIClient.getShopAPI();

    public PersonenTable(
            TableColumn<Person, String> personTypColumn,
    TableColumn<Person, String> nutzernameColumn,
    TableColumn<Person, String> nameColumn,
    TableColumn<Person, Integer> personNummerColumn,
    TableView<Person> personenTableView,
            TextField mitarbeiterNameField,
            TextField nutzernameField,
            TextField passwortField,
            Button registerMitarbeiterButton
    ) throws NotBoundException, RemoteException {
        this.personTypColumn = personTypColumn;
        this.nutzernameColumn = nutzernameColumn;
        this.nameColumn = nameColumn;
        this.personNummerColumn = personNummerColumn;
        this.personenTableView = personenTableView;
        this.mitarbeiterNameField = mitarbeiterNameField;
        this.nutzernameField = nutzernameField;
        this.passwortField = passwortField;
        this.registerMitarbeiterButton = registerMitarbeiterButton;
    }

    public void initializePersonView() {
        personNummerColumn = new TableColumn("Nummer");
        personTypColumn = new TableColumn("Typ");
        nutzernameColumn = new TableColumn("Nutzername");
        nameColumn = new TableColumn("Name");
        personenTableView.getColumns().addAll(personNummerColumn, personTypColumn, nutzernameColumn, nameColumn);
        personNummerColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getPersNr()).asObject());
        personTypColumn.setCellValueFactory(p -> {
            if (p.getValue() instanceof Kunde) {
                return new SimpleStringProperty("Kunde");
            }
            return new SimpleStringProperty("Mitarbeiter");
        });
        nutzernameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getNutzername()));
        nameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getName()));
    }

    /**
     * updatet die Personentabelle
     * @throws IOException
     */
    public void setPersonInTable() throws IOException {
        personenTableView.getItems().clear();
        ObservableList<Person> personObservableList = FXCollections.observableArrayList();
        personObservableList.addAll(shopAPI.getPersonList());
        personenTableView.setItems(personObservableList);
    }

    /**
     * registriert einen neuen Mitarbeiter mit den Daten in den dafür vorgesehenen Feldern
     * @throws PersonVorhandenException
     * @throws IOException
     */
    public void mitarbeiterRegistrieren() throws PersonVorhandenException, IOException {
        shopAPI.registrieren(new Mitarbeiter(shopAPI.getNaechstePersId(), nutzernameField.getText(), mitarbeiterNameField.getText(), passwortField.getText()));
        setPersonInTable();
    }

    /**
     * Eventhandler für die Personentabelle
     */
    public void setEventHandlerForPersonen(){
        registerMitarbeiterButton.setOnAction(e -> {
            try {
                mitarbeiterRegistrieren();
            } catch (PersonVorhandenException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}

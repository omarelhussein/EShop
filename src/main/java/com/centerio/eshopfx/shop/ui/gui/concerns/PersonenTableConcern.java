package com.centerio.eshopfx.shop.ui.gui.concerns;

import com.centerio.eshopfx.shop.domain.RemoteInterface;
import com.centerio.eshopfx.shop.domain.RemoteSingletonService;
import com.centerio.eshopfx.shop.domain.ShopAPI;
import com.centerio.eshopfx.shop.domain.exceptions.personen.PersonVorhandenException;
import com.centerio.eshopfx.shop.entities.Artikel;
import com.centerio.eshopfx.shop.entities.Kunde;
import com.centerio.eshopfx.shop.entities.Mitarbeiter;
import com.centerio.eshopfx.shop.entities.Person;
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
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class PersonenTableConcern {

    private TableColumn<Person, String> personTypColumn;
    private TableColumn<Person, String> nutzernameColumn;
    private TableColumn<Person, String> nameColumn;
    private TableColumn<Person, Integer> personNummerColumn;
    private TableView<Person> personenTableView;
    private TextField mitarbeiterNameField;

    private TextField nutzernameField;

    private TextField passwortField;

    private Button registerMitarbeiterButton;

    private Registry registry = LocateRegistry.getRegistry("localhost", 1099);
    private RemoteSingletonService singletonService = (RemoteSingletonService) registry.lookup("RemoteObject");
    private RemoteInterface shopAPI = singletonService.getSingletonInstance();


    public PersonenTableConcern(
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

    public void setPersonInTable() throws IOException {
        personenTableView.getItems().clear();
        ObservableList<Person> personObservableList = FXCollections.observableArrayList();
        personObservableList.addAll(shopAPI.getPersonList());
        personenTableView.setItems(personObservableList);
    }

    public void mitarbeiterRegistrieren() throws PersonVorhandenException, IOException {
        shopAPI.registrieren(new Mitarbeiter(shopAPI.getNaechstePersId(), nutzernameField.getText(), mitarbeiterNameField.getText(), passwortField.getText()));
        setPersonInTable();
    }

    public void setEventHandlerForPersonen(){
        registerMitarbeiterButton.setOnAction(e -> {
            try {
                mitarbeiterRegistrieren();
            } catch (PersonVorhandenException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}

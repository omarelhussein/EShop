package com.centerio.eshopfx.gui.utils;

import com.centerio.eshopfx.ShopAPIClient;
import domain.ShopAPI;
import entities.Person;
import entities.UserContext;
import exceptions.personen.PasswortNameException;

import java.io.IOException;
import java.rmi.RemoteException;

public class LoginUtils {

    private final ShopAPI shopAPI;

    public LoginUtils() throws RemoteException {
        shopAPI = ShopAPIClient.getShopAPI();
    }

    public Person login(String nutzername, String passwort) throws IOException, PasswortNameException {
        var nutzer = shopAPI.login(nutzername, passwort);
        UserContext.setUser(nutzer);
        return nutzer;
    }

    public void logout() throws IOException {
        shopAPI.logout();
        UserContext.setUser(null);
    }

}

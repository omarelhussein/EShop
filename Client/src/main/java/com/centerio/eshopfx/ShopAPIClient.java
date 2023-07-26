package com.centerio.eshopfx;

import domain.ShopAPI;
import entities.UserContext;
import entities.enums.Constants;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.logging.Logger;

public class ShopAPIClient {

    private static ShopAPI shopAPI;
    private static final Logger logger = Logger.getLogger(ShopAPIClient.class.getName());

    public static ShopAPI getShopAPI() throws RemoteException {
        if (shopAPI == null) {
            try {
                logger.info("Connecting to Server...");
                shopAPI = (ShopAPI) Naming.lookup(String.format("rmi://localhost:%d/ShopAPI", Constants.PORT));
                logger.info("Connected to Server successfully");
            } catch (Exception e) {
                logger.severe("Could not find ShopAPI in RMI registry");
                throw new RemoteException("Could not find ShopAPI in RMI registry", e);
            }
        }
        shopAPI.setUserContext(UserContext.getUser());
        return shopAPI;
    }

}

package com.centerio.eshopfx.shop;
import com.centerio.eshopfx.shop.domain.RemoteInterface;
import com.centerio.eshopfx.shop.domain.ShopAPI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
public class RMIServer {
    public static void main(String[] args){
        try {
            RemoteInterface remoteObject = ShopAPI.getInstance();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("RemoteObject", remoteObject);
            System.out.println("Server läuft...");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}

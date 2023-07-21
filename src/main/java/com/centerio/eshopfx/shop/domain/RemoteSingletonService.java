package com.centerio.eshopfx.shop.domain;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteSingletonService extends Remote {
    RemoteInterface getSingletonInstance() throws RemoteException;
}

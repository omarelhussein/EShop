package domain;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ShopEventListener extends Remote {

	void handleArtikelListChanged() throws RemoteException;

}

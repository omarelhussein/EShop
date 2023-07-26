package domain;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ShopEventListener extends Remote {

	/**
	 * Remoteinterface für ShopEventListener
	 * @throws RemoteException
	 */
	void handleArtikelListChanged() throws RemoteException;

}

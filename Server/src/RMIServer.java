
import domain.ShopAPI;
import domain.ShopAPIImpl;
import entities.enums.Constants;
import utils.SeedingUtils;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

public class RMIServer {

    private static final Logger logger = Logger.getLogger(RMIServer.class.getName());

    /**
     * RMI-Server wird initialisiert
     * Hierbei ist die ShopAPIImpl das UnicastRemoteObject und fungiert als stub.
     * @param args
     */
    public static void main(String[] args) {
        logger.info("Server wird gestartet...");
        try {
            ShopAPIImpl shopApiImpl = new ShopAPIImpl();
            ShopAPI stub = (ShopAPI) UnicastRemoteObject.exportObject(shopApiImpl, 0);

            logger.info("Registry mit Port " + Constants.PORT + " wird erstellt...");
            Registry registry = LocateRegistry.createRegistry(Constants.PORT);
            logger.info("ShopAPI wird gebunden...");
            registry.rebind("ShopAPI", stub);
            logger.info("Server wurde gestartet!");

            logger.info("Seeding wird ausgeführt...");
            new SeedingUtils();
            logger.info("Seeding wurde ausgeführt!");
            logger.info("Server läuft...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

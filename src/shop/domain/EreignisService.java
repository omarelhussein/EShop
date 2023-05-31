package shop.domain;

import shop.entities.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class EreignisService {
    private static EreignisService ereignisService;
    private final ArrayList<Ereignis> ereignisList;


    private EreignisService() {
        ereignisList = new ArrayList<>();
    }

    public static synchronized EreignisService getInstance() {
        if (ereignisService == null) {
            ereignisService = new EreignisService();
        }
        return ereignisService;
    }

    public Ereignis addEreignis(EreignisTyp ereignisTyp, Object obj, boolean erfolg) {
        if (!ereignisTyp.isSichtbar()) return null;
        var user = UserContext.getUser();
        if (user == null) { // at application start, no user is logged in, but we still want to log events
            user = new Mitarbeiter(1, "system", "system", "system");
        }
        Ereignis ereignis = new Ereignis(user, obj, ereignisTyp, LocalDateTime.now(), erfolg);
        ereignisList.add(ereignis);
        return ereignis;
    }

    public ArrayList<Ereignis> kundeOderMitarbeiterEreignisListe() {
        if (UserContext.getUser() instanceof Kunde) {
            ArrayList<Ereignis> kundenEreignisListe = new ArrayList<>();
            for (Ereignis ereignis : getEreignisList()) {
                if (ereignis.person().equals(UserContext.getUser())) {
                    kundenEreignisListe.add(ereignis);
                }
            }
            return kundenEreignisListe;
        } else {
            if (UserContext.getUser() instanceof Mitarbeiter) {
                ArrayList<Ereignis> mitarbeiterEreignisListe = new ArrayList<>();
                for (Ereignis ereignis : getEreignisList()) {
                    if (ereignis.person() instanceof Mitarbeiter) {
                        mitarbeiterEreignisListe.add(ereignis);
                    }
                }
                return mitarbeiterEreignisListe;
            }
            return getEreignisList();
        }
    }

    public ArrayList<Ereignis> getEreignisList() {
        return ereignisList;
    }

}

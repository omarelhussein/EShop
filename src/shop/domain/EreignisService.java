package shop.domain;

import shop.entities.*;
import shop.entities.enums.EreignisTyp;
import shop.entities.enums.KategorieEreignisTyp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EreignisService {
    private static EreignisService ereignisService;
    private final ArrayList<Ereignis> ereignisList;
    private final ArrayList<BestandshistorieItem> bestandshistorieItemsList;

    private EreignisService() {
        ereignisList = new ArrayList<>();
        bestandshistorieItemsList = new ArrayList<>();
    }

    public static synchronized EreignisService getInstance() {
        if (ereignisService == null) {
            ereignisService = new EreignisService();
        }
        return ereignisService;
    }

    public void addEreignis(KategorieEreignisTyp ereignisTyp, Object obj, boolean erfolg) {
        var user = UserContext.getUser();
        if (user == null) { // at application start, no user is logged in, but we still want to log events
            user = new Mitarbeiter(1, "system", "system", "system");
        }
        if(ereignisTyp == KategorieEreignisTyp.ARTIKEL_EREIGNIS && obj instanceof Artikel){
            Ereignis ereignis = new Ereignis(user, obj, ereignisTyp, LocalDateTime.now(), erfolg, ((Artikel) obj).getBestand());
            ereignisList.add(ereignis);
        } else {
            Ereignis ereignis = new Ereignis(user, obj, ereignisTyp, LocalDateTime.now(), erfolg);
            ereignisList.add(ereignis);
        }
    }

    public List<Ereignis> getArtikelEreignishistorie() {
        List<Ereignis> artikelhistorie = new ArrayList<>();
        for (Ereignis ereignis : ereignisList) {
            if (ereignis.getKategorieEreignisTyp() == KategorieEreignisTyp.ARTIKEL_EREIGNIS && ereignis.getObject() instanceof Artikel
                && (KategorieEreignisTyp.ARTIKEL_EREIGNIS.getEreignisTyps() == EreignisTyp.ARTIKEL_ANLEGEN )) {
                artikelhistorie.add(ereignis);
            }
        }
        return artikelhistorie;
    }

    public ArrayList<Ereignis> kundeOderMitarbeiterEreignisListe() {
        if (UserContext.getUser() instanceof Kunde) {
            ArrayList<Ereignis> kundenEreignisListe = new ArrayList<>();
            for (Ereignis ereignis : getEreignisList()) {
                if (ereignis.getPerson().equals(UserContext.getUser())) {
                    kundenEreignisListe.add(ereignis);
                }
            }
            return kundenEreignisListe;
        } else {
            if (UserContext.getUser() instanceof Mitarbeiter) {
                ArrayList<Ereignis> mitarbeiterEreignisListe = new ArrayList<>();
                for (Ereignis ereignis : getEreignisList()) {
                    if (ereignis.getPerson() instanceof Mitarbeiter) {
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

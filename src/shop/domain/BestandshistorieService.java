package shop.domain;

import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.entities.Artikel;
import shop.entities.BestandsHistorie;
import shop.entities.Ereignis;
import shop.entities.EreignisArt;

import java.time.LocalDateTime;

public class BestandshistorieService {
    private static BestandshistorieService bestandshistorieService;

    public static synchronized BestandshistorieService getInstance() {
        if (bestandshistorieService == null) {
            bestandshistorieService = new BestandshistorieService();
        }
        return bestandshistorieService;
    }

    public void addBestandHistorie() {
        var ereignisList = EreignisService.getInstance().getEreignisList();
        Ereignis e = ereignisList.get(ereignisList.size() - 1);
        if (e.getEreignisArt() == EreignisArt.BESTAND_VERAENDERUNG) {
            e.getBestandshistorie().getBestandsHistorieListe().add(e.getObjectBestand());
            e.getBestandshistorie().getDatum().add(getLastDatum());
        }
        if (e.getEreignisArt() == EreignisArt.BESTAND_AKTUALISIERUNG) {
            for (Artikel artikel : ArtikelService.getInstance().getArtikelList()) {
                artikel.getBestandsHistorie().getBestandsHistorieListe().add(artikel.getBestand());
                artikel.getBestandsHistorie().getDatum().add(getLastDatum());
            }
        }


    }


    public BestandsHistorie suchBestandshistorie(int ArtNr) throws ArtikelNichtGefundenException {
        Artikel artikel = ArtikelService.getInstance().getArtikelByArtNr(ArtNr);
        return artikel.getBestandsHistorie();
    }

    public LocalDateTime getLastDatum() {
        return EreignisService
                .getInstance()
                .getEreignisList()
                .get(EreignisService.getInstance().getEreignisList().size() - 1)
                .getDatum();
    }

}

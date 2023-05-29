package shop.domain;

import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.entities.Artikel;
import shop.entities.BestandshistorieItem;
import shop.entities.Ereignis;

import java.util.List;

public class BestandshistorieService {
    private static BestandshistorieService bestandshistorieService;

    public static synchronized BestandshistorieService getInstance() {
        if (bestandshistorieService == null) {
            bestandshistorieService = new BestandshistorieService();
        }
        return bestandshistorieService;
    }

    public void addBestandshistorie(Ereignis ereignis) {
        if (ereignis.getEreignisArt() == null) return;
        switch (ereignis.getEreignisArt()) {
            case BESTAND_VERAENDERUNG -> {
                var bestandshistorie = new BestandshistorieItem(ereignis.getObjectBestand());
                ((Artikel) ereignis.getObject()).getBestandshistorie().add(bestandshistorie);
            }
            case KAUF -> {
                for (Artikel artikel : ((List<Artikel>)ereignis.getObject())) {
                    if(artikel.getBestandshistorie().get(artikel.getBestandshistorie().size() - 1).getBestand() != artikel.getBestand()) {
                        var bestandshistorie = new BestandshistorieItem(artikel.getBestand());
                        artikel.getBestandshistorie().add(bestandshistorie);
                    }
                }
            }

        }
    }

    public List<BestandshistorieItem> suchBestandshistorie(int artNr) throws ArtikelNichtGefundenException {
        return ArtikelService.getInstance().getArtikelByArtNr(artNr).getBestandshistorie();
    }

}

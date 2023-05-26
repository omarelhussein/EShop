package shop.domain;

import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.entities.Artikel;
import shop.entities.Bestandshistorie;
import shop.entities.Ereignis;

import java.util.List;

public class BestandshistorieService {
    private ArtikelService artikelService;
    private EreignisService ereignisService;
    private static BestandshistorieService bestandshistorieService;

    public static synchronized BestandshistorieService getInstance(){
        if (bestandshistorieService == null) {
            bestandshistorieService = new BestandshistorieService();
        }
        return bestandshistorieService;
    }
    public void addBestandHistorie(){
        Ereignis e = ((Ereignis)ereignisService.getInstance().getEreignisList().get(ereignisService.getInstance().getEreignisList().size()-1));
        if(e.getEreignisArt().equals("Bestandveraenderung")){
            e.getBestandshistorie().getBestandshistoryList().add(e.getObjectBestand());
            e.getBestandshistorie().getDatum().add(getLastDatum());
        }
        if(e.getEreignisArt().equals("BestandUpdate")){
                for(Artikel artikel : artikelService.getInstance().getArtikelList()){
                    artikel.getBestandshistory().getBestandshistoryList().add(artikel.getBestand());
                    artikel.getBestandshistory().getDatum().add(getLastDatum());
                }
        }


    }


    public Bestandshistorie suchBestandshistorie(int ArtNr) throws ArtikelNichtGefundenException {
        Artikel artikel = artikelService.getInstance().getArtikelByArtNr(ArtNr);
        return artikel.getBestandshistory();
    }

    public String getLastDatum(){
        return ((Ereignis)ereignisService.getInstance().getEreignisList().get(ereignisService.getInstance().getEreignisList().size()-1)).getDatum();
    }

}

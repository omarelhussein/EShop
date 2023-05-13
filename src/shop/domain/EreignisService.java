package shop.domain;

import shop.entities.Artikel;
import shop.entities.Ereignis;
import shop.entities.Person;

import java.util.ArrayList;

public class EreignisService {

    private static EreignisService ereignisService;

    private ArrayList<Ereignis> ereignisList = new ArrayList<>();

    private ArtikelService artikelService = new ArtikelService();
    private Person person;

    private EreignisService(){
    }
    public static synchronized EreignisService getInstance(){
        if (ereignisService == null){
            ereignisService = new EreignisService();
        }
        return ereignisService;
    }
    public void artikelAddEreignis(Artikel artikel){
        String description = "hat " + artikel.toString() + "hinzugefügt.";
        Ereignis addArtikelEreignis = new Ereignis(person, description);
        ereignisList.add(addArtikelEreignis);
    }

    public void artikelRemoveEreignis(Artikel artikel) {
        String description = "hat " + artikel.toString() + "entfernt.";
        Ereignis removeArtikelEreignis = new Ereignis(person, description);
        ereignisList.add(removeArtikelEreignis);
    }

    public void sucheArtikelByNameEreignis(String queryString){
        String description = "hat " + queryString + "gesucht.";
        Ereignis ArtikelSucheByNameEreignis = new Ereignis(person, description);
        ereignisList.add(ArtikelSucheByNameEreignis);
    }

    public void sucheArtikelByNr(int Nr){
        String description = "hat " + Nr + "gesucht.";
        Ereignis artikelSucheByNrEreignis = new Ereignis(person, description);
        ereignisList.add(artikelSucheByNrEreignis);
    }

    public void setPerson(Person person){
        ereignisService.person = person;
    }

}

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
        String description = "hat " + artikel.toString() + "hinzugefügt."; //placeholder
        Ereignis addArtikelEreignis = new Ereignis(person, description);
        ereignisList.add(addArtikelEreignis);
    }

    public void artikelRemoveEreignis(Artikel artikel) {
        String description = "hat " + artikel.toString() + "entfernt."; //placeholder
        Ereignis removeArtikelEreignis = new Ereignis(person, description);
        ereignisList.add(removeArtikelEreignis);
    }

    public void sucheArtikelByNameEreignis(String queryString){
        String description = "hat " + queryString + "gesucht."; //placeholder
        Ereignis ArtikelSucheByNameEreignis = new Ereignis(person, description);
        ereignisList.add(ArtikelSucheByNameEreignis);
    }

    public void sucheArtikelByNrEreignis(int Nr){
        String description = "hat " + Nr + "gesucht."; //placeholder
        Ereignis artikelSucheByNrEreignis = new Ereignis(person, description);
        ereignisList.add(artikelSucheByNrEreignis);
    }

    public void getArtikelListEreignis(){
        String description = "hat Artikelliste ausgegeben."; //placeholder
        Ereignis getArtikelListeEreignis = new Ereignis(person, description);
        ereignisList.add(getArtikelListeEreignis);
    }

    public void getArtikelByArtNrEreignis(int artikelNr){
        String description = "hat Artikel nach Artikel Nr " + artikelNr + "gefetched"; //placeholder
        Ereignis getArtikelByArtikelNrEreignis = new Ereignis(person, description);
        ereignisList.add(getArtikelByArtikelNrEreignis);
    }

    public void getArtikelByArtQueryEreignis(String query){
        String description = "hat " + query + " über Query gesucht."; //placeholder
        Ereignis artikelSucheByQueryEreignis = new Ereignis(person, description);
        ereignisList.add(artikelSucheByQueryEreignis);
    }

    public void getLoginEreignis(){
        String description ="hat sich eingeloggt";
        Ereignis loginEreignis = new Ereignis(person, description);
        ereignisList.add(loginEreignis);
    }


    public void setPerson(Person person){
        ereignisService.person = person;
    }

    public ArrayList<Ereignis> getEreignisList(){
        return ereignisList;
    }
}

package shop.entities;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Warenkorb implements Serializable {

    private final Kunde kunde;
    private final List<WarenkorbArtikel> warenkorbArtikelList;

    public Warenkorb(Kunde kunde) {
        this.kunde = kunde;
        this.warenkorbArtikelList = new ArrayList<>();
    }

    public void addArtikel(WarenkorbArtikel artikel) {
        this.warenkorbArtikelList.add(artikel);
    }

    public boolean removeArtikel(WarenkorbArtikel artikel) {
        if (artikel != null) {
            return this.warenkorbArtikelList.remove(artikel);
        }
        return false;
    }

    public double getGesamtSumme() {
        double preis = 0;
        for (WarenkorbArtikel value : this.warenkorbArtikelList) {
            preis += value.getGesamtPreis();
        }
        return Math.round(preis * 100.) / 100.;
    }

    public List<Artikel> getArtikelList() {
        List<Artikel> artikel = new ArrayList<>();
        for (WarenkorbArtikel value : this.warenkorbArtikelList) {
            if (value.getArtikel() != null) {
                artikel.add(value.getArtikel());
            }
        }
        return artikel;
    }

    public List<WarenkorbArtikel> getWarenkorbArtikelList() {
        return this.warenkorbArtikelList;
    }

    public int getAnzahlArtikel() {
        int anzahl = 0;
        for (WarenkorbArtikel value : this.warenkorbArtikelList) {
            anzahl += value.getAnzahl();
        }
        return anzahl;
    }

    public Kunde getKunde() {
        return this.kunde;
    }

    public boolean istLeer() {
        return this.warenkorbArtikelList.isEmpty();
    }

    public Warenkorb deepCopy() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            out.close();

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            Warenkorb copy = (Warenkorb) in.readObject();
            in.close();

            return copy;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Could not deep copy Warenkorb", e);
        }
    }

}

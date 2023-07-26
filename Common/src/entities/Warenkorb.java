package entities;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Warenkorb implements Serializable {

    private final Kunde kunde;
    private final List<WarenkorbArtikel> warenkorbArtikelList;

    public Warenkorb(Kunde kunde) {
        this.kunde = kunde;
        this.warenkorbArtikelList = Collections.synchronizedList(new ArrayList<>());
    }

    public synchronized void addArtikel(WarenkorbArtikel artikel) {
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
        // Diese Zeile rundet das Ergebnis auf zwei Dezimalstellen.
        // 'Math.round(... * 100.0) / 100.0' ist ein gängiges Muster zum Runden auf zwei Dezimalstellen
        // die .0 ist wichtig, da sonst die Division als Integer-Division interpretiert wird.
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

    /**
     * Erstellt eine tiefe Kopie (Deep Copy) dieses Warenkorbs.
     * Eine tiefe Kopie bedeutet, dass alle in diesem Warenkorb enthaltenen Objekte kopiert werden,
     * sodass die kopierte Warenkorb-Instanz vollständig unabhängig von der ursprünglichen ist.
     * Die Methode nutzt die Serialisierung, um die tiefe Kopie zu erstellen. Zunächst wird
     * der Warenkorb in einen Byte-Array-Output-Stream geschrieben. Dann wird ein neuer
     * Warenkorb aus diesem Stream gelesen.
     *
     * @return eine tiefe Kopie dieses Warenkorbs
     * @throws RuntimeException wenn die tiefe Kopie nicht erstellt werden kann
     */
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

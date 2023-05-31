package shop.domain;

import shop.domain.exceptions.artikel.ArtikelNichtGefundenException;
import shop.entities.ArtikelHistorie;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BestandshistorieService {

    private static final int STANDARD_TAGE_ZURUECK = 30;

    public List<ArtikelHistorie> suchBestandshistorie(int artNr, int tage, Boolean istKaufFilter) throws ArtikelNichtGefundenException, IOException {
        var artikel = ArtikelService.getInstance().getArtikelByArtNr(artNr);
        var neueTage = ueberpruefeTage(tage);
        return artikel.getBestandshistorie().stream()
                .filter(
                        eintrag -> eintrag
                                .getDatum()
                                .isAfter(eintrag.getDatum().minus(neueTage, ChronoUnit.DAYS))
                ).filter(
                        bestandshistorie -> istKaufFilter == null || bestandshistorie.istKauf() == istKaufFilter
                ).map(
                        bestandshistorie -> new ArtikelHistorie(artikel, bestandshistorie)
                ).toList();
    }

    /**
     * Überprüft, ob die Anzahl der Tage größer als 0 ist.
     * Wenn nicht, wird der Standardwert verwendet.
     */
    private int ueberpruefeTage(int tage) {
        if (tage <= 0) tage = STANDARD_TAGE_ZURUECK;
        return tage;
    }

}

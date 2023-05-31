package shop.entities;

/**
 * Die ArtikelHistorie Record-Klasse speichert die Historie eines bestimmten Artikels
 * zusammen mit einem zugehörigen BestandshistorieItem.
 * <p>
 * Eine Record-Klasse in Java ist eine spezielle Art von Klasse, die für die Speicherung
 * von unveränderlichen Daten verwendet wird. Sie generiert automatisch endgültige Felder
 * und entsprechende Getter-Methoden, sowie Methoden wie equals(), hashCode() und toString().
 */
public record ArtikelHistorie(Artikel artikel, BestandshistorieItem bestandshistorieItem) {
}

                          ## Aufbau des Projekts
Das Projekt besteht aus 3 Modulen:
- `Common` - Enthält die gemeinsamen Klassen, die von Server und Client verwendet werden.
- `Server` - Enthält die Server-Anwendung.
- `Client` - Enthält die Client-Anwendung(en).

### Top-Level-Architekturdiagramm

    +------------+   RMI    +-----------+
    |            |<-------->|           |
    |   Server   |          |   Client  |
    |            |          |(CUI & GUI)|
    +-----^------+          +-----^-----+
          |                        |
          +--------+  +------------+
                   |  |
                   v  v
               +---------+
               |  Common |
               |(Entities|
               | & API)  |
               +---------+





## Einrichtung und Starten des Projekts

### Voraussetzungen
- Java 17 oder höher
- Maven 3.8.3 oder höher

### Starten des Projekts
Der Server und der Client können über die IDE gestartet werden.
Der Server muss zuerst gestartet werden, bevor der Client gestartet wird, da der Client eine Verbindung zum Server aufbaut.

Die Schritte zum Starten des Projekts sind wie folgt:

1. Modul `Server` starten. Die Anwendung läuft auf Port 1299.
   Der Server kan über die Main-Methode in der Klasse `RMIServer` gestartet werden.
2. Modul `Client` starten. Der Client hat 2 Programme, die gestartet werden können:
    - `CUI` - Der Client kann über die Main-Methode in der Klasse `EShopCUI` gestartet werden.
    - `GUI` - Der Client kann über die Main-Methode in der Klasse `EShopApplication` gestartet werden.

### Troubleshooting
- Wenn der Server nicht gestartet werden kann, überprüfen Sie, ob der Port 1299 frei ist.
- Wenn der Client nicht gestartet werden kann, überprüfen Sie, ob der Server gestartet wurde.
- Falls es zu build-Fehlern kommt, überprüfen Sie, ob die richtige Java-Version verwendet wird.
- Bei weiteren build-Fehlern:
    - Löschen Sie den Ordner `target` bzw. `out` im jeweiligen Modul.
    - Führen Sie `mvn clean install` im jeweiligen Modul aus.
    - Importieren Sie das Projekt erneut in die IDE.
    - Leeren Sie den Cache der IDE und starten Sie die IDE neu.
    - Überprüfen Sie, ob nach dem Importieren bestimmter Module gefragt wird, die nicht importiert wurden.

### Dokumentation
Die Dokumentation

### Anmeldedaten
Der Client verfügt über 2 Standardkonten, die zum Testen verwendet werden können:

| Bereich     | Benutzername | Passwort |
|-------------|--------------|----------|
| Mitarbeiter | admin        | admin    |
| Kunden      | kunde        | kunde    |

## Verwendete Technologien
- Java 17
- Maven (Build-Tool)
- JavaFX (GUI)

## Autoren
- Omar El Hussein
- Maxim Kirsch
- Niclas Grüschow

#### Zuletzt bearbeitet: 26. Juli 2023 um 17 Uhr 30
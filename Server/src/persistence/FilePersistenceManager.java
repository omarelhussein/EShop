package persistence;


import domain.ArtikelService;
import domain.PersonenService;
import entities.*;
import entities.enums.EreignisTyp;
import entities.enums.KategorieEreignisTyp;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * FilePersistenceManager ist eine generische Klasse zur Verwaltung von persistenten Daten
 * in einer CSV-Datei. Diese Klasse kann jedes Objekt, das das CSVSerializable-Interface
 * implementiert, in eine Datei schreiben und aus einer Datei lesen.
 * Sie verwendet ein ReadWriteLock, um sicherzustellen, dass Lese- und Schreiboperationen
 * sicher und frei von Datenrennen durchgeführt werden.
 *
 * @param <T> der Typ des Objekts, das verwaltet werden soll. Es muss das CSVSerializable-Interface implementieren.
 */
public class FilePersistenceManager<T extends CSVSerializable> {

    private final ReadWriteLock lock;
    private final String filename;

    public FilePersistenceManager(String filename) {
        this.lock = new ReentrantReadWriteLock();
        this.filename = filename;
    }

    /**
     * Ersetzt alle Daten in der Datei durch die bereitgestellten Objekte.
     * Dieser Vorgang ist "threadsicher" und blockiert alle Leseoperationen,
     * während die Schreiboperation ausgeführt wird.
     *
     * @param objects die Objekte, die in die Datei geschrieben werden sollen
     * @throws IOException wenn ein Problem beim Schreiben der Daten auftritt
     */
    public void replaceAll(Iterable<T> objects) throws IOException {
        this.lock.writeLock().lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (T object : objects) {
                writer.write(getIdentifierForClass(object.getClass()) + "|" + object.toCSVString());
                writer.newLine();
            }
            writer.flush();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Liest alle Objekte aus der Datei und gibt eine Liste dieser Objekte zurück.
     * Diese Methode verwendet Reflexion, um neue Instanzen der bereitgestellten Klasse zu erstellen
     * und sie aus den Daten in der Datei zu befüllen.
     *
     * @return eine Liste aller aus der Datei gelesenen Objekte
     * @throws IOException wenn ein Problem beim Lesen der Daten auftritt
     */
    public List<T> readAll() throws IOException {
        lock.readLock().lock();
        List<T> objects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                String identifier = parts[0];
                String data = parts[1];
                // create instance of object parameter
                Class<? extends T> clazz = getClassForIdentifier(identifier);
                T obj = clazz.getDeclaredConstructor().newInstance();
                if (obj instanceof Ereignis) {
                    String[] tokens = data.split(";");
                    Person person = PersonenService.getInstance().getPersonByPersNr(Integer.parseInt(tokens[0]));
                    ((Ereignis) obj).setPerson(person);
                    if (tokens[1].equals("Artikel") || tokens[1].equals("Massenartikel")) {
                        Object object = ArtikelService.getInstance().getArtikelByArtNr(Integer.parseInt(tokens[2]));
                        ((Ereignis) obj).setObject(object);
                    }
                    if (tokens[1].equals("Kunde") || tokens[1].equals("Mitarbeiter")) {
                        Object object = PersonenService.getInstance().getPersonByPersNr(Integer.parseInt(tokens[2]));
                        ((Ereignis) obj).setObject(object);
                    }
                    ((Ereignis) obj).setKategorieEreignisTyp(KategorieEreignisTyp.valueOf(tokens[3]));
                    ((Ereignis) obj).setEreignisTyp(EreignisTyp.valueOf(tokens[4]));
                    ((Ereignis) obj).setDatum(LocalDateTime.parse(tokens[5]));
                    ((Ereignis) obj).setErfolg(((Ereignis) obj).stringToErfolg(tokens[6]));
                    if (!(tokens[7].equals("null")))
                        ((Ereignis) obj).setBestand(Integer.parseInt(tokens[7]));
                } else {
                    obj.fromCSVString(data);
                }
                objects.add(obj);
            }
        } catch (FileNotFoundException e) {
            // Datei existiert noch nicht
        } catch (IOException e) {
            throw new IOException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.readLock().unlock();
        }
        return objects;
    }

    // Hier die Hilfsmethoden für die Reflexion
    // Reflektion ist eine Technik, die es ermöglicht, zur Laufzeit Informationen über Klassen zu erhalten
    private static final Map<String, Class<? extends CSVSerializable>> identifierToClassMap = new HashMap<>();

    static {
        identifierToClassMap.put("MitarbeiterClass", Mitarbeiter.class);
        identifierToClassMap.put("KundeClass", Kunde.class);
        identifierToClassMap.put("ArtikelClass", Artikel.class);
        identifierToClassMap.put("MassengutClass", Massenartikel.class);
        identifierToClassMap.put("RechnungClass", Rechnung.class);
        identifierToClassMap.put("EreignisClass", Ereignis.class);
    }

    // Diese Map tauscht die Werte der obigen Map aus. So kann man mit der Klasse als Key arbeiten.
    private static final Map<Class<? extends CSVSerializable>, String> classToIdentifierMap =
            identifierToClassMap
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    /**
     * Gibt den Identifier für eine Klasse zurück mithilfe der ausgetauschten Map.
     */
    private String getIdentifierForClass(Class<? extends CSVSerializable> cls) {
        String result = classToIdentifierMap.get(cls);
        if (result == null) {
            throw new RuntimeException("Identifier für Klasse " + cls.getName() + " nicht gefunden. Ist die Klasse in der Map registriert?");
        }
        return result;
    }

    /**
     * Gibt die Klasse für einen Identifier zurück.
     */
    private Class<? extends T> getClassForIdentifier(String identifier) {
        var clazz =  identifierToClassMap.get(identifier);
        return (Class<? extends T>) clazz;
    }

}

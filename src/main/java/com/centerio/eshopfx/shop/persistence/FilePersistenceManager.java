package com.centerio.eshopfx.shop.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
                writer.write(object.toCSVString());
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
     * @param object die Klasse des Objekts, das gelesen werden soll
     * @return eine Liste aller aus der Datei gelesenen Objekte
     * @throws IOException wenn ein Problem beim Lesen der Daten auftritt
     */
    public List<T> readAll(Class<T> object) throws IOException {
        lock.readLock().lock();
        List<T> objects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // create instance of object parameter
                T obj = object.getDeclaredConstructor().newInstance();
                obj.fromCSVString(line);
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


}

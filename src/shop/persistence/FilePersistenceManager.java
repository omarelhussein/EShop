package shop.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class FilePersistenceManager<T extends CSVSerializable> {

    // Verwaltet die Synchronisation der Threads. Stellt sicher,
    // dass nicht mehrere Threads gleichzeitig auf die Datei zugreifen und somit die Daten korrumpieren.
    private final ReadWriteLock lock;
    private final Supplier<T> supplier;
    private final String filename;

    public FilePersistenceManager(Supplier<T> supplier, String filename) {
        this.lock = new ReentrantReadWriteLock();
        this.supplier = supplier;
        this.filename = filename;
    }

    public void save(T object) throws IOException {
//        this.lock.writeLock().lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(object.toCSVString());
            writer.newLine();
            writer.flush();
            writer.close();
        } finally {
//            this.lock.writeLock().unlock();
        }
    }

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

    public void delete(T object) throws IOException {
        List<T> objects = readAll();
        objects.remove(object);
        replaceAll(objects);
    }

    public void deleteAll() throws IOException {
        replaceAll(new ArrayList<>());
    }

    public void update(T object) throws IOException {
        List<T> objects = readAll();
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).equals(object)) {
                objects.set(i, object);
                break;
            }
        }
        replaceAll(objects);
    }

    public List<T> readAll() throws IOException {
        lock.readLock().lock();
        List<T> objects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                T obj = supplier.get();
                obj.fromCSVString(line);
                objects.add(obj);
            }
        } catch (FileNotFoundException e) {
            // Datei existiert noch nicht
        } finally {
            lock.readLock().unlock();
        }
        return objects;
    }


}

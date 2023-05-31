package shop.persistence;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class FilePersistenceManager<T extends CSVSerializable> {

    // Verwaltet die Synchronisation der Threads. Stellt sicher,
    // dass nicht mehrere Threads gleichzeitig auf die Datei zugreifen und somit die Daten korrumpieren.
    private final ReadWriteLock lock;
    private final String filename;

    public FilePersistenceManager(String filename) {
        this.lock = new ReentrantReadWriteLock();
        this.filename = filename;
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

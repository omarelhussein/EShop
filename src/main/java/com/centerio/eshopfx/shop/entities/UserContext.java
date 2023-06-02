package com.centerio.eshopfx.shop.entities;

/**
 * UserContext stellt eine Thread-sichere Möglichkeit dar, ein Person-Objekt
 * im Kontext des aktuellen Threads zu speichern. Diese Klasse nutzt ThreadLocal,
 * um zu gewährleisten, dass jedes Thread sein eigenes Person-Objekt hat.
 * Es kann zum Speichern von Benutzerinformationen in Webanwendungen verwendet werden,
 * bei denen jeder Thread für eine separate Anfrage zuständig ist.
 */
public class UserContext {

    /**
     * Ein ThreadLocal, das ein Person-Objekt speichert.
     * Jeder Thread hat seine eigene Instanz dieses Objekts.
     */
    private static final ThreadLocal<Person> userContext = new ThreadLocal<>();

    /**
     * Setzt das Person-Objekt für den aktuellen Thread.
     *
     * @param user das Person-Objekt, das für diesen Thread gesetzt werden soll
     */
    public static void setUser(Person user) {
        userContext.set(user);
    }

    /**
     * Gibt das Person-Objekt für den aktuellen Thread zurück.
     *
     * @return das Person-Objekt, das für diesen Thread gesetzt wurde.
     *         Kann null sein, wenn für diesen Thread kein Benutzer gesetzt wurde.
     */
    public static Person getUser() {
        return userContext.get();
    }

    /**
     * Entfernt das Person-Objekt aus dem aktuellen Thread.
     * Nach dem Aufruf dieser Methode gibt getUser() für diesen Thread null zurück.
     */
    public static void clearUser() {
        userContext.remove();
    }

}

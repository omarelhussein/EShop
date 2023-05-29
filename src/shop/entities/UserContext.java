package shop.entities;

public class UserContext {

    private static final ThreadLocal<Person> userContext = new ThreadLocal<>();

    public static void setUser(Person user) {
        userContext.set(user);
    }

    public static Person getUser() {
        return userContext.get();
    }

    public static void clearUser() {
        userContext.remove();
    }

}

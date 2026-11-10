package campus.lostfound.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public final class DB {
    private static final Properties PROPS = new Properties();
    static {
        try (InputStream in = DB.class.getResourceAsStream("/db.properties")) {
            if (in == null) throw new IllegalStateException(
                "Missing db.properties. Copy db.properties.example to db.properties and configure it.");
            PROPS.load(in);
        } catch (Exception e) { throw new ExceptionInInitializerError(e); }
    }
    private DB() {}
    public static Connection getConnection() throws java.sql.SQLException {
        return DriverManager.getConnection(PROPS.getProperty("db.url"),
            PROPS.getProperty("db.username"), PROPS.getProperty("db.password"));
    }
}

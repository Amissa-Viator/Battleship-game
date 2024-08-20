package gamefiles.database.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static final String PASSWORD = PropertiesUtil.get("db.password");
    private static final String USERNAME = PropertiesUtil.get("db.username");
    private static final String URL = PropertiesUtil.get("db.url");
    private static Connection connection;
    private ConnectionManager() {}

    static {
        try {
            Class.forName("org.postgresql.Driver");
            open();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection get() {
        return connection;
    }

    public static void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void open() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

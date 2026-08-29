package it.ispwproject.myvet.dao;

import it.ispwproject.myvet.enumerator.Role;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private static Connection connection;
    private static Role currentRole;

    private static final String PROPERTIES_FILE =
            "src/main/resources/db.properties";

    private static final Properties properties = new Properties();

    private ConnectionFactory() {
    }

    static {
        try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "Impossibile caricare db.properties"
            );
        }
    }

    private static void initConnection() throws SQLException {
        String url = properties.getProperty("CONNECTION_URL");

        String user;
        String password;

        if (currentRole != null) {
            user = properties.getProperty(currentRole.name() + "_USER");
            password = properties.getProperty(currentRole.name() + "_PASS");
        } else {
            // Prima del login viene utilizzato l'utente con permessi minimi.
            user = properties.getProperty("LOGIN_USER");
            password = properties.getProperty("LOGIN_PASS");
        }

        if (url == null || user == null || password == null) {
            throw new SQLException(
                    "Configurazione del database mancante per il ruolo: "
                            + currentRole
            );
        }

        connection = DriverManager.getConnection(url, user, password);
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initConnection();
        }

        return connection;
    }

    public static void changeRole(Role role) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }

        currentRole = role;
        initConnection();
    }

    public static void clearRole() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }

        connection = null;
        currentRole = null;
    }
}
package storage_db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static String url;
    private static String user;
    private static String password;
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());

    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("dbconfig.properties")) {
            if (input == null) {
                handleConfigError("File dbconfig.properties non trovato nel classpath.", null);
            }
            Properties properties = new Properties();
            properties.load(input);
            url = properties.getProperty("db.url");
            user = properties.getProperty("db.user");
            password = properties.getProperty("db.password");
            logger.info("Configurazione database caricata con successo.");
        } catch (IOException e) {
            handleConfigError("Errore nel caricamento del file di configurazione: dbconfig.properties", e);
        }
    }

    private DatabaseConnection() {
        logger.fine("Costruttore di DatabaseConnection chiamato.");
    }

    public static Connection getConnection() throws SQLException {
        try {
            logger.fine("Tentativo di caricamento del driver JDBC...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.fine("Driver JDBC caricato correttamente.");
        } catch (ClassNotFoundException e) {
            handleDriverError(e);
        }

        try {
            logger.info("Tentativo di connessione al database...");
            Connection conn = DriverManager.getConnection(url, user, password);
            logger.info("Connessione al database riuscita.");
            return conn;
        } catch (SQLException e) {
            handleConnectionError(e);
        }

        return null; // Non raggiunto grazie alle eccezioni
    }

    // Metodi helper per la gestione centralizzata degli errori
    private static void handleConfigError(String message, Throwable cause) {
        String errorMsg = (cause == null) ? message : message + " - " + cause.getMessage();
        logger.log(Level.SEVERE, errorMsg, cause);
        throw new ConfigurationLoadException(errorMsg, cause);
    }

    private static void handleDriverError(ClassNotFoundException e) throws SQLException {
        String errorMsg = "Driver JDBC non trovato: " + e.getMessage();
        logger.log(Level.SEVERE, errorMsg, e);
        throw new SQLException(errorMsg, e);
    }

    private static void handleConnectionError(SQLException e) throws SQLException {
        String errorMsg = "Errore durante la connessione al database: " + e.getMessage();
        logger.log(Level.SEVERE, errorMsg, e);
        throw new SQLException(errorMsg, e);
    }

    // Eccezione custom per errori di configurazione
    public static class ConfigurationLoadException extends RuntimeException {
        public ConfigurationLoadException(String message) {
            super(message);
        }

        public ConfigurationLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
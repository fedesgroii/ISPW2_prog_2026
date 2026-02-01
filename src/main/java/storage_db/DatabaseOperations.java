package storage_db;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseOperations {
    private static final Logger logger = Logger.getLogger(DatabaseOperations.class.getName());

    // Costruttore privato per impedire la creazione di istanze della classe
    private DatabaseOperations() {}

    /**
     * Esegue una query SQL di selezione e restituisce i risultati formattati.
     *
     * @param sql la query SQL con eventuali parametri segnaposto (?)
     * @param params i valori da sostituire nei segnaposto
     * @return Lista di mappe contenenti coppie nome-colonna/valore
     * @throws DatabaseException in caso di errori durante l'esecuzione
     */
    public static List<Map<String, Object>> executeQuery(String sql, Object... params) throws DatabaseException {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            handleSQLException("Errore esecuzione query", sql, params, e);
        }
        return results;
    }

    /**
     * Esegue un'operazione SQL di aggiornamento (INSERT/UPDATE/DELETE).
     *
     * @param sql il comando SQL con eventuali parametri segnaposto (?)
     * @param params i valori da sostituire nei segnaposto
     * @return Il numero di righe interessate
     * @throws DatabaseException in caso di errori durante l'esecuzione
     */
    public static int executeUpdate(String sql, Object... params) throws DatabaseException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            handleSQLException("Errore esecuzione update", sql, params, e);
            return 0; // Questo valore non verrà mai restituito perché l'eccezione viene rilanciata
        }
    }

    /**
     * Imposta i parametri su uno PreparedStatement.
     *
     * @param stmt lo PreparedStatement su cui impostare i parametri
     * @param params i valori da impostare
     * @throws SQLException in caso di errori durante l'impostazione
     */
    private static void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    /**
     * Gestisce una transazione composta da più operazioni.
     *
     * @param operations lista di operazioni da eseguire nella transazione
     * @throws DatabaseException in caso di errori o rollback
     */
    public static void executeTransaction(List<Runnable> operations) throws DatabaseException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            for (Runnable op : operations) {
                op.run();
            }
            conn.commit();
        } catch (SQLException e) {
            rollbackTransaction(conn, e);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Rollback della transazione in caso di errore.
     *
     * @param conn la connessione al database
     * @param e l'eccezione generata
     * @throws DatabaseException sempre per segnalare il problema
     */
    private static void rollbackTransaction(Connection conn, SQLException e) throws DatabaseException {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                handleSQLException("Errore durante il rollback", null, null, ex);
            }
        }
        handleSQLException("Transazione fallita", null, null, e);
    }

    /**
     * Chiude la connessione gestendo eventuali eccezioni.
     *
     * @param conn la connessione da chiudere
     */
    private static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Errore chiusura connessione: {0}", e.getMessage());
            }
        }
    }

    /**
     * Formatta i parametri per il logging.
     *
     * @param params i parametri da formattare
     * @return una stringa rappresentante i parametri
     */
    private static String formatParams(Object... params) {
        if (params == null || params.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < params.length; i++) {
            sb.append(params[i]);
            if (i < params.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Gestisce le eccezioni SQL loggando e rilanciando un'eccezione personalizzata.
     *
     * @param message Messaggio di errore contestuale
     * @param sql Query SQL coinvolta (può essere null)
     * @param params Parametri utilizzati nella query (può essere null)
     * @param e Eccezione originale
     * @throws DatabaseException sempre per segnalare il problema
     */
    private static void handleSQLException(String message, String sql, Object[] params, SQLException e) throws DatabaseException {
        String errorMsg = message + (sql != null ? " - Query: " + sql + ", Parametri: " + formatParams(params) : "") + ". Dettagli: " + e.getMessage();
        logger.log(Level.SEVERE, e, () -> errorMsg);
        throw new DatabaseException(errorMsg, e);
    }

    /**
     * Eccezione personalizzata per errori del database.
     */
    public static class DatabaseException extends Exception {
        public DatabaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

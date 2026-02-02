package storage_db;

import model.Paziente;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseStorageStrategyPaziente implements DataStorageStrategy<Paziente> {
    private static final Logger logger = Logger.getLogger(DatabaseStorageStrategyPaziente.class.getName());

    // Query SQL con nomi di colonne corretti
    private static final String INSERT_QUERY = "INSERT INTO pazienti (nome, cognome, dataDiNascita, numeroTelefonico, email, numeroTesseraSanitaria, condizioniMediche, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_QUERY = "SELECT numeroTesseraSanitaria, nome, cognome, dataDiNascita, numeroTelefonico, email, condizioniMediche, password FROM pazienti WHERE numeroTesseraSanitaria=?";
    private static final String UPDATE_QUERY = "UPDATE pazienti SET nome = ?, cognome = ?, dataDiNascita = ?, numeroTelefonico = ?, email = ?, condizioniMediche = ?, password = ? WHERE numeroTesseraSanitaria = ?";
    private static final String DELETE_QUERY = "DELETE FROM pazienti WHERE numeroTesseraSanitaria = ?";
    private static final String SELECT_ALL_QUERY = "SELECT numeroTesseraSanitaria, nome, cognome, dataDiNascita, numeroTelefonico, email, condizioniMediche, password FROM pazienti";

    // Definisci una costante per la stringa "Paziente non può essere null"
    private static final String PAZIENTE_NOT_NULL_MESSAGE = "Paziente non può essere null";

    @Override
    public boolean salva(Paziente paziente) {
        Objects.requireNonNull(paziente, PAZIENTE_NOT_NULL_MESSAGE);
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY)) {
            conn.setAutoCommit(true); // ✅ Abilita il commit automatico
            setPazienteParameters(stmt, paziente);

            // ✅ Log dei dati prima dell'inserimento
            logger.log(Level.INFO, () -> "Tentativo di inserimento paziente: " + paziente);

            return stmt.executeUpdate() > 0; // ✅ Controlla se almeno una riga è stata inserita
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e,
                    () -> "Errore durante l'inserimento del paziente: " + paziente.getCodiceFiscalePaziente());
            return false;
        }
    }

    @Override
    public Optional<Paziente> trova(Paziente paziente) {
        Objects.requireNonNull(paziente, PAZIENTE_NOT_NULL_MESSAGE);
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_QUERY)) {
            stmt.setString(1, paziente.getCodiceFiscalePaziente());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPaziente(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e,
                    () -> "Errore durante la ricerca del paziente: " + paziente.getCodiceFiscalePaziente());
        }
        return Optional.empty();
    }

    @Override
    public boolean aggiorna(Paziente paziente) {
        Objects.requireNonNull(paziente, PAZIENTE_NOT_NULL_MESSAGE);
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(UPDATE_QUERY)) {
            setPazienteParameters(stmt, paziente);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e,
                    () -> "Errore durante l'aggiornamento del paziente: " + paziente.getCodiceFiscalePaziente());
            return false;
        }
    }

    @Override
    public boolean elimina(Paziente paziente) {
        Objects.requireNonNull(paziente, PAZIENTE_NOT_NULL_MESSAGE);
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(DELETE_QUERY)) {
            stmt.setString(1, paziente.getCodiceFiscalePaziente());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e,
                    () -> "Errore durante l'eliminazione del paziente: " + paziente.getCodiceFiscalePaziente());
            return false;
        }
    }

    public List<Paziente> getAllPazienti() {
        List<Paziente> pazienti = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_QUERY);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                pazienti.add(mapResultSetToPaziente(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il recupero dei pazienti", e);
        }
        return pazienti;
    }

    private void setPazienteParameters(PreparedStatement stmt, Paziente paziente) throws SQLException {
        stmt.setString(1, paziente.getNome());
        stmt.setString(2, paziente.getCognome());
        // ✅ Usa Timestamp se la colonna nel DB è DATETIME
        stmt.setTimestamp(3, Timestamp.valueOf(paziente.getDataDiNascita().atStartOfDay()));
        stmt.setString(4, paziente.getNumeroTelefonico());
        stmt.setString(5, paziente.getEmail());
        stmt.setString(6, paziente.getCodiceFiscalePaziente()); // Assicurati che sia il codice giusto
        stmt.setString(7, paziente.getCondizioniMediche());
        stmt.setString(8, paziente.getPassword());
    }

    private Paziente mapResultSetToPaziente(ResultSet rs) throws SQLException {
        return new Paziente.Builder()
                .nome(rs.getString("nome"))
                .cognome(rs.getString("cognome"))
                .dataDiNascita(rs.getDate("dataDiNascita").toLocalDate())
                .numeroTelefonico(rs.getString("numeroTelefonico"))
                .email(rs.getString("email"))
                .codiceFiscalePaziente(rs.getString("numeroTesseraSanitaria"))
                .condizioniMediche(rs.getString("condizioniMediche"))
                .password(rs.getString("password"))
                .build();
    }

    @Override
    public Optional<Paziente> findByEmail(String email) {
        String query = "SELECT numeroTesseraSanitaria, nome, cognome, dataDiNascita, numeroTelefonico, email, condizioniMediche, password FROM pazienti WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPaziente(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e, () -> "Errore durante la ricerca del paziente per email: " + email);
        }
        return Optional.empty();
    }
}
package storage_db;

import model.Specialista;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseStorageStrategySpecialista implements DataStorageStrategy<Specialista> {
    private static final Logger logger = Logger.getLogger(DatabaseStorageStrategySpecialista.class.getName());

    // Definisci una costante per la stringa "Specialista non può essere null"
    private static final String SPECIALISTA_NOT_NULL_MESSAGE = "Specialista non può essere null";

    // Query SQL con nomi di colonne corretti
    private static final String INSERT_QUERY = "INSERT INTO specialista (nome, cognome, dataDiNascita, numeroTelefonico, email, specializzazione, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_QUERY = "SELECT nome, cognome, dataDiNascita, numeroTelefonico, email, specializzazione, password FROM specialista WHERE nome = ? AND cognome = ? AND email = ?";
    private static final String UPDATE_QUERY = "UPDATE specialista SET dataDiNascita = ?, numeroTelefonico = ?, specializzazione = ?, password = ? WHERE nome = ? AND cognome = ? AND email = ?";
    private static final String DELETE_QUERY = "DELETE FROM specialista WHERE nome = ? AND cognome = ? AND email = ?";
    private static final String SELECT_ALL_QUERY = "SELECT nome, cognome, dataDiNascita, numeroTelefonico, email, specializzazione, password FROM specialisti";

    @Override
    public boolean salva(Specialista specialista) {
        Objects.requireNonNull(specialista, SPECIALISTA_NOT_NULL_MESSAGE);
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY)) {
            setSpecialistaParameters(stmt, specialista);
            logger.info(() -> "Tentativo di inserimento specialista: " + specialista);
            return stmt.executeUpdate() > 0; // Controlla se almeno una riga è stata inserita
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e,
                    () -> "Errore durante l'inserimento dello specialista: " + specialista.getNome());
            return false;
        }
    }

    @Override
    public Optional<Specialista> trova(Specialista specialista) {
        Objects.requireNonNull(specialista, SPECIALISTA_NOT_NULL_MESSAGE);
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_QUERY)) {
            stmt.setString(1, specialista.getNome());
            stmt.setString(2, specialista.getCognome());
            stmt.setString(3, specialista.getEmail());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSpecialista(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e, () -> "Errore durante la ricerca dello specialista: " + specialista.getNome());
        }
        return Optional.empty();
    }

    @Override
    public boolean aggiorna(Specialista specialista) {
        Objects.requireNonNull(specialista, SPECIALISTA_NOT_NULL_MESSAGE);
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(UPDATE_QUERY)) {
            setSpecialistaParametersForUpdate(stmt, specialista);
            stmt.setString(5, specialista.getNome());
            stmt.setString(6, specialista.getCognome());
            stmt.setString(7, specialista.getEmail());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e,
                    () -> "Errore durante l'aggiornamento dello specialista: " + specialista.getNome());
            return false;
        }
    }

    @Override
    public boolean elimina(Specialista specialista) {
        Objects.requireNonNull(specialista, SPECIALISTA_NOT_NULL_MESSAGE);
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(DELETE_QUERY)) {
            stmt.setString(1, specialista.getNome());
            stmt.setString(2, specialista.getCognome());
            stmt.setString(3, specialista.getEmail());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e,
                    () -> "Errore durante l'eliminazione dello specialista: " + specialista.getNome());
            return false;
        }
    }

    /**
     * Recupera tutti gli specialisti dal database.
     */
    public List<Specialista> getAllSpecialisti() {
        List<Specialista> specialisti = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_QUERY);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                specialisti.add(mapResultSetToSpecialista(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il recupero degli specialisti", e);
        }
        return specialisti;
    }

    /**
     * Imposta i parametri per lo specialista nel PreparedStatement durante
     * l'inserimento.
     */
    private void setSpecialistaParameters(PreparedStatement stmt, Specialista specialista) throws SQLException {
        stmt.setString(1, specialista.getNome());
        stmt.setString(2, specialista.getCognome());
        stmt.setDate(3, Date.valueOf(specialista.getDataDiNascita()));
        stmt.setString(4, specialista.getNumeroTelefonico());
        stmt.setString(5, specialista.getEmail());
        stmt.setString(6, specialista.getSpecializzazione());
        stmt.setString(7, specialista.getPassword());
    }

    /**
     * Imposta i parametri per lo specialista nel PreparedStatement durante
     * l'aggiornamento.
     */
    private void setSpecialistaParametersForUpdate(PreparedStatement stmt, Specialista specialista)
            throws SQLException {
        stmt.setDate(1, Date.valueOf(specialista.getDataDiNascita()));
        stmt.setString(2, specialista.getNumeroTelefonico());
        stmt.setString(3, specialista.getSpecializzazione());
        stmt.setString(4, specialista.getPassword());
    }

    /**
     * Mappa un ResultSet a un oggetto Specialista.
     */
    private Specialista mapResultSetToSpecialista(ResultSet rs) throws SQLException {
        return new Specialista.Builder()
                .nome(rs.getString("nome"))
                .cognome(rs.getString("cognome"))
                .dataDiNascita(rs.getDate("dataDiNascita").toLocalDate())
                .numeroTelefonico(rs.getString("numeroTelefonico"))
                .email(rs.getString("email"))
                .specializzazione(rs.getString("specializzazione"))
                .password(rs.getString("password"))
                .build();
    }

    @Override
    public Optional<Specialista> findByEmail(String email) {
        String query = "SELECT nome, cognome, dataDiNascita, numeroTelefonico, email, specializzazione, password FROM specialista WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSpecialista(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante la ricerca dello specialista per email: " + email, e);
        }
        return Optional.empty();
    }
}
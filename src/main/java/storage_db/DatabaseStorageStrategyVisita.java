package storage_db;

import model.Visita;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseStorageStrategyVisita implements DataStorageStrategy<Visita> {
    private static final Logger logger = Logger.getLogger(DatabaseStorageStrategyVisita.class.getName());

    private static final String VISITA_NOT_NULL_MESSAGE = "Visita non può essere null";

    // Query SQL come costanti (Simplified: no JOINs)
    private static final String INSERT_QUERY = "INSERT INTO visite (paziente_codice_fiscale, specialista_id, data, orario, tipo_visita, motivo_visita, stato) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_QUERY = "SELECT * FROM visite WHERE paziente_codice_fiscale=? AND specialista_id=? AND data=? AND orario=?";
    private static final String UPDATE_QUERY = "UPDATE visite SET tipo_visita = ?, motivo_visita = ?, stato = ? WHERE paziente_codice_fiscale = ? AND specialista_id = ? AND data = ? AND orario = ?";
    private static final String DELETE_QUERY = "DELETE FROM visite WHERE paziente_codice_fiscale = ? AND specialista_id = ? AND data = ? AND orario = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM visite";
    private static final String SELECT_BY_DATE_AND_SPEC_QUERY = "SELECT * FROM visite WHERE data=? AND specialista_id=?";

    @Override
    public boolean salva(Visita visita) {
        Objects.requireNonNull(visita, VISITA_NOT_NULL_MESSAGE);
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY)) {
            stmt.setString(1, visita.getPazienteCodiceFiscale());
            stmt.setInt(2, visita.getSpecialistaId());
            stmt.setObject(3, visita.getData());
            stmt.setObject(4, visita.getOrario());
            stmt.setString(5, visita.getTipoVisita());
            stmt.setString(6, visita.getMotivoVisita());
            stmt.setString(7, visita.getStato());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e, () -> "Errore durante l'inserimento della visita per paziente: "
                    + visita.getPazienteCodiceFiscale());
            return false;
        }
    }

    @Override
    public Optional<Visita> trova(Visita visita) {
        Objects.requireNonNull(visita, VISITA_NOT_NULL_MESSAGE);
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_QUERY)) {
            setKeyParameters(stmt, 1, visita);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVisita(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e, () -> "Errore durante la ricerca della visita per paziente: "
                    + visita.getPazienteCodiceFiscale());
        }
        return Optional.empty();
    }

    @Override
    public boolean aggiorna(Visita visita) {
        Objects.requireNonNull(visita, VISITA_NOT_NULL_MESSAGE);
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(UPDATE_QUERY)) {
            stmt.setString(1, visita.getTipoVisita());
            stmt.setString(2, visita.getMotivoVisita());
            stmt.setString(3, visita.getStato());
            setKeyParameters(stmt, 4, visita);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e, () -> "Errore durante l'aggiornamento della visita per paziente: "
                    + visita.getPazienteCodiceFiscale());
            return false;
        }
    }

    @Override
    public boolean elimina(Visita visita) {
        Objects.requireNonNull(visita, VISITA_NOT_NULL_MESSAGE);
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(DELETE_QUERY)) {
            setKeyParameters(stmt, 1, visita);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e, () -> "Errore durante l'eliminazione della visita per paziente: "
                    + visita.getPazienteCodiceFiscale());
            return false;
        }
    }

    public List<Visita> findBySpecialistId(int specialistaId) {
        List<Visita> visite = new ArrayList<>();
        String query = "SELECT * FROM visite WHERE specialista_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, specialistaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    visite.add(mapResultSetToVisita(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il recupero delle visite per specialista: " + specialistaId, e);
        }
        return visite;
    }

    public List<Visita> findByDateAndSpecialist(LocalDate data, int specialistaId) {
        List<Visita> visite = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_BY_DATE_AND_SPEC_QUERY)) {
            stmt.setObject(1, data);
            stmt.setInt(2, specialistaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    visite.add(mapResultSetToVisita(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il recupero delle visite per data e specialista", e);
        }
        return visite;
    }

    private void setKeyParameters(PreparedStatement stmt, int startIndex, Visita visita) throws SQLException {
        stmt.setString(startIndex, visita.getPazienteCodiceFiscale());
        stmt.setInt(startIndex + 1, visita.getSpecialistaId());
        stmt.setObject(startIndex + 2, visita.getData());
        stmt.setObject(startIndex + 3, visita.getOrario());
    }

    private Visita mapResultSetToVisita(ResultSet rs) throws SQLException {
        return new Visita(
                rs.getString("paziente_codice_fiscale"),
                rs.getObject("data", LocalDate.class),
                rs.getObject("orario", LocalTime.class),
                rs.getInt("specialista_id"),
                rs.getString("tipo_visita"),
                rs.getString("motivo_visita"),
                rs.getString("stato"));
    }

    public boolean isVisitaDisponibileInDatabase(LocalDate data, LocalTime orario, int specialistId) {
        if (data == null || orario == null) {
            logger.warning("Dati non validi per la verifica della visita.");
            return false;
        }
        final String SELECT_BY_DATE_ORARIO_QUERY = "SELECT 1 FROM visite WHERE data=? AND orario=? AND specialista_id=? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_BY_DATE_ORARIO_QUERY)) {

            stmt.setObject(1, data);
            stmt.setObject(2, orario);
            stmt.setInt(3, specialistId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return false;
                }
            }
        } catch (SQLException _) {
            logger.severe("Errore durante la verifica della disponibilità della visita");
            return false;
        }
        return true;
    }

    @Override
    public List<Visita> getAllInstanceOfActor() {
        List<Visita> visite = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_QUERY);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                visite.add(mapResultSetToVisita(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il recupero delle visite", e);
        }
        return visite;
    }

    @Override
    public Optional<Visita> findByEmail(String email) {
        return Optional.empty();
    }
}

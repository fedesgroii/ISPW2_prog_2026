package storage_db;

import model.Paziente;
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

    // Query SQL come costanti
    private static final String INSERT_QUERY = "INSERT INTO visite (paziente_codice_fiscale, data, orario, specialista, tipo_visita, motivo_visita, stato) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_QUERY = "SELECT paziente_codice_fiscale, data, orario, specialista, tipo_visita, motivo_visita, stato FROM visite WHERE paziente_codice_fiscale=? AND data=? AND orario=?";
    private static final String UPDATE_QUERY = "UPDATE visite SET specialista = ?, tipo_visita = ?, motivo_visita = ?, stato = ? WHERE paziente_codice_fiscale = ? AND data = ? AND orario = ?";
    private static final String DELETE_QUERY = "DELETE FROM visite WHERE paziente_codice_fiscale = ? AND data = ? AND orario = ?";
    private static final String SELECT_ALL_QUERY = "SELECT paziente_codice_fiscale, data, orario, specialista, tipo_visita, motivo_visita, stato FROM visite";
    private static final String SELECT_BY_DATE_AND_SPEC_QUERY = "SELECT paziente_codice_fiscale, data, orario, specialista, tipo_visita, motivo_visita, stato FROM visite WHERE data=? AND specialista=?";

    @Override
    public boolean salva(Visita visita) {
        Objects.requireNonNull(visita, VISITA_NOT_NULL_MESSAGE);
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY)) {
            stmt.setString(1, visita.getPaziente().getCodiceFiscalePaziente());
            stmt.setObject(2, visita.getData());
            stmt.setObject(3, visita.getOrario());
            stmt.setString(4, visita.getSpecialista());
            stmt.setString(5, visita.getTipoVisita());
            stmt.setString(6, visita.getMotivoVisita());
            stmt.setString(7, visita.getStato());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e, () -> "Errore durante l'inserimento della visita per paziente: "
                    + visita.getPaziente().getCodiceFiscalePaziente());
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
                    return Optional.of(mapResultSetToVisita(rs, visita.getPaziente()));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e, () -> "Errore durante la ricerca della visita per paziente: "
                    + visita.getPaziente().getCodiceFiscalePaziente());
        }
        return Optional.empty();
    }

    @Override
    public boolean aggiorna(Visita visita) {
        Objects.requireNonNull(visita, VISITA_NOT_NULL_MESSAGE);
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(UPDATE_QUERY)) {
            stmt.setString(1, visita.getSpecialista());
            stmt.setString(2, visita.getTipoVisita());
            stmt.setString(3, visita.getMotivoVisita());
            stmt.setString(4, visita.getStato());
            setKeyParameters(stmt, 5, visita);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e, () -> "Errore durante l'aggiornamento della visita per paziente: "
                    + visita.getPaziente().getCodiceFiscalePaziente());
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
                    + visita.getPaziente().getCodiceFiscalePaziente());
            return false;
        }
    }

    public List<Visita> getAllVisite() {
        List<Visita> visite = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_QUERY);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                visite.add(mapResultSetToVisita(rs, null));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il recupero delle visite", e);
        }
        return visite;
    }

    public List<Visita> findByDateAndSpecialist(LocalDate data, String specialista) {
        List<Visita> visite = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_BY_DATE_AND_SPEC_QUERY)) {
            stmt.setObject(1, data);
            stmt.setString(2, specialista);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    visite.add(mapResultSetToVisita(rs, null));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il recupero delle visite per data e specialista", e);
        }
        return visite;
    }

    private void setKeyParameters(PreparedStatement stmt, int startIndex, Visita visita) throws SQLException {
        stmt.setString(startIndex, visita.getPaziente().getCodiceFiscalePaziente());
        stmt.setObject(startIndex + 1, visita.getData());
        stmt.setObject(startIndex + 2, visita.getOrario());
    }

    private Visita mapResultSetToVisita(ResultSet rs, Paziente paziente) throws SQLException {
        return new Visita(
                (paziente != null) ? paziente
                        : new Paziente.Builder()
                                .codiceFiscalePaziente(rs.getString("paziente_codice_fiscale"))
                                .email("placeholder@email.com") // Mandatory fields for Builder
                                .password("placeholder")
                                .build(),
                rs.getObject("data", LocalDate.class),
                rs.getObject("orario", LocalTime.class),
                rs.getString("specialista"),
                rs.getString("tipo_visita"),
                rs.getString("motivo_visita"),
                rs.getString("stato"));
    }

    public boolean isVisitaDisponibileInDatabase(LocalDate data, LocalTime orario) {
        if (data == null || orario == null) {
            logger.warning("Dati non validi per la verifica della visita.");
            return false;
        }
        final String SELECT_BY_DATE_ORARIO_QUERY = "SELECT 1 FROM visite WHERE data=? AND orario=? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_BY_DATE_ORARIO_QUERY)) {

            stmt.setObject(1, data);
            stmt.setObject(2, orario);

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
        return getAllVisite();
    }

    @Override
    public Optional<Visita> findByEmail(String email) {
        // Non ha senso cercare una visita per email (non è un utente)
        return Optional.empty();
    }
}

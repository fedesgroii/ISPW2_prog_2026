package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Entity class for a medical visit (Visita).
 */
public class Visita {
    private final Paziente paziente;
    private final LocalDate data;
    private final LocalTime orario;
    private final String specialista;
    private final String tipoVisita;
    private final String motivoVisita;
    private final String stato;

    public Visita(Paziente paziente, LocalDate data, LocalTime orario, String specialista, String tipoVisita,
            String motivoVisita, String stato) {
        this.paziente = Objects.requireNonNull(paziente, "Paziente è obbligatorio");
        this.data = Objects.requireNonNull(data, "Data è obbligatoria");
        this.orario = Objects.requireNonNull(orario, "Orario è obbligatorio");
        this.specialista = specialista;
        this.tipoVisita = tipoVisita;
        this.motivoVisita = motivoVisita;
        this.stato = stato;
    }

    // Getters
    public Paziente getPaziente() {
        return paziente;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getOrario() {
        return orario;
    }

    public String getSpecialista() {
        return specialista;
    }

    public String getTipoVisita() {
        return tipoVisita;
    }

    public String getMotivoVisita() {
        return motivoVisita;
    }

    public String getStato() {
        return stato;
    }

    @Override
    public String toString() {
        return "Visita{" +
                "paziente=" + paziente.getCodiceFiscalePaziente() +
                ", data=" + data +
                ", orario=" + orario +
                ", specialista='" + specialista + '\'' +
                ", stato='" + stato + '\'' +
                '}';
    }
}

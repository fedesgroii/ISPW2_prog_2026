package model;

import java.time.LocalDate;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity class for a medical visit (Visita), strictly aligned with the database
 * schema.
 */
public class Visita {
    @JsonProperty("paziente_codice_fiscale")
    private String pazienteCodiceFiscale;

    @JsonProperty("specialista_id")
    private int specialistaId;

    @JsonProperty("data")
    private LocalDate data;

    @JsonProperty("orario")
    private LocalTime orario;

    @JsonProperty("tipo_visita")
    private String tipoVisita;

    @JsonProperty("motivo_visita")
    private String motivoVisita;

    @JsonProperty("stato")
    private String stato;

    @JsonCreator
    public Visita(
            @JsonProperty("paziente_codice_fiscale") String pazienteCodiceFiscale,
            @JsonProperty("data") LocalDate data,
            @JsonProperty("orario") LocalTime orario,
            @JsonProperty("specialista_id") int specialistaId,
            @JsonProperty("tipo_visita") String tipoVisita,
            @JsonProperty("motivo_visita") String motivoVisita,
            @JsonProperty("stato") String stato) {
        this.pazienteCodiceFiscale = pazienteCodiceFiscale;
        this.data = data;
        this.orario = orario;
        this.specialistaId = specialistaId;
        this.tipoVisita = tipoVisita;
        this.motivoVisita = motivoVisita;
        this.stato = stato;
    }

    // Getters and Setters
    public String getPazienteCodiceFiscale() {
        return pazienteCodiceFiscale;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getOrario() {
        return orario;
    }

    public int getSpecialistaId() {
        return specialistaId;
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
                "paziente='" + pazienteCodiceFiscale + '\'' +
                ", data=" + data +
                ", orario=" + orario +
                ", specialistaId=" + specialistaId +
                ", stato='" + stato + '\'' +
                '}';
    }
}

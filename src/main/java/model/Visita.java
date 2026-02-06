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
    private String tipo_visita;

    @JsonProperty("motivo_visita")
    private String motivo_visita;

    @JsonProperty("stato")
    private String stato;

    @JsonCreator
    public Visita(
            @JsonProperty("paziente_codice_fiscale") String pazienteCodiceFiscale,
            @JsonProperty("data") LocalDate data,
            @JsonProperty("orario") LocalTime orario,
            @JsonProperty("specialista_id") int specialistaId,
            @JsonProperty("tipo_visita") String tipo_visita,
            @JsonProperty("motivo_visita") String motivo_visita,
            @JsonProperty("stato") String stato) {
        this.pazienteCodiceFiscale = pazienteCodiceFiscale;
        this.data = data;
        this.orario = orario;
        this.specialistaId = specialistaId;
        this.tipo_visita = tipo_visita;
        this.motivo_visita = motivo_visita;
        this.stato = stato;
    }

    // Getters and Setters
    public String getPazienteCodiceFiscale() {
        return pazienteCodiceFiscale;
    }

    public void setPazienteCodiceFiscale(String pazienteCodiceFiscale) {
        this.pazienteCodiceFiscale = pazienteCodiceFiscale;
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

    public void setOrario(LocalTime orario) {
        this.orario = orario;
    }

    public int getSpecialistaId() {
        return specialistaId;
    }

    public void setSpecialistaId(int specialistaId) {
        this.specialistaId = specialistaId;
    }

    public String getTipo_visita() {
        return tipo_visita;
    }

    public void setTipo_visita(String tipo_visita) {
        this.tipo_visita = tipo_visita;
    }

    public String getMotivo_visita() {
        return motivo_visita;
    }

    public void setMotivo_visita(String motivo_visita) {
        this.motivo_visita = motivo_visita;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
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

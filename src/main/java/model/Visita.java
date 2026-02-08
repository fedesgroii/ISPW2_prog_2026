package model;

import java.time.LocalDate;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import observer.Observer;
import observer.Subject;

/**
 * Entity class for a medical visit (Visita), strictly aligned with the database
 * schema.
 * Acts as a ConcreteSubject in the Observer pattern.
 */
public class Visita implements Subject {
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

    private final List<Observer> observers = new ArrayList<>();

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

    // --- Subject implementation ---
    @Override
    public void attach(Observer o) {
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void detach(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : new ArrayList<>(observers)) { // Copy to avoid ConcurrentModificationException
            o.update();
        }
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

    public void setStato(String stato) {
        this.stato = stato;
        notifyObservers();
    }

    /**
     * Confirms the visit and notifies observers.
     */
    public void confirm() {
        this.stato = "Confermata";
        notifyObservers();
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

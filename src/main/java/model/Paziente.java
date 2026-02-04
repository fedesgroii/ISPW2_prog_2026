package model;

import java.time.LocalDate;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entità che rappresenta un Paziente nel sistema.
 * 
 * Database: tabella `pazienti`
 * - PK: numeroTesseraSanitaria
 * - Autenticazione: email + password
 */
public class Paziente {
    private final String numeroTesseraSanitaria; // Chiave primaria / Codice Fiscale
    private final String nome;
    private final String cognome;
    private final LocalDate dataDiNascita;
    private final String numeroTelefonico;
    private final String email; // Username per autenticazione
    private final String condizioniMediche;
    private final String password; // Password per autenticazione

    // Costruttore annotato per Jackson
    @JsonCreator
    public Paziente(
            @JsonProperty("numeroTesseraSanitaria") String numeroTesseraSanitaria,
            @JsonProperty("nome") String nome,
            @JsonProperty("cognome") String cognome,
            @JsonProperty("dataDiNascita") LocalDate dataDiNascita,
            @JsonProperty("numeroTelefonico") String numeroTelefonico,
            @JsonProperty("email") String email,
            @JsonProperty("condizioniMediche") String condizioniMediche,
            @JsonProperty("password") String password) {
        this.numeroTesseraSanitaria = numeroTesseraSanitaria;
        this.nome = nome;
        this.cognome = cognome;
        this.dataDiNascita = dataDiNascita;
        this.numeroTelefonico = numeroTelefonico;
        this.email = email;
        this.condizioniMediche = condizioniMediche;
        this.password = password;
    }

    // Costruttore privato (si usa il Builder)
    private Paziente(Builder builder) {
        this.numeroTesseraSanitaria = builder.numeroTesseraSanitaria;
        this.nome = builder.nome;
        this.cognome = builder.cognome;
        this.dataDiNascita = builder.dataDiNascita;
        this.numeroTelefonico = builder.numeroTelefonico;
        this.email = builder.email;
        this.condizioniMediche = builder.condizioniMediche;
        this.password = builder.password;
    }

    // Getters
    public String getCodiceFiscalePaziente() {
        return numeroTesseraSanitaria;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public LocalDate getDataDiNascita() {
        return dataDiNascita;
    }

    public String getNumeroTelefonico() {
        return numeroTelefonico;
    }

    public String getEmail() {
        return email;
    }

    public String getCondizioniMediche() {
        return condizioniMediche;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Paziente paziente = (Paziente) o;
        return Objects.equals(numeroTesseraSanitaria, paziente.numeroTesseraSanitaria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroTesseraSanitaria);
    }

    @Override
    public String toString() {
        return "Paziente{" +
                "numeroTesseraSanitaria='" + numeroTesseraSanitaria + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    /**
     * Builder per creare istanze di Paziente.
     * Pattern Builder per costruzione flessibile e leggibile.
     */
    public static class Builder {
        private String numeroTesseraSanitaria;
        private String nome;
        private String cognome;
        private LocalDate dataDiNascita;
        private String numeroTelefonico;
        private String email;
        private String condizioniMediche;
        private String password;

        public Builder() {
            // Default constructor for the Builder pattern
        }

        public Builder codiceFiscalePaziente(String numeroTesseraSanitaria) {
            this.numeroTesseraSanitaria = numeroTesseraSanitaria;
            return this;
        }

        public Builder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public Builder cognome(String cognome) {
            this.cognome = cognome;
            return this;
        }

        public Builder dataDiNascita(LocalDate dataDiNascita) {
            this.dataDiNascita = dataDiNascita;
            return this;
        }

        public Builder numeroTelefonico(String numeroTelefonico) {
            this.numeroTelefonico = numeroTelefonico;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder condizioniMediche(String condizioniMediche) {
            this.condizioniMediche = condizioniMediche;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Paziente build() {
            // Validazione dei campi obbligatori
            Objects.requireNonNull(numeroTesseraSanitaria, "Numero tessera sanitaria è obbligatorio");
            Objects.requireNonNull(email, "Email è obbligatoria");
            Objects.requireNonNull(password, "Password è obbligatoria");

            return new Paziente(this);
        }
    }
}

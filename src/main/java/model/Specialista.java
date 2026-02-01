package model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entità che rappresenta uno Specialista nel sistema.
 * 
 * Database: tabella `specialista`
 * - PK composita: nome, cognome, email, specializzazione
 * - Autenticazione: email + password
 */
public class Specialista {
    private final String nome; // Parte della PK
    private final String cognome; // Parte della PK
    private final LocalDate dataDiNascita;
    private final String numeroTelefonico;
    private final String email; // Parte della PK + Username per autenticazione
    private final String specializzazione; // Parte della PK
    private final String password; // Password per autenticazione

    // Costruttore privato (si usa il Builder)
    private Specialista(Builder builder) {
        this.nome = builder.nome;
        this.cognome = builder.cognome;
        this.dataDiNascita = builder.dataDiNascita;
        this.numeroTelefonico = builder.numeroTelefonico;
        this.email = builder.email;
        this.specializzazione = builder.specializzazione;
        this.password = builder.password;
    }

    // Getters
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

    public String getSpecializzazione() {
        return specializzazione;
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
        Specialista that = (Specialista) o;
        return Objects.equals(nome, that.nome) &&
                Objects.equals(cognome, that.cognome) &&
                Objects.equals(email, that.email) &&
                Objects.equals(specializzazione, that.specializzazione);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, cognome, email, specializzazione);
    }

    @Override
    public String toString() {
        return "Specialista{" +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", specializzazione='" + specializzazione + '\'' +
                '}';
    }

    /**
     * Builder per creare istanze di Specialista.
     * Pattern Builder per costruzione flessibile e leggibile.
     */
    public static class Builder {
        private String nome;
        private String cognome;
        private LocalDate dataDiNascita;
        private String numeroTelefonico;
        private String email;
        private String specializzazione;
        private String password;

        public Builder() {
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

        public Builder specializzazione(String specializzazione) {
            this.specializzazione = specializzazione;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Specialista build() {
            // Validazione dei campi obbligatori (quelli della PK + password)
            Objects.requireNonNull(nome, "Nome è obbligatorio");
            Objects.requireNonNull(cognome, "Cognome è obbligatorio");
            Objects.requireNonNull(email, "Email è obbligatoria");
            Objects.requireNonNull(specializzazione, "Specializzazione è obbligatoria");
            Objects.requireNonNull(password, "Password è obbligatoria");

            return new Specialista(this);
        }
    }
}

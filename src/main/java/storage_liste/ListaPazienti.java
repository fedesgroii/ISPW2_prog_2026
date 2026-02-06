package storage_liste;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Paziente;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * Singleton class that manages an in-memory list of patients.
 */
public class ListaPazienti {
    private static final Logger logger = Logger.getLogger(ListaPazienti.class.getName());

    // AtomicReference for thread-safe Singleton implementation
    private static final AtomicReference<ListaPazienti> istanzaListaPazienti = new AtomicReference<>();

    // Internal list exposed as an ObservableList for JavaFX compatibility
    private final ObservableList<Paziente> observableListaPazienti;

    // Private constructor to prevent multiple instantiations
    private ListaPazienti() {
        this.observableListaPazienti = FXCollections.observableList(new CopyOnWriteArrayList<>());
        // Initial test user for in-memory verification
        this.observableListaPazienti.add(new Paziente.Builder()
                .nome("Federico")
                .cognome("Sgroi")
                .dataDiNascita(java.time.LocalDate.of(2003, 3, 1))
                .numeroTelefonico("1234567890")
                .email("paziente@test.it")
                .codiceFiscalePaziente("RSSMRA90A01H501Z")
                .condizioniMediche("Nessuna")
                .password("pass")
                .build());
    }

    /**
     * Returns the thread-safe Singleton instance.
     */
    public static ListaPazienti getIstanzaListaPazienti() {
        if (istanzaListaPazienti.get() == null) {
            istanzaListaPazienti.compareAndSet(null, new ListaPazienti());
        }
        return istanzaListaPazienti.get();
    }

    /**
     * Adds a patient to the list.
     */
    public void aggiungiPaziente(Paziente paziente) {
        if (paziente == null) {
            logger.warning("Tentativo di aggiungere un paziente nullo.");
            return;
        }
        observableListaPazienti.add(paziente);
    }

    /**
     * Removes a patient by their Codice Fiscale (Health Insurance Number).
     */
    public boolean rimuoviPaziente(String codiceFiscale) {
        if (codiceFiscale == null || codiceFiscale.isBlank()) {
            logger.warning("Codice fiscale non valido per la rimozione.");
            return false;
        }
        return observableListaPazienti.removeIf(p -> p.getCodiceFiscalePaziente().equals(codiceFiscale));
    }

    /**
     * Displays all registered patients in the logs.
     */
    public void visualizzaPazienti() {
        if (observableListaPazienti.isEmpty()) {
            logger.info("Nessun paziente registrato.");
        } else {
            observableListaPazienti.forEach(paziente -> logger.info(paziente.toString()));
        }
    }

    /**
     * Finds a patient by Codice Fiscale.
     */
    public Paziente trovaPaziente(String codiceFiscale) {
        if (codiceFiscale == null || codiceFiscale.isBlank()) {
            logger.warning("Codice fiscale non valido per la ricerca.");
            return null;
        }
        return observableListaPazienti.stream()
                .filter(p -> p.getCodiceFiscalePaziente().equals(codiceFiscale))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns the observable list of patients.
     */
    public ObservableList<Paziente> getObservableListaPazienti() {
        return observableListaPazienti;
    }
}

package storage_liste;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Paziente;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class ListaPazienti {
    private static final Logger logger = Logger.getLogger(ListaPazienti.class.getName());
    // Lista osservabile di pazienti (utile per JavaFX)
    private final ObservableList<Paziente> observableListaPazienti;

    // Variabile statica per contenere l'unica istanza della classe (Singleton)
    private static ListaPazienti istanzaListaPazienti;

    // Costruttore privato per evitare istanziazioni multiple
    private ListaPazienti() {
        observableListaPazienti = FXCollections.observableArrayList();
        // Aggiunta utenti di test per verifica In-Memory
        observableListaPazienti.add(new Paziente.Builder()
                .nome("Mario")
                .cognome("Rossi")
                .dataDiNascita(java.time.LocalDate.of(1990, 1, 1))
                .numeroTelefonico("1234567890")
                .email("paziente@test.it")
                .codiceFiscalePaziente("RSSMRA90A01H501W")
                .condizioniMediche("Nessuna")
                .password("password123")
                .build());
    }

    // Metodo statico per ottenere l'unica istanza della classe
    public static ListaPazienti getIstanzaListaPazienti() {
        if (istanzaListaPazienti == null) {
            istanzaListaPazienti = new ListaPazienti(); // Crea l'istanza solo se non esiste già
        }
        return istanzaListaPazienti;
    }

    // Metodo per aggiungere un paziente alla lista
    public void aggiungiPaziente(Paziente paziente) {
        observableListaPazienti.add(paziente);
    }


    // Metodo per rimuovere un paziente dalla lista (per codice fiscale)
    public boolean rimuoviPaziente(String codiceFiscale) {
        for (Paziente paziente : observableListaPazienti) {
            if (paziente.getCodiceFiscalePaziente().equals(codiceFiscale)) {
                observableListaPazienti.remove(paziente);
                return true;
            }
        }
        return false; // Paziente non trovato
    }

    // Metodo per visualizzare la lista di pazienti
    public void visualizzaPazienti() {
        if (observableListaPazienti.isEmpty()) {
            logger.info("Nessun paziente registrato.");
        } else {
            for (Paziente paziente : observableListaPazienti) {
                logger.info((Supplier<String>) paziente);
            }
        }
    }

    // Metodo per trovare un paziente per codice fiscale
    public Paziente trovaPaziente(String codiceFiscale) {
        for (Paziente paziente : observableListaPazienti) {
            if (paziente.getCodiceFiscalePaziente().equals(codiceFiscale)) {
                return paziente;
            }
        }
        return null; // Paziente non trovato
    }

    public boolean isDuplicateInList(Paziente paziente, ListaPazienti pazienteLista) {
        // Controlla duplicati nella lista
        for (Paziente p : pazienteLista.getObservableListaPazienti()) {
            if ((p.getEmail() != null && paziente.getEmail() != null
                    && p.getEmail().equalsIgnoreCase(paziente.getEmail())) ||
                    (p.getNumeroTelefonico() != null && paziente.getNumeroTelefonico() != null
                            && p.getNumeroTelefonico().equalsIgnoreCase(paziente.getNumeroTelefonico()))
                    ||
                    (p.getCodiceFiscalePaziente() != null && paziente.getCodiceFiscalePaziente() != null
                            && p.getCodiceFiscalePaziente().equalsIgnoreCase(paziente.getCodiceFiscalePaziente()))) {
                return true; // Restituisce true se ci sono duplicati
            }
        }
        return false;
    }

    // **Metodo per ottenere la lista osservabile di pazienti**
    public ObservableList<Paziente> getObservableListaPazienti() {
        return observableListaPazienti;
    }

}

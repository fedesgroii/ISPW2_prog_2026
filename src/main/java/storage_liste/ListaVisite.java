package storage_liste;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Visita;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ListaVisite {
    private static final Logger logger = Logger.getLogger(ListaVisite.class.getName());

    // Uso di AtomicReference per garantire la thread-safety del Singleton
    private static final AtomicReference<ListaVisite> istanzaListaVisite = new AtomicReference<>();

    // Lista thread-safe interna, che viene poi esposta come ObservableList
    private final ObservableList<Visita> observableListaVisite;

    // Costruttore privato per impedire istanziazioni esterne
    private ListaVisite() {
        this.observableListaVisite = FXCollections.observableList(new CopyOnWriteArrayList<>());
    }

    // Metodo per ottenere l'istanza Singleton in modo thread-safe
    public static ListaVisite getIstanzaListaVisite() {
        if (istanzaListaVisite.get() == null) {
            istanzaListaVisite.compareAndSet(null, new ListaVisite());
        }
        return istanzaListaVisite.get();
    }

    // Metodo per aggiungere una visita alla lista
    public boolean aggiungiVisita(Visita visita) {
        if (visita == null) {
            logger.warning("Tentativo di aggiungere una visita nulla.");
            return false;
        }
        observableListaVisite.add(visita);
        return true;
    }

    // Metodo per rimuovere una visita (identificata da codice fiscale, data e orario)
    public boolean rimuoviVisita(String codiceFiscale, String data, String orario) {
        if (codiceFiscale == null || data == null || orario == null) {
            logger.warning("Dati non validi per la rimozione della visita.");
            return false;
        }
        return observableListaVisite.removeIf(visita ->
                visita.getPaziente().getCodiceFiscalePaziente().equalsIgnoreCase(codiceFiscale) &&
                        visita.getData().toString().equals(data) &&
                        visita.getOrario().toString().equals(orario));
    }

    // Metodo per visualizzare la lista di visite
    public void visualizzaVisite() {
        if (observableListaVisite.isEmpty()) {
            if (logger.isLoggable(java.util.logging.Level.INFO)) {
                logger.info("Nessuna visita registrata.");
            }
        } else {
            for (Visita visita : observableListaVisite) {
                if (logger.isLoggable(java.util.logging.Level.INFO)) {
                    logger.info(visita.toString());
                }
            }
        }
    }


    // Metodo per trovare una visita per codice fiscale, data e orario
    public Optional<Visita> trovaVisita(String codiceFiscale, String data, String orario) {
        if (codiceFiscale == null || data == null || orario == null) {
            logger.warning("Dati non validi per la ricerca della visita.");
            return Optional.empty();
        }
        return observableListaVisite.stream()
                .filter(visita -> visita.getPaziente().getCodiceFiscalePaziente().equalsIgnoreCase(codiceFiscale) &&
                        visita.getData().toString().equals(data) &&
                        visita.getOrario().toString().equals(orario))
                .findFirst();
    }

    // Metodo per ottenere la lista osservabile di visite
    public ObservableList<Visita> getObservableListaVisite() {
        return observableListaVisite;
    }


    /**
     * Verifica se lo slot per una visita (data e orario) è disponibile all'interno della lista.
     *
     * @param data   La data della visita.
     * @param orario L'orario della visita.
     * @return true se lo slot è disponibile, false se esiste già una visita con la stessa data e orario.
     */
    public boolean isVisitaDisponibileInLista(LocalDate data, LocalTime orario) {
        if (data == null || orario == null) {
            logger.warning("Dati non validi per la verifica della visita.");
            return false;
        }

        return observableListaVisite.stream()
                .noneMatch(visita -> data.isEqual(visita.getData()) && orario.equals(visita.getOrario()));
    }


}

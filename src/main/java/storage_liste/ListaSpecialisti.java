package storage_liste;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Specialista;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ListaSpecialisti {
    private static final Logger logger = Logger.getLogger(ListaSpecialisti.class.getName());

    // Uso di AtomicReference per la thread-safety del Singleton
    private static final AtomicReference<ListaSpecialisti> istanzaListaSpecialisti = new AtomicReference<>();

    // Lista thread-safe interna, esposta come ObservableList
    private final ObservableList<Specialista> observableListaSpecialisti;

    // Costruttore privato per impedire istanziazioni esterne
    private ListaSpecialisti() {
        this.observableListaSpecialisti = FXCollections.observableList(new CopyOnWriteArrayList<>());
        // Aggiunta specialisti di test per verifica In-Memory
        this.observableListaSpecialisti.add(new Specialista.Builder()
                .nome("Luigi")
                .cognome("Verdi")
                .dataDiNascita(java.time.LocalDate.of(1980, 5, 20))
                .numeroTelefonico("0987654321")
                .email("specialista@test.it")
                .specializzazione("Cardiologia")
                .password("password456")
                .build());
    }

    // Metodo per ottenere l'istanza Singleton in modo thread-safe
    public static ListaSpecialisti getIstanzaListaSpecialisti() {
        if (istanzaListaSpecialisti.get() == null) {
            istanzaListaSpecialisti.compareAndSet(null, new ListaSpecialisti());
        }
        return istanzaListaSpecialisti.get();
    }

    // Metodo per aggiungere uno specialista alla lista
    public void aggiungiSpecialista(Specialista specialista) {
        if (specialista == null) {
            logger.warning("Tentativo di aggiungere uno specialista nullo.");
            return;
        }
        observableListaSpecialisti.add(specialista);
    }

    // Metodo per rimuovere uno specialista dalla lista (per email)
    public boolean rimuoviSpecialista(String email) {
        if (email == null || email.isBlank()) {
            logger.warning("Email non valida per la rimozione dello specialista.");
            return false;
        }
        return observableListaSpecialisti.removeIf(s -> s.getEmail().equalsIgnoreCase(email));
    }

    // Metodo per visualizzare la lista di specialisti
    public void visualizzaSpecialisti() {
        if (observableListaSpecialisti.isEmpty()) {
            logger.info("Nessuno specialista registrato.");
        } else {
            observableListaSpecialisti.forEach(specialista -> logger.info(specialista.toString()));
        }
    }

    // Metodo per trovare uno specialista per email
    public Specialista trovaSpecialista(String email) {
        if (email == null || email.isBlank()) {
            logger.warning("Email non valida per la ricerca dello specialista.");
            return null;
        }
        return observableListaSpecialisti.stream()
                .filter(s -> s.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    // Metodo per ottenere la lista osservabile di specialisti
    public ObservableList<Specialista> getObservableListaSpecialisti() {
        return observableListaSpecialisti;
    }
}

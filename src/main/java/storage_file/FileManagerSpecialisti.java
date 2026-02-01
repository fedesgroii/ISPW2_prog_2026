package storage_file;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Specialista;
import storage_db.DataStorageStrategy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileManagerSpecialisti implements DataStorageStrategy<Specialista> {
    private static final String DIRECTORY = "src/main/resources/specialisti_salvati/";
    private static final String FILE_EXTENSION = ".json"; // Definisci una costante per l'estensione del file

    private final ObjectMapper objectMapper;
    private static final Logger logger = Logger.getLogger(FileManagerSpecialisti.class.getName());
    // Lock per sincronizzazione multithread
    private final Object fileLock = new Object();

    public FileManagerSpecialisti() {
        this.objectMapper = new ObjectMapper();
        File dir = new File(DIRECTORY);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("Impossibile creare la directory: " + DIRECTORY);
        }
    }

    /**
     * Genera il nome del file basato sulla specializzazione dello specialista.
     */
    private String generaNomeFile(Specialista specialista) {
        if (!isValid(specialista)) {
            throw new IllegalArgumentException("Specialista non valido");
        }
        return specialista.getSpecializzazione().replaceAll("[^a-zA-Z0-9]", "_") + FILE_EXTENSION; // Usa la costante
                                                                                                   // FILE_EXTENSION
    }

    /**
     * Genera il percorso completo del file utilizzando Path per maggiore sicurezza.
     */
    private Path generaPercorsoFile(Specialista specialista) {
        return Paths.get(DIRECTORY, generaNomeFile(specialista));
    }

    /**
     * Valida l'oggetto Specialista.
     */
    private boolean isValid(Specialista specialista) {
        return specialista != null &&
                specialista.getSpecializzazione() != null &&
                !specialista.getSpecializzazione().isEmpty();
    }

    @Override
    public boolean salva(Specialista specialista) {
        synchronized (fileLock) {
            if (!isValid(specialista)) {
                logger.warning("Tentativo di salvataggio di uno specialista non valido.");
                return false;
            }
            Path path = generaPercorsoFile(specialista);
            File file = path.toFile();
            if (file.exists()) {
                logger.warning("File già esistente per lo specialista: " + file.getName());
                return false;
            }
            return scriviFile(file, specialista);
        }
    }

    @Override
    public Optional<Specialista> trova(Specialista specialista) {
        synchronized (fileLock) {
            if (!isValid(specialista)) {
                logger.warning("Tentativo di ricerca di uno specialista non valido.");
                return Optional.empty();
            }
            Path path = generaPercorsoFile(specialista);
            File file = path.toFile();
            if (!file.exists()) {
                logger.warning("Specialista non trovato: " + file.getName());
                return Optional.empty();
            }
            return leggiFile(file);
        }
    }

    @Override
    public boolean aggiorna(Specialista specialista) {
        synchronized (fileLock) {
            if (!isValid(specialista)) {
                logger.warning("Tentativo di aggiornamento di uno specialista non valido.");
                return false;
            }
            Path path = generaPercorsoFile(specialista);
            File file = path.toFile();
            if (!file.exists()) {
                logger.warning("File dello specialista non trovato per l'aggiornamento: " + file.getName());
                return false;
            }
            return scriviFile(file, specialista);
        }
    }

    @Override
    public boolean elimina(Specialista specialista) {
        synchronized (fileLock) {
            if (!isValid(specialista)) {
                logger.warning("Tentativo di eliminazione di uno specialista non valido.");
                return false;
            }
            Path path = generaPercorsoFile(specialista);
            try {
                Files.delete(path); // Usa Files#delete per migliorare i messaggi di errore
                return true;
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Errore durante l'eliminazione dello specialista", e);
                return false;
            }
        }
    }

    /**
     * Ricerca tutti gli specialisti presenti nella directory.
     */
    public List<Specialista> trovaTutti() {
        File dir = new File(DIRECTORY);
        if (!dir.exists() || !dir.isDirectory()) {
            logger.warning("Directory degli specialisti non trovata o non valida.");
            return List.of();
        }
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(file -> file.getName().endsWith(FILE_EXTENSION)) // Usa la costante FILE_EXTENSION
                .map(this::leggiFile)
                .flatMap(Optional::stream)
                .toList(); // Usa Stream.toList() invece di collect(Collectors.toList())
    }

    /**
     * Ricerca uno specialista tramite la sua specializzazione.
     */
    public Optional<Specialista> trovaPerSpecializzazione(String specializzazione) {
        if (specializzazione == null || specializzazione.isEmpty()) {
            logger.warning("Specializzazione non valida per la ricerca.");
            return Optional.empty();
        }
        File file = new File(DIRECTORY + specializzazione.replaceAll("[^a-zA-Z0-9]", "_") + FILE_EXTENSION);
        if (!file.exists()) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning(String.format("Specialista non trovato per la specializzazione: %s", specializzazione));
            }
            return Optional.empty();
        }
        return leggiFile(file);
    }

    /**
     * Scrive un oggetto Specialista in un file JSON.
     */
    private boolean scriviFile(File file, Specialista specialista) {
        try {
            objectMapper.writeValue(file, specialista);
            logger.info("Specialista salvato con successo: " + file.getName());
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Errore durante il salvataggio dello specialista", e);
            return false;
        }
    }

    /**
     * Legge un oggetto Specialista da un file JSON.
     */
    private Optional<Specialista> leggiFile(File file) {
        try {
            Specialista specialista = objectMapper.readValue(file, Specialista.class);
            return Optional.of(specialista);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Errore durante la lettura dello specialista", e);
            return Optional.empty();
        }
    }

    /**
     * Ricerca uno specialista tramite la sua email.
     */
    public Optional<Specialista> trovaPerEmail(String email) {
        if (email == null || email.isEmpty()) {
            logger.warning("Email non valida per la ricerca.");
            return Optional.empty();
        }
        File dir = new File(DIRECTORY);
        if (!dir.exists() || !dir.isDirectory()) {
            logger.warning("Directory degli specialisti non trovata o non valida.");
            return Optional.empty();
        }
        // Cerca il file con l'email specificata
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(file -> file.getName().endsWith(FILE_EXTENSION)) // Usa la costante FILE_EXTENSION
                .map(this::leggiFile) // Leggi il contenuto dei file
                .flatMap(Optional::stream) // Gestisci i valori Optional
                .filter(specialista -> specialista.getEmail().equalsIgnoreCase(email)) // Filtra per email
                .findFirst(); // Restituisce il primo risultato trovato
    }

    public static String getFolderPath() {
        return DIRECTORY;
    }

    @Override
    public Optional<Specialista> findByEmail(String email) {
        // Delega al metodo trovaPerEmail già esistente
        return trovaPerEmail(email);
    }
}
package storage_file;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Visita;
import storage_db.DataStorageStrategy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.time.LocalDate;
import java.time.LocalTime;

public class FileManagerVisite implements DataStorageStrategy<Visita> {
    private static final String DIRECTORY = "src/main/resources/visite_salvate/"; // Directory di salvataggio
    private static final String JSON_EXTENSION = ".json";
    private static final String ERR_DIR_NOT_FOUND = "Directory delle visite non trovata o non valida.";
    private final ObjectMapper objectMapper; // Gestore JSON
    private static final Logger logger = Logger.getLogger(FileManagerVisite.class.getName()); // Logger per debugging
    // Formati per data e ora
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmm");
    // Lock per sincronizzazione multithread
    private final Object fileLock = new Object();

    public FileManagerVisite() {
        this.objectMapper = new ObjectMapper(); // Inizializza ObjectMapper per gestire i file JSON
        File dir = new File(DIRECTORY);
        if (!dir.exists() && !dir.mkdirs()) { // Crea la directory se non esiste
            throw new IllegalStateException("Impossibile creare la directory: " + DIRECTORY);
        }
    }

    /**
     * Genera il nome del file basato sulle informazioni della visita.
     */
    private String generaNomeFile(Visita visita) {
        if (!isValid(visita)) { // Controllo validità
            throw new IllegalArgumentException("Visita non valida");
        }
        return visita.getPaziente().getCodiceFiscalePaziente() + "_" +
                visita.getData().format(DATE_FORMAT) + "_" +
                visita.getOrario().format(TIME_FORMAT) + JSON_EXTENSION; // Nome file univoco
    }

    /**
     * Genera il percorso completo del file utilizzando Path per maggiore sicurezza.
     */
    private Path generaPercorsoFile(Visita visita) {
        return Paths.get(DIRECTORY, generaNomeFile(visita));
    }

    /**
     * Valida l'oggetto Visita.
     */
    private boolean isValid(Visita visita) {
        return visita != null &&
                visita.getPaziente() != null &&
                visita.getPaziente().getCodiceFiscalePaziente() != null &&
                visita.getData() != null &&
                visita.getOrario() != null;
    }

    @Override
    public boolean salva(Visita visita) {
        synchronized (fileLock) {
            if (!isValid(visita)) {
                logger.warning("Tentativo di salvataggio di una visita non valida.");
                return false;
            }
            Path path = generaPercorsoFile(visita);
            File file = path.toFile();
            if (file.exists()) {
                logger.warning("File già esistente per la visita: " + file.getName());
                return false;
            }
            return scriviFile(file, visita);
        }
    }

    @Override
    public Optional<Visita> trova(Visita visita) {
        synchronized (fileLock) {
            if (!isValid(visita)) {
                logger.warning("Tentativo di ricerca di una visita non valida.");
                return Optional.empty();
            }
            Path path = generaPercorsoFile(visita);
            File file = path.toFile();
            if (!file.exists()) {
                logger.warning("Visita non trovata: " + file.getName());
                return Optional.empty();
            }
            return leggiFile(file);
        }
    }

    @Override
    public boolean aggiorna(Visita visita) {
        synchronized (fileLock) {
            if (!isValid(visita)) {
                logger.warning("Tentativo di aggiornamento di una visita non valida.");
                return false;
            }
            Path path = generaPercorsoFile(visita);
            File file = path.toFile();
            if (!file.exists()) {
                logger.warning("File della visita non trovato per l'aggiornamento: " + file.getName());
                return false;
            }
            return scriviFile(file, visita);
        }
    }

    @Override
    public boolean elimina(Visita visita) {
        synchronized (fileLock) {
            if (!isValid(visita)) {
                logger.warning("Tentativo di eliminazione di una visita non valida.");
                return false;
            }
            Path path = generaPercorsoFile(visita);
            try {
                Files.delete(path); // Usa Files#delete per migliorare i messaggi di errore
                return true;
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Errore durante l'eliminazione della visita", e);
                return false;
            }
        }
    }

    /**
     * Ricerca visite associate a un paziente specifico.
     */
    public List<Visita> trovaPerPaziente(String codiceFiscalePaziente) {
        File dir = new File(DIRECTORY);
        if (!dir.exists() || !dir.isDirectory()) {
            logger.warning(ERR_DIR_NOT_FOUND);
            return List.of();
        }
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(file -> file.getName().startsWith(codiceFiscalePaziente))
                .map(this::leggiFile)
                .flatMap(Optional::stream)
                .toList(); // Usa Stream.toList() invece di collect(Collectors.toList())
    }

    /**
     * Scrive un oggetto Visita in un file JSON.
     */
    private boolean scriviFile(File file, Visita visita) {
        try {
            objectMapper.writeValue(file, visita);
            logger.info("Visita salvata con successo: " + file.getName());
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Errore durante il salvataggio della visita", e);
            return false;
        }
    }

    /**
     * Legge un oggetto Visita da un file JSON.
     */
    private Optional<Visita> leggiFile(File file) {
        try {
            Visita visita = objectMapper.readValue(file, Visita.class);
            return Optional.of(visita);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Errore durante la lettura della visita", e);
            return Optional.empty();
        }
    }

    public static String getFolderPath() {
        return DIRECTORY;
    }

    /**
     * Verifica se lo slot della visita (data e orario) è disponibile nel file
     * system.
     * Se esiste già un file che rappresenta una visita nello stesso slot, lo slot
     * non è disponibile.
     *
     * @param data   La data della visita
     * @param orario L'orario della visita
     * @return true se non esiste una visita nello stesso slot (quindi lo slot è
     *         disponibile),
     *         false altrimenti.
     */
    public boolean isVisitaDisponibileInFile(LocalDate data, LocalTime orario) {
        // Validazione dei parametri
        if (data == null || orario == null) {
            logger.warning("Dati non validi per la verifica della visita.");
            return false;
        }

        synchronized (fileLock) {
            File dir = new File(DIRECTORY);
            if (!dir.exists() || !dir.isDirectory()) {
                logger.warning(ERR_DIR_NOT_FOUND);
                // Se la directory non esiste, possiamo considerare lo slot come disponibile
                return true;
            }

            // Formatta la data e l'orario secondo i pattern definiti
            String dateStr = data.format(DATE_FORMAT);
            String timeStr = orario.format(TIME_FORMAT);
            // Il suffisso del file da cercare (es: _20250219_1530.json)
            String suffix = "_" + dateStr + "_" + timeStr + JSON_EXTENSION;

            // Elenca tutti i file presenti nella directory
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Se il nome del file termina con il suffisso specificato,
                    // significa che esiste già una visita per quella data e orario.
                    if (file.getName().endsWith(suffix)) {
                        return false;
                    }
                }
            }
        }

        // Nessun file trovato per quella data/orario: lo slot è disponibile
        return true;
    }

    @Override
    public List<Visita> getAllInstanceOfActor() {
        File dir = new File(DIRECTORY);
        if (!dir.exists() || !dir.isDirectory()) {
            logger.warning(ERR_DIR_NOT_FOUND);
            return List.of();
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return List.of();
        }
        return Arrays.stream(files)
                .filter(file -> file.getName().endsWith(JSON_EXTENSION))
                .map(this::leggiFile)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public Optional<Visita> findByEmail(String email) {
        return Optional.empty();
    }
}
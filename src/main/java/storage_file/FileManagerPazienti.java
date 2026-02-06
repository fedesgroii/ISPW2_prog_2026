package storage_file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import model.Paziente;
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

public class FileManagerPazienti implements DataStorageStrategy<Paziente> {
    private static String resolveDirectory() {
        String baseDir = "src/main/resources/pazienti_salvati/";
        String moduleDir = "ISPW2_PROG_2026/" + baseDir;

        File moduleFolder = new File(moduleDir);
        File baseFolder = new File(baseDir);

        // Prefer the directory that exists AND contains files
        if (moduleFolder.exists() && hasJsonFiles(moduleFolder)) {
            return moduleDir;
        }
        if (baseFolder.exists() && hasJsonFiles(baseFolder)) {
            return baseDir;
        }

        // Fallback to module dir if it exists, otherwise base
        return (moduleFolder.exists()) ? moduleDir : baseDir;
    }

    private static boolean hasJsonFiles(File folder) {
        File[] files = folder.listFiles();
        return files != null && Arrays.stream(files).anyMatch(f -> f.getName().endsWith(FILE_EXTENSION));
    }

    private static final String DIRECTORY = resolveDirectory();
    private static final String FILE_EXTENSION = ".json";

    private final ObjectMapper objectMapper;
    private static final Logger logger = Logger.getLogger(FileManagerPazienti.class.getName());
    private final Object fileLock = new Object();

    public FileManagerPazienti() {
        logger.info(() -> "[DEBUG] FileManagerPazienti initialized. Using directory: "
                + new File(DIRECTORY).getAbsolutePath());
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        File dir = new File(DIRECTORY);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("Impossibile creare la directory: " + DIRECTORY);
        }
    }

    /**
     * Genera il nome del file basato sul codice fiscale del paziente.
     */
    private String generaNomeFile(Paziente paziente) {
        if (!isValid(paziente)) {
            throw new IllegalArgumentException("Paziente non valido");
        }
        return paziente.getCodiceFiscalePaziente() + FILE_EXTENSION; // Usa la costante FILE_EXTENSION
    }

    /**
     * Genera il percorso completo del file utilizzando Path per maggiore sicurezza.
     */
    private Path generaPercorsoFile(Paziente paziente) {
        return Paths.get(DIRECTORY, generaNomeFile(paziente));
    }

    /**
     * Valida l'oggetto Paziente.
     */
    private boolean isValid(Paziente paziente) {
        return paziente != null &&
                paziente.getCodiceFiscalePaziente() != null &&
                !paziente.getCodiceFiscalePaziente().isEmpty();
    }

    @Override
    public boolean salva(Paziente paziente) {
        synchronized (fileLock) {
            if (!isValid(paziente)) {
                logger.warning("Tentativo di salvataggio di un paziente non valido.");
                return false;
            }
            Path path = generaPercorsoFile(paziente);
            File file = path.toFile();
            if (file.exists()) {
                logger.warning("File già esistente per il paziente: " + file.getName());
                return false;
            }
            return scriviFile(file, paziente);
        }
    }

    @Override
    public Optional<Paziente> trova(Paziente paziente) {
        synchronized (fileLock) {
            if (!isValid(paziente)) {
                logger.warning("Tentativo di ricerca di un paziente non valido.");
                return Optional.empty();
            }
            Path path = generaPercorsoFile(paziente);
            File file = path.toFile();
            if (!file.exists()) {
                logger.warning("Paziente non trovato: " + file.getName());
                return Optional.empty();
            }
            return leggiFile(file);
        }
    }

    @Override
    public boolean aggiorna(Paziente paziente) {
        synchronized (fileLock) {
            if (!isValid(paziente)) {
                logger.warning("Tentativo di aggiornamento di un paziente non valido.");
                return false;
            }
            Path path = generaPercorsoFile(paziente);
            File file = path.toFile();
            if (!file.exists()) {
                logger.warning("File del paziente non trovato per l'aggiornamento: " + file.getName());
                return false;
            }
            return scriviFile(file, paziente);
        }
    }

    @Override
    public boolean elimina(Paziente paziente) {
        synchronized (fileLock) {
            if (!isValid(paziente)) {
                logger.warning("Tentativo di eliminazione di un paziente non valido.");
                return false;
            }
            Path path = generaPercorsoFile(paziente);
            try {
                Files.delete(path); // Usa Files#delete per migliorare i messaggi di errore
                return true;
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Errore durante l'eliminazione del paziente", e);
                return false;
            }
        }
    }

    /**
     * Ricerca tutti i pazienti presenti nella directory.
     */
    public List<Paziente> trovaTutti() {
        File dir = new File(DIRECTORY);
        if (!dir.exists() || !dir.isDirectory()) {
            logger.warning("Directory dei pazienti non trovata o non valida.");
            return List.of();
        }
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(file -> file.getName().endsWith(FILE_EXTENSION)) // Usa la costante FILE_EXTENSION
                .map(this::leggiFile)
                .flatMap(Optional::stream)
                .toList(); // Usa Stream.toList() invece di collect(Collectors.toList())
    }

    /**
     * Scrive un oggetto Paziente in un file JSON.
     */
    private boolean scriviFile(File file, Paziente paziente) {
        try {
            objectMapper.writeValue(file, paziente);
            logger.info("Paziente salvato con successo: " + file.getName());
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Errore durante il salvataggio del paziente", e);
            return false;
        }
    }

    /**
     * Legge un oggetto Paziente da un file JSON.
     */
    private Optional<Paziente> leggiFile(File file) {
        try {
            Paziente paziente = objectMapper.readValue(file, Paziente.class);
            return Optional.of(paziente);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Errore durante la lettura del paziente", e);
            return Optional.empty();
        }
    }

    public static boolean trovaCodiceFiscaleNellaCartellaPazienti(String inputValue) {
        File folder = new File(DIRECTORY); // Usa la costante DIRECTORY
        if (!folder.exists() || !folder.isDirectory()) {
            logger.log(Level.SEVERE, "PATH cartella in cui cercare non valido");
            return false;
        }
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            logger.info("La cartella è vuota o non contiene file.");
            return false;
        }
        inputValue = inputValue + FILE_EXTENSION; // Usa la costante FILE_EXTENSION
        String finalInputValue = inputValue;
        return Arrays.stream(files)
                .filter(Objects::nonNull)
                .anyMatch(file -> {
                    boolean match = Objects.equals(file.getName(), finalInputValue);
                    if (match)
                        logger.log(Level.INFO, "Corrispondenza trovata: {0}", file.getName());
                    return match;
                });
    }

    public static String getFolderPath() {
        return DIRECTORY;
    }

    @Override
    public List<Paziente> getAllInstanceOfActor() {
        return trovaTutti();
    }

    @Override
    public Optional<Paziente> findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            logger.warning("Email non valida per la ricerca.");
            return Optional.empty();
        }
        List<Paziente> tutti = trovaTutti();
        logger.log(Level.INFO, "[DEBUG] Searching for email {0} in {1} patients.",
                new Object[] { email, tutti.size() });

        // Cerca in tutti i file
        return tutti.stream()
                .filter(p -> {
                    boolean match = p.getEmail().equalsIgnoreCase(email);
                    if (match) {
                        logger.log(Level.INFO, "[DEBUG] MATCH FOUND for patient: {0}", p.getEmail());
                    }
                    return match;
                })
                .findFirst();
    }
}
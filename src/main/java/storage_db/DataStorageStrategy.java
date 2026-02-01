package storage_db;

import java.util.Optional;

public interface DataStorageStrategy<T> {
    boolean salva(T obj);

    Optional<T> trova(T obj);

    boolean aggiorna(T obj);

    boolean elimina(T obj);

    // Metodo aggiunto per supportare l'autenticazione
    Optional<T> findByEmail(String email);
}

package authentication;

/**
 * Rappresenta il risultato di un tentativo di autenticazione.
 * Contiene informazioni sul successo/fallimento e sui dati dell'utente
 * autenticato.
 */
public class AuthenticationResult {
    private final boolean success;
    private final String userType; // "Patient" | "Specialist" | null
    private final Object user; // Paziente | Specialista | null
    private final String errorMessage;

    private AuthenticationResult(boolean success, String userType, Object user, String errorMessage) {
        this.success = success;
        this.userType = userType;
        this.user = user;
        this.errorMessage = errorMessage;
    }

    /**
     * Crea un risultato di autenticazione riuscita.
     * 
     * @param type Il tipo di utente ("Patient" o "Specialist")
     * @param user L'oggetto utente (Paziente o Specialista)
     * @return AuthenticationResult con successo
     */
    public static AuthenticationResult success(String type, Object user) {
        return new AuthenticationResult(true, type, user, null);
    }

    /**
     * Crea un risultato di autenticazione fallita.
     * 
     * @param message Il messaggio di errore
     * @return AuthenticationResult con fallimento
     */
    public static AuthenticationResult failure(String message) {
        return new AuthenticationResult(false, null, null, message);
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getUserType() {
        return userType;
    }

    public Object getUser() {
        return user;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        if (success) {
            return "AuthenticationResult{success=true, userType='" + userType + "'}";
        } else {
            return "AuthenticationResult{success=false, error='" + errorMessage + "'}";
        }
    }
}

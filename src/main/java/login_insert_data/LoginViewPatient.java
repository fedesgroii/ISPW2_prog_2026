package login_insert_data;

/**
 * Vista di Login per il Paziente (GUI).
 */
public class LoginViewPatient extends LoginViewBase {
    @Override
    protected String getTitleText() {
        return "Login Paziente";
    }

    @Override
    protected String getSubtitleText() {
        return "Inserisci le tue credenziali per accedere.";
    }

    @Override
    protected String getTipo() {
        return "Patient";
    }
}

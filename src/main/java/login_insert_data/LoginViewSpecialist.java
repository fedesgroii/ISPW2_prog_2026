package login_insert_data;

/**
 * Vista di Login per lo Specialista (GUI).
 */
public class LoginViewSpecialist extends LoginViewBase {
    @Override
    protected String getTitleText() {
        return "Login Specialista";
    }

    @Override
    protected String getSubtitleText() {
        return "Inserisci le tue credenziali da specialista per accedere.";
    }

    @Override
    protected String getTipo() {
        return "Specialist";
    }
}

package navigation; // Package di appartenenza

import select_type_login.LoginViewBoundaryCli;
import login_insert_data.LoginViewCli;
import patient_dashboard.PatientDashboardViewCli;
import specialist_dashboard.SpecialistDashboardViewCli;

// Concrete Factory per le viste CLI
// Implementa il Factory Method per creare solo viste a linea di comando
public class CliViewFactory extends ViewFactory {

    @Override
    public View createView(String viewName) {
        try {
            return switch (viewName) {
                case "Login" -> new LoginViewBoundaryCli();
                case "Patient" -> new LoginViewCli("Patient");
                case "Specialist" -> new LoginViewCli("Specialist");
                case "PatientDashboard" -> new PatientDashboardViewCli();
                case "SpecialistDashboard" -> new SpecialistDashboardViewCli();
                case "Booking" -> new patient_dashboard.book_appointment.BookAppointmentViewCli();
                default -> null;
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Errore durante la creazione della vista CLI: " + viewName + ". Errore: " + e.getMessage());
        }
    }
}

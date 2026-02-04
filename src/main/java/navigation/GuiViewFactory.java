package navigation; // Package di appartenenza

import select_type_login.LoginViewBoundaryGui;
import login_insert_data.LoginViewPatient;
import login_insert_data.LoginViewSpecialist;
import patient_dashboard.PatientDashboardView;
import specialist_dashboard.SpecialistDashboardView;

// Concrete Factory per le viste GUI
// Implementa il Factory Method per creare solo viste grafiche
public class GuiViewFactory extends ViewFactory {

    @Override
    public View createView(String viewName) {
        try {
            return switch (viewName) {
                case "Login" -> new LoginViewBoundaryGui();
                case "Patient" -> new LoginViewPatient();
                case "Specialist" -> new LoginViewSpecialist();
                case "PatientDashboard" -> new PatientDashboardView();
                case "SpecialistDashboard" -> new SpecialistDashboardView();
                case "Booking" -> new patient_dashboard.book_appointment.BookAppointmentViewGui();
                default -> null;
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Errore durante la creazione della vista GUI: " + viewName + ". Errore: " + e.getMessage());
        }
    }
}

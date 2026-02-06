package test;

import authentication.dao.FileUserDAO;
import model.Paziente;
import model.Specialista;
import storage_file.FileManagerPazienti;
import storage_file.FileManagerSpecialisti;
import java.util.Optional;

public class TestLogin {
    public static void main(String[] args) {
        System.out.println("Starting Login Test...");

        System.out.println("\n--- Testing Patients ---");
        FileManagerPazienti fmPaz = new FileManagerPazienti();
        FileUserDAO<Paziente> daoPaz = new FileUserDAO<>(fmPaz);

        // Let's list some emails first to be sure
        fmPaz.trovaTutti().forEach(p -> System.out.println("Found patient email: " + p.getEmail()));

        String testEmail = "s.f@gmail.com";
        String testPass = "passwordsicura";

        Optional<Paziente> authPaz = daoPaz.authenticateByEmailAndPassword(testEmail, testPass);
        if (authPaz.isPresent()) {
            System.out.println("Patient Login SUCCESS: " + authPaz.get().getEmail());
        } else {
            System.out.println("Patient Login FAILED for: " + testEmail);
        }

        System.out.println("\n--- Testing Specialists ---");
        FileManagerSpecialisti fmSpec = new FileManagerSpecialisti();
        FileUserDAO<Specialista> daoSpec = new FileUserDAO<>(fmSpec);

        fmSpec.trovaTutti().forEach(s -> System.out.println("Found specialist email: " + s.getEmail()));

        String specEmail = "fedes@test.com";
        String specPass = "pass";

        Optional<Specialista> authSpec = daoSpec.authenticateByEmailAndPassword(specEmail, specPass);
        if (authSpec.isPresent()) {
            System.out.println("Specialist Login SUCCESS: " + authSpec.get().getEmail());
        } else {
            System.out.println("Specialist Login FAILED for: " + specEmail);
        }
    }
}

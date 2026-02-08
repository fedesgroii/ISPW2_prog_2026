package test_class;

import authentication.factory.DAOFactory;
import navigation.CliViewFactory;
import navigation.GuiViewFactory;
import navigation.View;
import navigation.ViewFactory;
import org.junit.jupiter.api.*;
import startupconfig.StartupConfigBean;
import startupconfig.StartupSettingsEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StartupSettingsAndFactoryTest {

    @Test
    @Order(1)
    void testStartupSettingsSingletonThreadSafe() throws Exception {
        // Test Singleton identity
        StartupSettingsEntity instance1 = StartupSettingsEntity.getInstance();
        StartupSettingsEntity instance2 = StartupSettingsEntity.getInstance();

        assertSame(instance1, instance2, "Both instances should be the same (Singleton).");

        // Test thread-safety (Bill Pugh Singleton is inherently thread-safe, but we
        // verify)
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<StartupSettingsEntity> future1 = executor.submit(StartupSettingsEntity::getInstance);
        Future<StartupSettingsEntity> future2 = executor.submit(StartupSettingsEntity::getInstance);

        StartupSettingsEntity threadInstance1 = future1.get();
        StartupSettingsEntity threadInstance2 = future2.get();

        assertSame(threadInstance1, threadInstance2, "Instances from different threads should be the same.");
        executor.shutdown();
    }

    @Test
    @Order(2)
    void testDAOFactoryRAM() {
        StartupConfigBean config = new StartupConfigBean(true, 0); // RAM
        DAOFactory.DAOPair daos = DAOFactory.createDAOs(config);

        assertNotNull(daos.appointmentRepository);
        assertTrue(daos.appointmentRepository instanceof patient_dashboard.book_appointment.RamAppointmentDAO);
    }

    @Test
    @Order(3)
    void testDAOFactoryDatabase() {
        StartupConfigBean config = new StartupConfigBean(true, 1); // Database
        DAOFactory.DAOPair daos = DAOFactory.createDAOs(config);

        assertNotNull(daos.appointmentRepository);
        assertTrue(daos.appointmentRepository instanceof patient_dashboard.book_appointment.DatabaseAppointmentDAO);
    }

    @Test
    @Order(4)
    void testDAOFactoryFile() {
        StartupConfigBean config = new StartupConfigBean(true, 2); // File
        DAOFactory.DAOPair daos = DAOFactory.createDAOs(config);

        assertNotNull(daos.appointmentRepository);
        assertTrue(daos.appointmentRepository instanceof patient_dashboard.book_appointment.FileAppointmentDAO);
    }

    @Test
    @Order(5)
    void testViewFactoryGUI() {
        ViewFactory factory = ViewFactory.getFactory(true);
        assertTrue(factory instanceof GuiViewFactory);

        // Test creation of a common view (e.g. Login)
        View loginView = factory.createView("Login");
        assertNotNull(loginView);
        assertTrue(loginView.getClass().getSimpleName().contains("Gui")
                || loginView.getClass().getSimpleName().contains("Boundary"));
    }

    @Test
    @Order(6)
    void testViewFactoryCLI() {
        ViewFactory factory = ViewFactory.getFactory(false);
        assertTrue(factory instanceof CliViewFactory);

        View loginView = factory.createView("Login");
        assertNotNull(loginView);
        assertTrue(loginView.getClass().getSimpleName().contains("Cli"));
    }
}

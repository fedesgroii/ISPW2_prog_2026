package startupconfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComandoDaTerminale {
    private static final Logger logger = Logger.getLogger(ComandoDaTerminale.class.getName());

    public static void main(String[] args) {
        String comando = "sudo /usr/local/mysql/support-files/mysql.server start";
        eseguiComando(comando);
    }

    public static void eseguiComando(String comando) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", comando);
            processBuilder.redirectErrorStream(true); // Unisce stdout e stderr
            Process process = processBuilder.start();
            // Legge l'output del comando
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }
            // Attende la terminazione del processo
            int exitCode = process.waitFor();
            logger.log(Level.INFO, "Processo terminato con codice: {0}", exitCode);
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, e, () -> "Errore durante l'esecuzione del comando: " + comando);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt(); // Rilancia l'interruzione
            }
        }
    }

    public static void avviaServerSQL(){
        eseguiComando("sudo /usr/local/mysql/support-files/mysql.server start");
    }
}
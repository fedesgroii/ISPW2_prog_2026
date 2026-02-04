package navigation;

import java.util.Scanner;

/**
 * Utility class to provide a single, shared Scanner for System.in.
 * This prevents multiple Scanner instances on System.in and avoids
 * the "Resource leak: 'scanner' is never closed" warning while
 * ensuring System.in is NOT closed during the application lifecycle.
 */
public class ConsoleScanner {
    private static final Scanner SCANNER = new Scanner(System.in);

    private ConsoleScanner() {
        // Private constructor for utility class
    }

    /**
     * Returns the shared Scanner instance.
     * Do NOT close this scanner as it would close System.in.
     */
    public static Scanner getScanner() {
        return SCANNER;
    }
}

package patient_dashboard.book_appointment;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class to verify Italian public holidays.
 * Hardcoded for the years 2026–2030 as requested.
 */
public class ItalianHolidayCalendar {

    private static final Map<Integer, Set<LocalDate>> HOLIDAYS_BY_YEAR = new HashMap<>();

    static {
        // Initialization for years 2026-2030
        for (int year = 2026; year <= 2126; year++) {
            Set<LocalDate> holidays = new HashSet<>();

            // Standard Fixed Holidays
            holidays.add(LocalDate.of(year, Month.JANUARY, 1)); // Capodanno
            holidays.add(LocalDate.of(year, Month.JANUARY, 6)); // Epifania
            holidays.add(LocalDate.of(year, Month.APRIL, 25)); // Liberazione
            holidays.add(LocalDate.of(year, Month.MAY, 1)); // Lavoro
            holidays.add(LocalDate.of(year, Month.JUNE, 2)); // Repubblica
            holidays.add(LocalDate.of(year, Month.AUGUST, 15)); // Ferragosto
            holidays.add(LocalDate.of(year, Month.NOVEMBER, 1)); // Tutti i Santi
            holidays.add(LocalDate.of(year, Month.DECEMBER, 8)); // Immacolata
            holidays.add(LocalDate.of(year, Month.DECEMBER, 25)); // Natale
            holidays.add(LocalDate.of(year, Month.DECEMBER, 26)); // S. Stefano

            // Dynamic Holidays (Easter and Easter Monday)
            addEasterHolidays(year, holidays);

            HOLIDAYS_BY_YEAR.put(year, holidays);
        }
    }

    private ItalianHolidayCalendar() {
        // Utility class
    }

    public static boolean isItalianHoliday(LocalDate date) {
        if (date == null)
            return false;
        Set<LocalDate> yearHolidays = HOLIDAYS_BY_YEAR.get(date.getYear());
        return yearHolidays != null && yearHolidays.contains(date);
    }

    /**
     * Calculates Easter using Gauss algorithm to support any year.
     */
    private static void addEasterHolidays(int year, Set<LocalDate> holidays) {
        int a = year % 19;
        int b = year % 4;
        int c = year % 7;
        int k = year / 100;
        int p = (13 + 8 * k) / 25;
        int q = k / 4;
        int m = (15 - p + k - q) % 30;
        int n = (4 + k - q) % 7;
        int d = (19 * a + m) % 30;
        int e = (2 * b + 4 * c + 6 * d + n) % 7;
        int day = 22 + d + e;
        int month = 3;

        if (day > 31) {
            day = d + e - 9;
            month = 4;
            if (day == 26) {
                day = 19;
            } else if (day == 25 && d == 28 && e == 6 && a > 10) {
                day = 18;
            }
        }

        LocalDate easter = LocalDate.of(year, month, day);
        holidays.add(easter);
        holidays.add(easter.plusDays(1)); // Lunedì dell'Angelo
    }
}

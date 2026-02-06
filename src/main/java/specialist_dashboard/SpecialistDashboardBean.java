package specialist_dashboard;

public class SpecialistDashboardBean {
    private String nome;
    private String cognome;
    private int unreadNotificationsCount;

    public SpecialistDashboardBean() {
        // Default constructor
    }

    public SpecialistDashboardBean(String nome, String cognome, int unreadNotificationsCount) {
        this.nome = nome;
        this.cognome = cognome;
        this.unreadNotificationsCount = unreadNotificationsCount;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public int getUnreadNotificationsCount() {
        return unreadNotificationsCount;
    }

    public void setUnreadNotificationsCount(int unreadNotificationsCount) {
        this.unreadNotificationsCount = unreadNotificationsCount;
    }
}

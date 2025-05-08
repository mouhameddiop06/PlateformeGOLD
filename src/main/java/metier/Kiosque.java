package metier;

public class Kiosque {
    private int idKiosque;
    private String numeroIdentificationKiosque;
    private String etatKiosque;
    private double latitude;
    private double longitude;
    private int idAgentKiosque;
    private int idAdministrateur;
    private String adresse;
    private String horaires;
    private String statistiques;
    private String emailAgent;

    // Constructeur par défaut
    public Kiosque() {}

    // Constructeur avec tous les champs
    public Kiosque(int idKiosque, String numeroIdentificationKiosque, String etatKiosque, double latitude, double longitude,
                   int idAgentKiosque, int idAdministrateur, String adresse, String horaires, String statistiques, String emailAgent) {
        this.idKiosque = idKiosque;
        this.numeroIdentificationKiosque = numeroIdentificationKiosque;
        this.etatKiosque = etatKiosque;
        this.latitude = latitude;
        this.longitude = longitude;
        this.idAgentKiosque = idAgentKiosque;
        this.idAdministrateur = idAdministrateur;
        this.adresse = adresse;
        this.horaires = horaires;
        this.statistiques = statistiques;
        this.emailAgent = emailAgent;
    }

    // Getters et Setters
    public int getIdKiosque() {
        return idKiosque;
    }

    public void setIdKiosque(int idKiosque) {
        this.idKiosque = idKiosque;
    }

    public String getNumeroIdentificationKiosque() {
        return numeroIdentificationKiosque;
    }

    public void setNumeroIdentificationKiosque(String numeroIdentificationKiosque) {
        this.numeroIdentificationKiosque = numeroIdentificationKiosque;
    }

    public String getEtatKiosque() {
        return etatKiosque;
    }

    public void setEtatKiosque(String etatKiosque) {
        this.etatKiosque = etatKiosque;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getIdAgentKiosque() {
        return idAgentKiosque;
    }

    public void setIdAgentKiosque(int idAgentKiosque) {
        this.idAgentKiosque = idAgentKiosque;
    }

    public int getIdAdministrateur() {
        return idAdministrateur;
    }

    public void setIdAdministrateur(int idAdministrateur) {
        this.idAdministrateur = idAdministrateur;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getHoraires() {
        return horaires;
    }

    public void setHoraires(String horaires) {
        this.horaires = horaires;
    }

    public String getStatistiques() {
        return statistiques;
    }

    public void setStatistiques(String statistiques) {
        this.statistiques = statistiques;
    }

    public String getEmailAgent() {
        return emailAgent;
    }

    public void setEmailAgent(String emailAgent) {
        this.emailAgent = emailAgent;
    }

    @Override
    public String toString() {
        return "Kiosque{" +
                "idKiosque=" + idKiosque +
                ", numeroIdentificationKiosque='" + numeroIdentificationKiosque + '\'' +
                ", etatKiosque='" + etatKiosque + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", idAgentKiosque=" + idAgentKiosque +
                ", idAdministrateur=" + idAdministrateur +
                ", adresse='" + adresse + '\'' +
                ", horaires='" + horaires + '\'' +
                ", statistiques='" + statistiques + '\'' +
                ", emailAgent='" + emailAgent + '\'' +
                '}';
    }
}
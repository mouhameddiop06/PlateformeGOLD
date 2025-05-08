package metier;

public class Personnel extends Utilisateur {
    private int idPersonnel;
    private String numeroIdentificationPers;

    // Getters and Setters
    public int getIdPersonnel() { return idPersonnel; }
    public void setIdPersonnel(int idPersonnel) { this.idPersonnel = idPersonnel; }

    public String getNumeroIdentificationPers() { return numeroIdentificationPers; }
    public void setNumeroIdentificationPers(String numeroIdentificationPers) { this.numeroIdentificationPers = numeroIdentificationPers; }
}

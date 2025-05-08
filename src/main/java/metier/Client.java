package metier;

public class Client extends Utilisateur {
    private int idClient;
    private String numeroIdentificationClient;

    // Getters and Setters
    public int getIdClient() { return idClient; }
    public void setIdClient(int idClient) { this.idClient = idClient; }

    public String getNumeroIdentificationClient() { return numeroIdentificationClient; }
    public void setNumeroIdentificationClient(String numeroIdentificationClient) { this.numeroIdentificationClient = numeroIdentificationClient; }
}

package metier;

public class AgentKiosque extends Utilisateur {

    private int idAgentKiosque;
    private String numeroIdentificationAgent;
    private int idUtilisateur;
    private double gainMensuel;
    private double tauxCommission;

    // Constructor
    public AgentKiosque() {}

    public AgentKiosque(int idAgentKiosque, String numeroIdentificationAgent, int idUtilisateur, double gainMensuel, double tauxCommission) {
        this.idAgentKiosque = idAgentKiosque;
        this.numeroIdentificationAgent = numeroIdentificationAgent;
        this.idUtilisateur = idUtilisateur;
        this.gainMensuel = gainMensuel;
        this.tauxCommission = tauxCommission;
    }

    // Getters and Setters
    public int getIdAgentKiosque() {
        return idAgentKiosque;
    }

    public void setIdAgentKiosque(int idAgentKiosque) {
        this.idAgentKiosque = idAgentKiosque;
    }

    public String getNumeroIdentificationAgent() {
        return numeroIdentificationAgent;
    }

    public void setNumeroIdentificationAgent(String numeroIdentificationAgent) {
        this.numeroIdentificationAgent = numeroIdentificationAgent;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public double getGainMensuel() {
        return gainMensuel;
    }

    public void setGainMensuel(double gainMensuel) {
        this.gainMensuel = gainMensuel;
    }

    public double getTauxCommission() {
        return tauxCommission;
    }

    public void setTauxCommission(double tauxCommission) {
        this.tauxCommission = tauxCommission;
    }

    // toString method for easy debugging
    @Override
    public String toString() {
        return "AgentKiosque{" +
                "idAgentKiosque=" + idAgentKiosque +
                ", numeroIdentificationAgent='" + numeroIdentificationAgent + '\'' +
                ", idUtilisateur=" + idUtilisateur +
                ", gainMensuel=" + gainMensuel +
                ", tauxCommission=" + tauxCommission +
                '}';
    }
}

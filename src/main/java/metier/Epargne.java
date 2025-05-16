package metier;

import java.math.BigDecimal;
import java.util.Date;

public class Epargne {
    private int idEpargne; 
    private String nomEpargne;
    private Date dateCreation;
    private Date dateEcheance;
    private BigDecimal montantActuel;
    private BigDecimal objectifEpargne;
    private String statut;
    private int idCompteClient;

    // Constructeur par défaut
    public Epargne() {}

    // Constructeur avec tous les champs
    public Epargne(int idEpargne, String nomEpargne, Date dateCreation, Date dateEcheance, 
                   BigDecimal montantActuel, BigDecimal objectifEpargne, String statut, int idCompteClient) {
        this.idEpargne = idEpargne;
        this.nomEpargne = nomEpargne;
        this.dateCreation = dateCreation;
        this.dateEcheance = dateEcheance;
        this.montantActuel = montantActuel;
        this.objectifEpargne = objectifEpargne;
        this.statut = statut;
        this.idCompteClient = idCompteClient;
    }

    // Getters et Setters
    public int getIdEpargne() {
        return idEpargne;
    }

    public void setIdEpargne(int idEpargne) {
        this.idEpargne = idEpargne;
    }

    public String getNomEpargne() {
        return nomEpargne;
    }

    public void setNomEpargne(String nomEpargne) {
        this.nomEpargne = nomEpargne;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Date getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(Date dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public BigDecimal getMontantActuel() {
        return montantActuel;
    }

    public void setMontantActuel(BigDecimal montantActuel) {
        this.montantActuel = montantActuel;
    }

    public BigDecimal getObjectifEpargne() {
        return objectifEpargne;
    }

    public void setObjectifEpargne(BigDecimal objectifEpargne) {
        this.objectifEpargne = objectifEpargne;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getIdCompteClient() {
        return idCompteClient;
    }

    public void setIdCompteClient(int idCompteClient) {
        this.idCompteClient = idCompteClient;
    }

    // Méthode toString() pour l'affichage
    @Override
    public String toString() {
        return "Epargne{" +
               "idEpargne=" + idEpargne +
               ", nomEpargne='" + nomEpargne + '\'' +
               ", dateCreation=" + dateCreation +
               ", dateEcheance=" + dateEcheance +
               ", montantActuel=" + montantActuel +
               ", objectifEpargne=" + objectifEpargne +
               ", statut='" + statut + '\'' +
               ", idCompteClient=" + idCompteClient +
               '}';
    }
}

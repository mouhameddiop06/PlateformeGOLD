package metier;

public class ComptePrincipalBenefice {
    private Long idBenefice;
    private double montantBenefice;

    // Constructeurs
    public ComptePrincipalBenefice() {}

    public ComptePrincipalBenefice(Long idBenefice, double montantBenefice) {
        this.idBenefice = idBenefice;
        this.montantBenefice = montantBenefice;
    }

    // Getters et Setters
    public Long getIdBenefice() {
        return idBenefice;
    }

    public void setIdBenefice(Long idBenefice) {
        this.idBenefice = idBenefice;
    }

    public double getMontantBenefice() {
        return montantBenefice;
    }

    public void setMontantBenefice(double montantBenefice) {
        this.montantBenefice = montantBenefice;
    }
}

package metier;

public class Utilisateur {
    private int idUtilisateur;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;  // Nouveau champ
    private String motdepasse;  // Nouveau champ
    private int codesecret;  // Nouveau champ
    private String status;  // 'active' ou 'inactive'
    private String role;  // Exemple : 'Client', 'AgentKiosque', etc.

    // Constructeur par défaut
    public Utilisateur() {}

    // Constructeur avec paramètres (optionnel selon vos besoins)
    public Utilisateur(int idUtilisateur, String nom, String prenom, String email, String telephone, String adresse, String motdepasse, int codesecret, String status, String role) {
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.motdepasse = motdepasse;
        this.codesecret = codesecret;
        this.status = status;
        this.role = role;
    }

    // Getters et Setters pour tous les attributs

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getMotdepasse() {
        return motdepasse;
    }

    public void setMotdepasse(String motdepasse) {
        this.motdepasse = motdepasse;
    }

    public int getCodesecret() {
        return codesecret;
    }

    public void setCodesecret(int codesecret) {
        this.codesecret = codesecret;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Méthode toString() pour faciliter l'affichage des informations de l'utilisateur
    @Override
    public String toString() {
        return "Utilisateur{" +
                "idUtilisateur=" + idUtilisateur +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", adresse='" + adresse + '\'' +
                ", motdepasse='" + motdepasse + '\'' +
                ", codesecret=" + codesecret +
                ", status='" + status + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}

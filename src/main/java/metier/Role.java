package metier;

public class Role {
    private int idRole;
    private String nomRole;

    // Constructeur sans paramètres
    public Role() {}

    // Constructeur avec paramètres
    public Role(int idRole, String nomRole) {
        this.idRole = idRole;
        this.nomRole = nomRole;
    }

    // Getters et Setters
    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }

    public String getNomRole() {
        return nomRole;
    }

    public void setNomRole(String nomRole) {
        this.nomRole = nomRole;
    }

    @Override
    public String toString() {
        return "Role [idRole=" + idRole + ", nomRole=" + nomRole + "]";
    }
}

package dao;

import metier.Utilisateur;
import utilitaires.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {
    private Connection connection;

    public UtilisateurDAO() {
        try {
            this.connection = ConnectDB.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Vérifier si un email existe déjà dans la base de données.
     */
    public boolean emailExiste(String email) {
        String query = "SELECT COUNT(*) FROM Utilisateur WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // Retourne true si l'email existe déjà
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Retourne false si l'email n'existe pas
    }

    /**
     * Ajouter un nouvel utilisateur dans la base de données.
     */
    public int ajouterUtilisateur(Utilisateur utilisateur) {
        // Vérifier si l'email existe déjà
        if (emailExiste(utilisateur.getEmail())) {
            return -1;  // Retourner -1 si l'email existe déjà
        }

        String query = "INSERT INTO Utilisateur(nom, prenom, email, telephone, adresse, motdepasse, codesecret, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getPrenom());
            ps.setString(3, utilisateur.getEmail());
            ps.setString(4, utilisateur.getTelephone());
            ps.setString(5, utilisateur.getAdresse());
            ps.setString(6, utilisateur.getMotdepasse());
            ps.setInt(7, utilisateur.getCodesecret());  // Vérifier que codesecret est un int
            ps.setString(8, utilisateur.getStatus());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);  // Retourne l'ID utilisateur généré
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Retourne -1 si l'insertion échoue
    }

    /**
     * Associer un rôle à l'utilisateur dans la table UtilisateurRole.
     */
    public boolean ajouterUtilisateurRole(int idUtilisateur, String role) {
        String query = "INSERT INTO UtilisateurRole(idUtilisateur, idRole) " +
                "VALUES (?, (SELECT idRole FROM Role WHERE nomRole = ?))";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idUtilisateur);
            ps.setString(2, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Modifier un utilisateur existant dans la base de données.
     */
    public boolean modifierUtilisateur(Utilisateur utilisateur) {
        String query = "UPDATE Utilisateur SET nom = ?, prenom = ?, email = ?, telephone = ?, adresse = ?, motdepasse = ?, codesecret = ? WHERE idUtilisateur = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getPrenom());
            ps.setString(3, utilisateur.getEmail());
            ps.setString(4, utilisateur.getTelephone());
            ps.setString(5, utilisateur.getAdresse());
            ps.setString(6, utilisateur.getMotdepasse());
            ps.setInt(7, utilisateur.getCodesecret());
            ps.setInt(8, utilisateur.getIdUtilisateur());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Trouver un utilisateur par ID (pour utilisation dans la servlet).
     */
    public Utilisateur getUtilisateurById(int idUtilisateur) {
        Utilisateur utilisateur = null;
        String query = "SELECT u.idUtilisateur, u.nom, u.prenom, u.email, u.telephone, u.adresse, u.motdepasse, u.codesecret, u.status, r.nomRole " +
                "FROM Utilisateur u " +
                "JOIN UtilisateurRole ur ON u.idUtilisateur = ur.idUtilisateur " +
                "JOIN Role r ON ur.idRole = r.idRole " +
                "WHERE u.idUtilisateur = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idUtilisateur);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                utilisateur = new Utilisateur();
                utilisateur.setIdUtilisateur(rs.getInt("idUtilisateur"));
                utilisateur.setNom(rs.getString("nom"));
                utilisateur.setPrenom(rs.getString("prenom"));
                utilisateur.setEmail(rs.getString("email"));
                utilisateur.setTelephone(rs.getString("telephone"));
                utilisateur.setAdresse(rs.getString("adresse"));
                utilisateur.setMotdepasse(rs.getString("motdepasse"));
                utilisateur.setCodesecret(rs.getInt("codesecret"));
                utilisateur.setStatus(rs.getString("status"));
                utilisateur.setRole(rs.getString("nomRole"));   // Ajouter le rôle à l'utilisateur
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateur;
    }

    /**
     * Lister les utilisateurs selon leur rôle (Client, AgentKiosque, Personnel, etc.).
     */
    public List<Utilisateur> listerUtilisateursParRole(String role) {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String query = "SELECT u.idUtilisateur, u.nom, u.prenom, u.email, u.telephone, u.adresse, u.motdepasse, u.codesecret, u.status " +
                "FROM Utilisateur u " +
                "JOIN UtilisateurRole ur ON u.idUtilisateur = ur.idUtilisateur " +
                "JOIN Role r ON ur.idRole = r.idRole " +
                "WHERE r.nomRole = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setIdUtilisateur(rs.getInt("idUtilisateur"));
                utilisateur.setNom(rs.getString("nom"));
                utilisateur.setPrenom(rs.getString("prenom"));
                utilisateur.setEmail(rs.getString("email"));
                utilisateur.setTelephone(rs.getString("telephone"));
                utilisateur.setAdresse(rs.getString("adresse"));
                utilisateur.setMotdepasse(rs.getString("motdepasse"));
                utilisateur.setCodesecret(rs.getInt("codesecret"));
                utilisateur.setStatus(rs.getString("status"));  // Gérer le statut de l'utilisateur (active/inactive)
                utilisateur.setRole(role);  // Définir le rôle
                utilisateurs.add(utilisateur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateurs;
    }

    /**
     * Activer ou désactiver un utilisateur (mettre à jour son statut).
     */
    public boolean changerStatutUtilisateur(int idUtilisateur, String statut) {
        String query = "UPDATE Utilisateur SET status = ? WHERE idUtilisateur = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, statut);
            ps.setInt(2, idUtilisateur);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retourne le nombre d'utilisateurs par rôle.
     */
    public int compterUtilisateursParRole(String role) {
        String query = "SELECT COUNT(*) FROM Utilisateur u " +
                "JOIN UtilisateurRole ur ON u.idUtilisateur = ur.idUtilisateur " +
                "JOIN Role r ON ur.idRole = r.idRole " +
                "WHERE r.nomRole = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);  // Retourne le nombre d'utilisateurs pour ce rôle
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Supprimer un utilisateur.
     */
    public boolean supprimerUtilisateur(int idUtilisateur) {
        String deleteRoleQuery = "DELETE FROM UtilisateurRole WHERE idUtilisateur = ?";
        String deleteUserQuery = "DELETE FROM Utilisateur WHERE idUtilisateur = ?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement psRole = connection.prepareStatement(deleteRoleQuery);
                 PreparedStatement psUser = connection.prepareStatement(deleteUserQuery)) {

                psRole.setInt(1, idUtilisateur);
                int rowsAffectedRole = psRole.executeUpdate();

                psUser.setInt(1, idUtilisateur);
                int rowsAffectedUser = psUser.executeUpdate();

                if (rowsAffectedUser > 0) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Erreur lors du rollback: " + ex.getMessage());
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Erreur lors de la réinitialisation de l'autocommit: " + e.getMessage());
            }
        }
    }
}

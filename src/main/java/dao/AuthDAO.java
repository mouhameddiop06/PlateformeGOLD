package dao;

import metier.Utilisateur;
import utilitaires.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDAO {
    private Connection connection;

    public AuthDAO() {
        try {
            this.connection = ConnectDB.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Authentifier un utilisateur avec email et mot de passe
     */
    public Utilisateur authentifier(String email, String motdepasse) {
        Utilisateur utilisateur = null;
        String query = "SELECT * FROM Utilisateur WHERE email = ? AND motdepasse = ?";
        
        System.out.println("Tentative d'authentification avec l'email : " + email + " et le mot de passe : " + motdepasse);

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, motdepasse);  // Assurez-vous que le mot de passe est en clair ou haché

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                utilisateur = new Utilisateur();
                utilisateur.setIdUtilisateur(rs.getInt("idUtilisateur"));
                utilisateur.setNom(rs.getString("nom"));
                utilisateur.setPrenom(rs.getString("prenom"));
                utilisateur.setEmail(rs.getString("email"));
                utilisateur.setMotdepasse(rs.getString("motdepasse"));
                // Ajoutez les autres champs nécessaires
            } else {
                System.out.println("Aucun utilisateur trouvé pour cet email et mot de passe");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return utilisateur;
    }
}

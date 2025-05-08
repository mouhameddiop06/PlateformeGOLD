package dao;

import utilitaires.ConnectDB;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONObject;
import java.security.SecureRandom;

public class ResetCodeDAO {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public JSONObject verifierUtilisateur(String telephone) throws SQLException {
        // Ajout de logs pour le débogage
        System.out.println("Recherche de l'utilisateur avec le numéro: " + telephone);

        String query = "SELECT u.nom, u.prenom, u.email FROM Utilisateur u WHERE u.telephone = ?";
        
        try (PreparedStatement pst = ConnectDB.creerStatement(query)) {
            pst.setString(1, telephone);
            
            // Log de la requête
            System.out.println("Requête SQL: " + query);
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                JSONObject userInfo = new JSONObject();
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String email = rs.getString("email");
                
                // Log des informations trouvées
                System.out.println("Utilisateur trouvé:");
                System.out.println("Nom: " + nom);
                System.out.println("Prénom: " + prenom);
                System.out.println("Email: " + email);
                
                userInfo.put("nom", nom);
                userInfo.put("prenom", prenom);
                userInfo.put("email", email);
                return userInfo;
            } else {
                System.out.println("Aucun utilisateur trouvé pour le numéro: " + telephone);
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la vérification de l'utilisateur:");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String genererNouveauCode() {
        return String.format("%04d", SECURE_RANDOM.nextInt(10000));
    }

    public boolean updateCodeSecret(String telephone, String nouveauCode) throws SQLException {
        String query = "UPDATE Utilisateur SET codeSecret = ? WHERE telephone = ?";
        
        try (PreparedStatement pst = ConnectDB.creerStatement(query)) {
            pst.setInt(1, Integer.parseInt(nouveauCode));
            pst.setString(2, telephone);
            
            // Log de la mise à jour
            System.out.println("Mise à jour du code secret pour le numéro: " + telephone);
            System.out.println("Nouveau code: " + nouveauCode);
            
            int result = pst.executeUpdate();
            
            // Log du résultat
            System.out.println("Nombre de lignes mises à jour: " + result);
            
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la mise à jour du code secret:");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // Méthode de test pour vérifier directement dans la base de données
    public void testConnexion(String telephone) {
        try {
            String query = "SELECT COUNT(*) as total FROM Utilisateur WHERE telephone = ?";
            try (PreparedStatement pst = ConnectDB.creerStatement(query)) {
                pst.setString(1, telephone);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    System.out.println("Nombre total d'utilisateurs avec ce numéro: " + rs.getInt("total"));
                }
            }

            // Afficher toutes les colonnes pour ce numéro
            query = "SELECT * FROM Utilisateur WHERE telephone = ?";
            try (PreparedStatement pst = ConnectDB.creerStatement(query)) {
                pst.setString(1, telephone);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    System.out.println("Détails de l'utilisateur trouvé:");
                    System.out.println("ID: " + rs.getInt("idUtilisateur"));
                    System.out.println("Nom: " + rs.getString("nom"));
                    System.out.println("Prénom: " + rs.getString("prenom"));
                    System.out.println("Email: " + rs.getString("email"));
                    System.out.println("Téléphone: " + rs.getString("telephone"));
                } else {
                    System.out.println("Aucune donnée trouvée dans la table Utilisateur");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du test de connexion:");
            e.printStackTrace();
        }
    }
}
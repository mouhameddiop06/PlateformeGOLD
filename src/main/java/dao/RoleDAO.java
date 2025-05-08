package dao;

import utilitaires.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleDAO {
    private Connection connection;

    public RoleDAO() {
        try {
            this.connection = ConnectDB.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupérer le rôle d'un utilisateur à partir de son idUtilisateur
     */
    public String getRoleByUserId(int idUtilisateur) {
        String role = null;
        String query = "SELECT r.nomRole FROM Role r " +
                       "JOIN UtilisateurRole ur ON r.idRole = ur.idRole " +
                       "WHERE ur.idUtilisateur = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idUtilisateur);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                role = rs.getString("nomRole");  // Récupérer le rôle
                System.out.println("Rôle récupéré pour idUtilisateur " + idUtilisateur + " : " + role);
            } else {
                System.out.println("Aucun rôle trouvé pour idUtilisateur " + idUtilisateur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return role;
    }

}

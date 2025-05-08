package dao;

import java.sql.Connection;
import utilitaires.ConnectDB;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdministrateurDAO {
    private Connection connection;

    public AdministrateurDAO() throws SQLException {
        connection = ConnectDB.getInstance().getConnection();
    }

    // Méthode pour obtenir l'ID de l'administrateur à partir de l'ID utilisateur
    public int getIdAdministrateurByUserId(int idUtilisateur) throws SQLException {
        String query = "SELECT idAdministrateur FROM Administrateur WHERE idUtilisateur = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idAdministrateur");
                } else {
                    return -1; // Retourne -1 si aucun administrateur n'est trouvé
                }
            }
        }
    }
}

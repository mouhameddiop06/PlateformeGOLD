package dao;

import metier.Client;
import utilitaires.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClientDAO {
    private Connection connection;

    public ClientDAO() throws SQLException {
        connection = ConnectDB.getInstance().getConnection();
    }

    public void ajouterClient(Client client) {
        String query = "INSERT INTO Client (utilisateurId) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, client.getIdUtilisateur());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

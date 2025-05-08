package dao;

import utilitaires.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticDAO {
    private Connection connection;

    public StatisticDAO() {
        try {
            this.connection = ConnectDB.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTotalUtilisateurs() {
        String query = "SELECT COUNT(*) AS total FROM Utilisateur";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalKiosquesActifs() {
        String query = "SELECT COUNT(*) AS total FROM Kiosque WHERE etatKiosque = 'actif'";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getTotalMontantTransactions() {
        String query = "SELECT SUM(montant) AS total FROM Transaction";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getNombreClients() {
        String query = "SELECT COUNT(*) AS total FROM UtilisateurRole WHERE idRole = (SELECT idRole FROM Role WHERE nomRole = 'Client')";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getNombreAgentsKiosque() {
        String query = "SELECT COUNT(*) AS total FROM UtilisateurRole WHERE idRole = (SELECT idRole FROM Role WHERE nomRole = 'AgentKiosque')";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getNombrePersonnels() {
        String query = "SELECT COUNT(*) AS total FROM UtilisateurRole WHERE idRole = (SELECT idRole FROM Role WHERE nomRole = 'Personnel')";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getNombreAdmins() {
        String query = "SELECT COUNT(*) AS total FROM UtilisateurRole WHERE idRole = (SELECT idRole FROM Role WHERE nomRole = 'Admin')";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getTransactionsParMois() {
        StringBuilder sb = new StringBuilder();
        String query = "SELECT EXTRACT(MONTH FROM date) AS mois, SUM(montant) AS total FROM Transaction GROUP BY mois ORDER BY mois";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (sb.length() > 0) sb.append(",");
                sb.append(rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}

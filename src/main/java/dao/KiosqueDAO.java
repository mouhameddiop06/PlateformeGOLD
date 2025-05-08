package dao;

import metier.Kiosque;
import utilitaires.ConnectDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import java.sql.Types;

public class KiosqueDAO {
    private Connection connection;

    public KiosqueDAO() {
        try {
            this.connection = ConnectDB.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour lister tous les kiosques
    public List<Kiosque> obtenirTousLesKiosques() throws SQLException {
        List<Kiosque> kiosques = new ArrayList<>();
        String sql = "SELECT k.*, a.email FROM Kiosque k LEFT JOIN AgentKiosque ak ON k.idAgentKiosque = ak.idAgentKiosque LEFT JOIN Utilisateur a ON ak.idUtilisateur = a.idUtilisateur";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Kiosque kiosque = new Kiosque();
                kiosque.setIdKiosque(rs.getInt("idKiosque"));
                kiosque.setNumeroIdentificationKiosque(rs.getString("numeroIdentificationKiosque"));
                kiosque.setEtatKiosque(rs.getString("etatKiosque"));
                kiosque.setLatitude(rs.getDouble("latitude"));
                kiosque.setLongitude(rs.getDouble("longitude"));
                kiosque.setAdresse(rs.getString("adresse"));
                kiosque.setIdAgentKiosque(rs.getInt("idAgentKiosque"));
                kiosque.setEmailAgent(rs.getString("email"));
                kiosques.add(kiosque);
            }
        }
        return kiosques;
    }

    // Méthode pour générer un numéro d'identification unique pour un kiosque
    public String genererNumeroIdentificationKiosque() {
        String query = "SELECT MAX(CAST(SUBSTRING(numeroIdentificationKiosque, 8) AS INTEGER)) FROM Kiosque";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int maxNum = rs.getInt(1);
                return String.format("KIOSQUE%04d", maxNum + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "KIOSQUE0001";  // Valeur par défaut si aucun kiosque n'existe encore
    }

    // Méthode pour ajouter un nouveau kiosque
    public boolean ajouterKiosque(Kiosque kiosque) {
        String numeroIdentificationKiosque = genererNumeroIdentificationKiosque();
        kiosque.setNumeroIdentificationKiosque(numeroIdentificationKiosque);

        String query = "INSERT INTO Kiosque (numeroIdentificationKiosque, etatKiosque, latitude, longitude, idAgentKiosque, idAdministrateur, adresse, horaires) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            if (connection == null || connection.isClosed()) {
                System.out.println("La connexion à la base de données est fermée ou nulle");
                return false;
            }

            if (kiosque.getEtatKiosque() == null || kiosque.getAdresse() == null || kiosque.getHoraires() == null) {
                System.out.println("Certains champs requis sont null");
                return false;
            }

            ps.setString(1, kiosque.getNumeroIdentificationKiosque());
            ps.setString(2, kiosque.getEtatKiosque());
            ps.setDouble(3, kiosque.getLatitude());
            ps.setDouble(4, kiosque.getLongitude());
            ps.setInt(5, kiosque.getIdAgentKiosque());
            ps.setInt(6, kiosque.getIdAdministrateur());
            ps.setString(7, kiosque.getAdresse());

            // Conversion des horaires en JSON
            JSONObject horaireJson = new JSONObject();
            horaireJson.put("horaires", kiosque.getHoraires());
            ps.setObject(8, horaireJson.toString(), Types.OTHER);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erreur SQL lors de l'ajout du kiosque: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Méthode pour modifier un kiosque existant
    public boolean modifierKiosque(Kiosque kiosque) {
        String query = "UPDATE Kiosque SET numeroIdentificationKiosque = ?, etatKiosque = ?, latitude = ?, longitude = ?, idAgentKiosque = ?, adresse = ?, horaires = ?::jsonb " +
                       "WHERE idKiosque = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, kiosque.getNumeroIdentificationKiosque());
            ps.setString(2, kiosque.getEtatKiosque());
            ps.setDouble(3, kiosque.getLatitude());
            ps.setDouble(4, kiosque.getLongitude());
            ps.setInt(5, kiosque.getIdAgentKiosque());
            ps.setString(6, kiosque.getAdresse());

            // Conversion des horaires en JSON
            JSONObject horaireJson = new JSONObject();
            horaireJson.put("horaires", kiosque.getHoraires());
            ps.setObject(7, horaireJson.toString(), Types.OTHER);

            ps.setInt(8, kiosque.getIdKiosque());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Erreur SQL lors de la modification du kiosque: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Méthode pour supprimer un kiosque par son ID
    public boolean supprimerKiosque(int idKiosque) {
        String query = "DELETE FROM Kiosque WHERE idKiosque = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idKiosque);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Méthode pour obtenir un kiosque par son ID
    public Kiosque getKiosqueById(int idKiosque) {
        Kiosque kiosque = null;
        String query = "SELECT * FROM Kiosque WHERE idKiosque = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idKiosque);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                kiosque = new Kiosque();
                kiosque.setIdKiosque(rs.getInt("idKiosque"));
                kiosque.setNumeroIdentificationKiosque(rs.getString("numeroIdentificationKiosque"));
                kiosque.setEtatKiosque(rs.getString("etatKiosque"));
                kiosque.setLatitude(rs.getDouble("latitude"));
                kiosque.setLongitude(rs.getDouble("longitude"));
                kiosque.setAdresse(rs.getString("adresse"));
                kiosque.setHoraires(rs.getString("horaires"));
                kiosque.setIdAgentKiosque(rs.getInt("idAgentKiosque"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return kiosque;
    }

    // Méthode pour compter les kiosques actifs
    public int compterKiosquesActifs() {
        int nombreKiosquesActifs = 0;
        String query = "SELECT COUNT(*) AS total FROM Kiosque WHERE etatKiosque = 'Actif'";

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                nombreKiosquesActifs = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nombreKiosquesActifs;
    }

    // Méthode pour compter les kiosques inactifs
    public int compterKiosquesInactifs() {
        int nombreKiosquesInactifs = 0;
        String query = "SELECT COUNT(*) AS total FROM Kiosque WHERE etatKiosque = 'Inactif'";

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                nombreKiosquesInactifs = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nombreKiosquesInactifs;
    }
}

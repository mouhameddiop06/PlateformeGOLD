package dao;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
public class TransfertDiffereDAO {
    private Connection connection;

    public TransfertDiffereDAO(Connection connection) {
        this.connection = connection;
    }

    public void programmerTransfertDiffere(int idCompteSource, String numeroDestinataire, double montant, LocalDateTime dateExecution) throws SQLException {
        connection.setAutoCommit(false);
        try {
            int idClientDestinataire = verifierContact(numeroDestinataire);
            if (idClientDestinataire == -1) {
                throw new SQLException("Numéro de téléphone non trouvé dans les contacts");
            }

            if (!verifierSolde(idCompteSource, montant)) {
                throw new SQLException("Solde insuffisant pour programmer le transfert différé");
            }

            int idCompteDestinataire = getCompteIdByClientId(idClientDestinataire);
            long idTransaction = insererTransaction(idCompteSource, idCompteDestinataire, montant, dateExecution);
            insererTransfertDiffere(idTransaction, dateExecution);

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void executerTransfertsDifferes() throws SQLException {
        List<Long> transfertsAExecuter = getTransfertsDifferesAExecuter();
        for (Long idTransfertDiffere : transfertsAExecuter) {
            executerTransfertDiffere(idTransfertDiffere);
        }
    }

    public void executerTransfertDiffere(Long idTransfertDiffere) throws SQLException {
        connection.setAutoCommit(false);
        try {
            JSONObject detailsTransfert = getDetailsTransfertDiffere(idTransfertDiffere);
            
            long idCompteSource = (Long) detailsTransfert.get("comptesource");
            long idCompteDestinataire = (Long) detailsTransfert.get("comptedestinataire");
            double montant = (Double) detailsTransfert.get("montant");

            if (verifierSoldeDisponible(idCompteSource, montant)) {
                mettreAJourSoldeCompte(idCompteSource, montant, false);
                mettreAJourSoldeCompte(idCompteDestinataire, montant, true);
                mettreAJourStatutTransfertDiffere(idTransfertDiffere, "exécuté");
                mettreAJourStatutTransaction((Long) detailsTransfert.get("idTransaction"), "effectué");
            } else {
                mettreAJourStatutTransfertDiffere(idTransfertDiffere, "échoué");
                mettreAJourStatutTransaction((Long) detailsTransfert.get("idTransaction"), "échoué");
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private int verifierContact(String numeroDestinataire) throws SQLException {
        String sql = "SELECT idClient FROM Contact WHERE numeroTelephone = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, numeroDestinataire);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt("idClient") : -1;
        }
    }

    private boolean verifierSolde(long idCompteSource, double montant) throws SQLException {
        String sql = "SELECT solde FROM Compte WHERE id_compte = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, idCompteSource);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getDouble("solde") >= montant;
        }
    }

    private int getCompteIdByClientId(int idClient) throws SQLException {
        String sql = "SELECT cc.id_compte FROM CompteClient cc WHERE cc.idClient = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idClient);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_compte");
            } else {
                throw new SQLException("Aucun compte trouvé pour le client avec l'ID: " + idClient);
            }
        }
    }

    private long insererTransaction(long idCompteSource, long idCompteDestinataire, double montant, LocalDateTime dateExecution) throws SQLException {
        String sql = "INSERT INTO Transaction (montant, date, statut, heure, id_optiontransaction, comptesource, comptedestinataire) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING idTransaction";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, montant);
            pstmt.setDate(2, Date.valueOf(dateExecution.toLocalDate()));
            pstmt.setString(3, "programmé");
            pstmt.setTime(4, Time.valueOf(dateExecution.toLocalTime()));
            pstmt.setInt(5, getIdOptionTransaction("transfert_differe_avec_temps"));
            pstmt.setLong(6, idCompteSource);
            pstmt.setLong(7, idCompteDestinataire);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getLong(1) : -1L;
        }
    }

    private void insererTransfertDiffere(long idTransaction, LocalDateTime dateExecution) throws SQLException {
        String sql = "INSERT INTO TransfertDiffere (idTransaction, dateExecution) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, idTransaction);
            pstmt.setTimestamp(2, Timestamp.valueOf(dateExecution));
            pstmt.executeUpdate();
        }
    }

    private int getIdOptionTransaction(String nomOption) throws SQLException {
        String sql = "SELECT idOption FROM OptionTransaction WHERE nomOption = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nomOption);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("idOption");
            } else {
                throw new SQLException("Option de transaction non trouvée: " + nomOption);
            }
        }
    }

    private boolean verifierSoldeDisponible(long idCompte, double montant) throws SQLException {
        String sql = "SELECT solde FROM Compte WHERE id_compte = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, idCompte);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("solde") >= montant;
            } else {
                throw new SQLException("Compte non trouvé avec l'ID: " + idCompte);
            }
        }
    }

    private void mettreAJourSoldeCompte(long idCompte, double montant, boolean estCredit) throws SQLException {
        String sql = "UPDATE Compte SET solde = solde " + (estCredit ? "+" : "-") + " ? WHERE id_compte = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, montant);
            pstmt.setLong(2, idCompte);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("La mise à jour du solde a échoué pour le compte avec l'ID: " + idCompte);
            }
        }
    }

    private void mettreAJourStatutTransfertDiffere(long idTransfertDiffere, String statut) throws SQLException {
        String sql = "UPDATE TransfertDiffere SET statut = ? WHERE idTransfertDiffere = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, statut);
            pstmt.setLong(2, idTransfertDiffere);
            pstmt.executeUpdate();
        }
    }

    private void mettreAJourStatutTransaction(long idTransaction, String statut) throws SQLException {
        String sql = "UPDATE Transaction SET statut = ? WHERE idTransaction = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, statut);
            pstmt.setLong(2, idTransaction);
            pstmt.executeUpdate();
        }
    }

   public List<Long> getTransfertsDifferesAExecuter() throws SQLException {
        List<Long> transferts = new ArrayList<>();
        String sql = "SELECT idTransfertDiffere FROM TransfertDiffere WHERE dateExecution <= CURRENT_TIMESTAMP AND statut = 'en_attente'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transferts.add(rs.getLong("idTransfertDiffere"));
            }
        }
        return transferts;
    }

    
    private JSONObject getDetailsTransfertDiffere(long idTransfertDiffere) throws SQLException {
        String sql = "SELECT td.idTransfertDiffere, t.idTransaction, t.montant, t.comptesource, t.comptedestinataire " +
                     "FROM TransfertDiffere td JOIN Transaction t ON td.idTransaction = t.idTransaction " +
                     "WHERE td.idTransfertDiffere = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, idTransfertDiffere);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JSONObject details = new JSONObject();
                details.put("idTransfertDiffere", rs.getLong("idTransfertDiffere"));
                details.put("idTransaction", rs.getLong("idTransaction"));
                details.put("montant", rs.getDouble("montant"));
                details.put("comptesource", rs.getLong("comptesource"));
                details.put("comptedestinataire", rs.getLong("comptedestinataire"));
                return details;
            }
            throw new SQLException("Transfert différé non trouvé");
        }
    }

    public JSONObject getDetailsCompte(long idCompte) throws SQLException {
        String sql = "SELECT c.id_compte, c.solde, u.nom, u.prenom, u.email FROM Compte c " +
                     "JOIN CompteClient cc ON c.id_compte = cc.id_compte " +
                     "JOIN Client cl ON cc.idClient = cl.idClient " +
                     "JOIN Utilisateur u ON cl.idUtilisateur = u.idUtilisateur " +
                     "WHERE c.id_compte = ?";
        JSONObject details = new JSONObject();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, idCompte);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                details.put("id_compte", rs.getLong("id_compte"));
                details.put("solde", rs.getDouble("solde"));
                details.put("nom", rs.getString("nom"));
                details.put("prenom", rs.getString("prenom"));
                details.put("email", rs.getString("email"));
            }
        }
        return details;
    }

    public JSONArray getTransfertsDifferes() throws SQLException {
        JSONArray result = new JSONArray();
        String sql = "SELECT td.idTransfertDiffere, t.montant, t.comptesource, t.comptedestinataire, td.dateExecution, td.statut " +
                     "FROM TransfertDiffere td JOIN Transaction t ON td.idTransaction = t.idTransaction " +
                     "ORDER BY td.dateExecution";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                JSONObject transfert = new JSONObject();
                transfert.put("idTransfertDiffere", rs.getLong("idTransfertDiffere"));
                transfert.put("montant", rs.getDouble("montant"));
                transfert.put("compteSource", rs.getLong("comptesource"));
                transfert.put("compteDestinataire", rs.getLong("comptedestinataire"));
                transfert.put("dateExecution", rs.getTimestamp("dateExecution").toString());
                transfert.put("statut", rs.getString("statut"));
                result.add(transfert);
            }
        }
        return result;
    }
} 

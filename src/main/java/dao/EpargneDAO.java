package dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import metier.Epargne;

public class EpargneDAO {
    private Connection connection;

    public EpargneDAO(Connection connection) {
        this.connection = connection;
    }

    // Méthode pour créer une épargne
    public void creerEpargne(Epargne epargne) throws SQLException {
        if (connection == null) {
            throw new SQLException("La connexion à la base de données est null. Impossible d'insérer l'épargne.");
        }

        String sql = "INSERT INTO Epargne (nomEpargne, dateCreation, dateEcheance, objectifEpargne, idCompteClient) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Affichage des valeurs à insérer pour débogage
            System.out.println("Nom Epargne: " + epargne.getNomEpargne());
            System.out.println("Date Creation: " + epargne.getDateCreation());
            System.out.println("Date Echeance: " + epargne.getDateEcheance());
            System.out.println("Objectif Epargne: " + epargne.getObjectifEpargne());
            System.out.println("ID Compte Client: " + epargne.getIdCompteClient());

            pstmt.setString(1, epargne.getNomEpargne());
            pstmt.setDate(2, new java.sql.Date(epargne.getDateCreation().getTime()));
            pstmt.setDate(3, new java.sql.Date(epargne.getDateEcheance().getTime()));
            pstmt.setBigDecimal(4, epargne.getObjectifEpargne());
            pstmt.setInt(5, epargne.getIdCompteClient());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("L'insertion de l'épargne a échoué, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    epargne.setIdEpargne(generatedKeys.getInt(1));
                    System.out.println("Épargne créée avec succès avec l'ID: " + epargne.getIdEpargne());
                } else {
                    throw new SQLException("L'insertion de l'épargne a échoué, aucun ID généré.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion de l'épargne: " + e.getMessage());
            throw e; // Rejeter l'exception pour qu'elle puisse être capturée par l'appelant
        }
    }


    // Méthode pour ajouter un montant à l'épargne
    public void ajouterMontantEpargne(int idEpargne, BigDecimal montant) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Débiter le compte client
            String sqlDebit = "UPDATE Compte SET solde = solde - ? WHERE id_compte = (SELECT c.id_compte FROM Compte c JOIN CompteClient cc ON c.id_compte = cc.id_compte JOIN Epargne e ON cc.idCompteClient = e.idCompteClient WHERE e.idEpargne = ?)";
            try (PreparedStatement pstmtDebit = connection.prepareStatement(sqlDebit)) {
                pstmtDebit.setBigDecimal(1, montant);
                pstmtDebit.setInt(2, idEpargne);
                pstmtDebit.executeUpdate();
            }

            // Créditer l'épargne
            String sqlCredit = "UPDATE Epargne SET montantActuel = montantActuel + ? WHERE idEpargne = ?";
            try (PreparedStatement pstmtCredit = connection.prepareStatement(sqlCredit)) {
                pstmtCredit.setBigDecimal(1, montant);
                pstmtCredit.setInt(2, idEpargne);
                pstmtCredit.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }


    // Méthode pour bloquer une épargne
    public void bloquerEpargne(int idEpargne) throws SQLException {
        String sql = "UPDATE Epargne SET statut = 'bloqué' WHERE idEpargne = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idEpargne);
            pstmt.executeUpdate();
        }
    }

    // Méthode pour retirer un montant de l'épargne
    public void retirerMontantEpargne(int idEpargne, BigDecimal montant) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Vérifier si l'épargne n'est pas bloquée
            String sqlVerif = "SELECT statut FROM Epargne WHERE idEpargne = ?";
            try (PreparedStatement pstmtVerif = connection.prepareStatement(sqlVerif)) {
                pstmtVerif.setInt(1, idEpargne);
                try (ResultSet rs = pstmtVerif.executeQuery()) {
                    if (rs.next() && "bloqué".equals(rs.getString("statut"))) {
                        throw new SQLException("L'épargne est bloquée et ne peut pas être retirée.");
                    }
                }
            }

            // Débiter l'épargne
            String sqlDebit = "UPDATE Epargne SET montantActuel = montantActuel - ? WHERE idEpargne = ?";
            try (PreparedStatement pstmtDebit = connection.prepareStatement(sqlDebit)) {
                pstmtDebit.setBigDecimal(1, montant);
                pstmtDebit.setInt(2, idEpargne);
                pstmtDebit.executeUpdate();
            }

            // Créditer le compte client
            String sqlCredit = "UPDATE Compte SET solde = solde + ? WHERE id_compte = (SELECT c.id_compte FROM Compte c JOIN CompteClient cc ON c.id_compte = cc.id_compte JOIN Epargne e ON cc.idCompteClient = e.idCompteClient WHERE e.idEpargne = ?)";
            try (PreparedStatement pstmtCredit = connection.prepareStatement(sqlCredit)) {
                pstmtCredit.setBigDecimal(1, montant);
                pstmtCredit.setInt(2, idEpargne);
                pstmtCredit.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Méthode pour supprimer une épargne
    public void supprimerEpargne(int idEpargne) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Récupérer le montant actuel de l'épargne
            BigDecimal montantActuel = BigDecimal.ZERO;
            String sqlMontant = "SELECT montantActuel FROM Epargne WHERE idEpargne = ?";
            try (PreparedStatement pstmtMontant = connection.prepareStatement(sqlMontant)) {
                pstmtMontant.setInt(1, idEpargne);
                try (ResultSet rs = pstmtMontant.executeQuery()) {
                    if (rs.next()) {
                        montantActuel = rs.getBigDecimal("montantActuel");
                    }
                }
            }

            // Créditer le compte client avec le montant actuel de l'épargne
            if (montantActuel.compareTo(BigDecimal.ZERO) > 0) {
                String sqlCredit = "UPDATE Compte SET solde = solde + ? WHERE id_compte = (SELECT c.id_compte FROM Compte c JOIN CompteClient cc ON c.id_compte = cc.id_compte JOIN Epargne e ON cc.idCompteClient = e.idCompteClient WHERE e.idEpargne = ?)";
                try (PreparedStatement pstmtCredit = connection.prepareStatement(sqlCredit)) {
                    pstmtCredit.setBigDecimal(1, montantActuel);
                    pstmtCredit.setInt(2, idEpargne);
                    pstmtCredit.executeUpdate();
                }
            }

            // Supprimer l'épargne
            String sqlDelete = "DELETE FROM Epargne WHERE idEpargne = ?";
            try (PreparedStatement pstmtDelete = connection.prepareStatement(sqlDelete)) {
                pstmtDelete.setInt(1, idEpargne);
                pstmtDelete.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Méthode pour récupérer les épargnes d'un client
 // Mise à jour de la méthode dans EpargneDAO.java

    public List<Epargne> getEpargnesByCompteClient(int idCompteClient) throws SQLException {
        List<Epargne> epargnes = new ArrayList<>();
        String sql = "SELECT DISTINCT e.* FROM Epargne e WHERE e.idCompteClient = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idCompteClient);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Epargne epargne = new Epargne();
                    epargne.setIdEpargne(rs.getInt("idEpargne"));
                    epargne.setNomEpargne(rs.getString("nomEpargne"));
                    epargne.setDateCreation(rs.getDate("dateCreation"));
                    epargne.setDateEcheance(rs.getDate("dateEcheance"));
                    epargne.setMontantActuel(BigDecimal.valueOf(rs.getDouble("montantActuel")));
                    epargne.setObjectifEpargne(BigDecimal.valueOf(rs.getDouble("objectifEpargne")));
                    epargne.setStatut(rs.getString("statut"));
                    epargnes.add(epargne);
                }
            }
        }
        return epargnes;
    }

}

package dao;

import utilitaires.ConnectDB;
import metier.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private Connection connection;

    public TransactionDAO() {
        try {
            this.connection = ConnectDB.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtenir le montant total des transactions
     */
    public int totalTransactions() {
        String query = "SELECT SUM(montant) FROM Transaction";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);  // Retourne la somme des montants des transactions
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;  // Si pas de transactions, retourne 0
    }

    
     public int transactionsDuJour() {
    String query = "SELECT SUM(montant) FROM Transaction WHERE date = CURRENT_DATE";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);  // Retourne la somme des montants pour aujourd'hui
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0;  // Si aucune transaction aujourd'hui, retourne 0
}


    /**
     * Obtenir le montant des transactions par mois pour l'année en cours
     */
     public int[] transactionsParMois() {
         int[] transactionsParMois = new int[12];
         Arrays.fill(transactionsParMois, 0); // Initialiser tous les mois à zéro

         // Utilisation de la colonne 'date' au lieu de 'dateTransaction'
         String query = "SELECT EXTRACT(MONTH FROM date) AS mois, SUM(montant) AS totalMois " +
                        "FROM Transaction " +
                        "WHERE EXTRACT(YEAR FROM date) = EXTRACT(YEAR FROM CURRENT_DATE) " +
                        "GROUP BY mois";

         try (PreparedStatement ps = connection.prepareStatement(query)) {
             ResultSet rs = ps.executeQuery();
             while (rs.next()) {
                 int mois = rs.getInt("mois");
                 int totalMois = rs.getInt("totalMois");
                 transactionsParMois[mois - 1] = totalMois;  // Index de tableau commence à 0, mois commence à 1
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
         return transactionsParMois;
     }

   
    
    
    /**
     * Nouvelle méthode pour récupérer toutes les transactions avec comptes source/destinataire et type de transaction
     */
   
     public List<Transaction> getAllTransactions() {
    	    List<Transaction> transactions = new ArrayList<>();
    	    String query = "SELECT " +
    	        "t.idTransaction, " +
    	        "t.montant, " +
    	        "t.date, " +
    	        "t.heure, " +
    	        "t.statut, " +
    	        "opt.nomOption AS typeTransaction, " +
    	        "COALESCE(us1.telephone, us_agent1.telephone) AS telephoneSource, " +
    	        "COALESCE(us2.telephone, us_agent2.telephone) AS telephoneDestinataire " +
    	        "FROM Transaction t " +
    	        // Join avec OptionTransaction pour le type de transaction
    	        "LEFT JOIN OptionTransaction opt ON t.id_optiontransaction = opt.idOption " +
    	        
    	        // Joins pour le compte source (client)
    	        "LEFT JOIN Compte c1 ON t.comptesource = c1.id_compte " +
    	        "LEFT JOIN CompteClient cc1 ON c1.id_compte = cc1.id_compte " +
    	        "LEFT JOIN Client cl1 ON cc1.idClient = cl1.idClient " +
    	        "LEFT JOIN Utilisateur us1 ON cl1.idUtilisateur = us1.idUtilisateur " +
    	        
    	        // Joins pour le compte source (agent)
    	        "LEFT JOIN CompteAgent ca1 ON c1.id_compte = ca1.id_compte " +
    	        "LEFT JOIN AgentKiosque ak1 ON ca1.idAgentKiosque = ak1.idAgentKiosque " +
    	        "LEFT JOIN Utilisateur us_agent1 ON ak1.idUtilisateur = us_agent1.idUtilisateur " +
    	        
    	        // Joins pour le compte destinataire (client)
    	        "LEFT JOIN Compte c2 ON t.comptedestinataire = c2.id_compte " +
    	        "LEFT JOIN CompteClient cc2 ON c2.id_compte = cc2.id_compte " +
    	        "LEFT JOIN Client cl2 ON cc2.idClient = cl2.idClient " +
    	        "LEFT JOIN Utilisateur us2 ON cl2.idUtilisateur = us2.idUtilisateur " +
    	        
    	        // Joins pour le compte destinataire (agent)
    	        "LEFT JOIN CompteAgent ca2 ON c2.id_compte = ca2.id_compte " +
    	        "LEFT JOIN AgentKiosque ak2 ON ca2.idAgentKiosque = ak2.idAgentKiosque " +
    	        "LEFT JOIN Utilisateur us_agent2 ON ak2.idUtilisateur = us_agent2.idUtilisateur " +
    	        
    	        "WHERE t.statut IS NOT NULL " +
    	        "ORDER BY t.date DESC, t.heure DESC";

    	    try (PreparedStatement ps = connection.prepareStatement(query)) {
    	        ResultSet rs = ps.executeQuery();
    	        while (rs.next()) {
    	            Transaction transaction = new Transaction(
    	                rs.getInt("idTransaction"),
    	                rs.getFloat("montant"),
    	                rs.getDate("date"),
    	                rs.getTime("heure"),
    	                rs.getString("statut"),
    	                rs.getString("telephoneSource"),
    	                rs.getString("telephoneDestinataire"),
    	                rs.getString("typeTransaction")
    	            );
    	            transactions.add(transaction);
    	        }
    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	    }

    	    return transactions;
    	}
    
    
    public void effectuerTransfertAutomatique(int idTransaction, double montant, int compteSource, int compteDestinataire) throws SQLException {
        Connection connection = null;
        try {
            connection = ConnectDB.getInstance().getConnection();
            connection.setAutoCommit(false);

            // Débiter le compte source
            String sqlDebit = "UPDATE Compte SET solde = solde - ? WHERE id_compte = ?";
            try (PreparedStatement pstmtDebit = connection.prepareStatement(sqlDebit)) {
                pstmtDebit.setDouble(1, montant);
                pstmtDebit.setInt(2, compteSource);
                pstmtDebit.executeUpdate();
            }

            // Créditer le compte destinataire
            String sqlCredit = "UPDATE Compte SET solde = solde + ? WHERE id_compte = ?";
            try (PreparedStatement pstmtCredit = connection.prepareStatement(sqlCredit)) {
                pstmtCredit.setDouble(1, montant);
                pstmtCredit.setInt(2, compteDestinataire);
                pstmtCredit.executeUpdate();
            }

            // Mettre à jour le statut de la transaction
            String sqlUpdateTransaction = "UPDATE Transaction SET statut = 'effectué' WHERE idTransaction = ?";
            try (PreparedStatement pstmtUpdateTrans = connection.prepareStatement(sqlUpdateTransaction)) {
                pstmtUpdateTrans.setInt(1, idTransaction);
                pstmtUpdateTrans.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        }
    }
}




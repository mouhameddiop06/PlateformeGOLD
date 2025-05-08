package dao;

import metier.AgentKiosque;
import metier.Transaction;
import utilitaires.ConnectDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AgentKiosqueDAO {
    private static final Logger logger = Logger.getLogger(AgentKiosqueDAO.class.getName());
    private Connection connection;

    public AgentKiosqueDAO() throws SQLException {
        connection = ConnectDB.getInstance().getConnection();
    }

    // Méthode pour obtenir l'ID de l'agent kiosque à partir de l'ID utilisateur
    public int getIdAgentKiosqueByUserId(int idUtilisateur) throws SQLException {
        String query = "SELECT idAgentKiosque FROM AgentKiosque WHERE idUtilisateur = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idAgentKiosque");
                } else {
                    return -1; // Retourne -1 si aucun agent kiosque n'est trouvé
                }
            }
        }
    }

    // Méthode pour lister les agents non affectés à un kiosque
    public List<AgentKiosque> listerAgentsNonAffectes() throws SQLException {
        List<AgentKiosque> agentsDisponibles = new ArrayList<>();
        String query = "SELECT a.idAgentKiosque, u.email, a.numeroIdentificationAgent " +
                "FROM AgentKiosque a " +
                "JOIN Utilisateur u ON a.idUtilisateur = u.idUtilisateur " +
                "LEFT JOIN Kiosque k ON a.idAgentKiosque = k.idAgentKiosque " +
                "WHERE k.idAgentKiosque IS NULL";

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AgentKiosque agent = new AgentKiosque();
                agent.setIdAgentKiosque(rs.getInt("idAgentKiosque"));
                agent.setEmail(rs.getString("email"));
                agent.setNumeroIdentificationAgent(rs.getString("numeroIdentificationAgent"));
                agentsDisponibles.add(agent);
            }
        } catch (SQLException e) {
            logger.severe("Erreur lors de la récupération des agents non affectés : " + e.getMessage());
            throw e;
        }

        return agentsDisponibles;
    }

    // Méthode pour lister les agents disponibles
    public List<AgentKiosque> listerAgentsDisponibles() {
        List<AgentKiosque> agentsDisponibles = new ArrayList<>();
        String query = "SELECT ak.idAgentKiosque, ak.numeroIdentificationAgent, u.email, ak.idUtilisateur " +
                "FROM AgentKiosque ak " +
                "JOIN Utilisateur u ON ak.idUtilisateur = u.idUtilisateur " +
                "LEFT JOIN Kiosque k ON ak.idAgentKiosque = k.idAgentKiosque " +
                "WHERE k.idKiosque IS NULL";

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AgentKiosque agent = new AgentKiosque();
                agent.setIdAgentKiosque(rs.getInt("idAgentKiosque"));
                agent.setNumeroIdentificationAgent(rs.getString("numeroIdentificationAgent"));
                agent.setIdUtilisateur(rs.getInt("idUtilisateur"));
                agent.setEmail(rs.getString("email"));

                agentsDisponibles.add(agent);
            }
        } catch (SQLException e) {
            logger.severe("Erreur lors de la récupération des agents disponibles : " + e.getMessage());
        }

        return agentsDisponibles;
    }

    // Méthode pour faire un dépôt avec validation du solde de l'agent
    public boolean faireDepot(String clientPhoneNumber, double montant, int agentId) throws SQLException {
        String getClientAccountQuery = "SELECT c.id_compte FROM Client cl " +
                "JOIN Utilisateur u ON cl.idUtilisateur = u.idUtilisateur " +
                "JOIN CompteClient cc ON cl.idClient = cc.idClient " +
                "JOIN Compte c ON cc.id_compte = c.id_compte " +
                "WHERE u.telephone = ?";
        String checkAgentBalanceQuery = "SELECT c.solde FROM CompteAgent ca " +
                "JOIN Compte c ON ca.id_compte = c.id_compte " +
                "WHERE ca.idAgentKiosque = ?";
        String updateClientBalanceQuery = "UPDATE Compte SET solde = solde + ? WHERE id_compte = ?";
        String updateAgentBalanceQuery = "UPDATE Compte SET solde = solde - ? WHERE id_compte = (SELECT id_compte FROM CompteAgent WHERE idAgentKiosque = ?)";
        String insertTransactionQuery = "INSERT INTO Transaction (montant, date, heure, statut, id_optiontransaction, comptesource, comptedestinataire) " +
                "VALUES (?, CURRENT_DATE, CURRENT_TIME, 'effectué', " +
                "(SELECT idOption FROM OptionTransaction WHERE nomOption = 'depot'), ?, ?)";
        String insertTransactionAgentQuery = "INSERT INTO TransactionAgent (idTransaction, idAgentKiosque) " +
                "VALUES (currval('transaction_idtransaction_seq'), ?)";

        connection.setAutoCommit(false);  // Démarrer une transaction SQL
        try (PreparedStatement getClientAccountStmt = connection.prepareStatement(getClientAccountQuery);
             PreparedStatement checkAgentBalanceStmt = connection.prepareStatement(checkAgentBalanceQuery);
             PreparedStatement updateClientBalanceStmt = connection.prepareStatement(updateClientBalanceQuery);
             PreparedStatement updateAgentBalanceStmt = connection.prepareStatement(updateAgentBalanceQuery);
             PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery);
             PreparedStatement insertTransactionAgentStmt = connection.prepareStatement(insertTransactionAgentQuery)) {

            // Récupérer le compte client
            getClientAccountStmt.setString(1, clientPhoneNumber);
            ResultSet clientRs = getClientAccountStmt.executeQuery();

            // Vérifier le solde de l'agent
            checkAgentBalanceStmt.setInt(1, agentId);
            ResultSet agentRs = checkAgentBalanceStmt.executeQuery();

            if (clientRs.next() && agentRs.next()) {
                int clientAccountId = clientRs.getInt("id_compte");
                double agentSolde = agentRs.getDouble("solde");

                if (agentSolde >= montant) {  // Vérification du solde de l'agent
                    // Mettre à jour le solde du client et de l'agent
                    updateClientBalanceStmt.setDouble(1, montant);
                    updateClientBalanceStmt.setInt(2, clientAccountId);
                    updateClientBalanceStmt.executeUpdate();

                    updateAgentBalanceStmt.setDouble(1, montant);
                    updateAgentBalanceStmt.setInt(2, agentId);
                    updateAgentBalanceStmt.executeUpdate();

                    // Enregistrer la transaction
                    insertTransactionStmt.setDouble(1, montant);
                    insertTransactionStmt.setInt(2, clientAccountId);
                    insertTransactionStmt.setInt(3, clientAccountId);
                    insertTransactionStmt.executeUpdate();

                    // Lier la transaction à l'agent
                    insertTransactionAgentStmt.setInt(1, agentId);
                    insertTransactionAgentStmt.executeUpdate();

                    connection.commit();  // Confirmer les modifications si tout s'est bien passé
                    return true;
                } else {
                    logger.warning("Solde insuffisant pour l'agent Kiosque ID: " + agentId);
                    connection.rollback();  // Annuler la transaction si l'agent n'a pas assez de solde
                    return false;
                }
            } else {
                connection.rollback();  // Annuler la transaction si le client n'est pas trouvé
                logger.warning("Client non trouvé ou erreur lors de la récupération des informations.");
                return false;
            }
        } catch (SQLException e) {
            connection.rollback();  // Annuler la transaction en cas d'erreur
            logger.severe("Erreur lors du dépôt : " + e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true);  // Remettre en mode auto-commit
        }
    }

    // Méthode pour valider un retrait avec un code de retrait avec validation du solde du client
    public boolean validerRetrait(String withdrawalCode, int agentId) throws SQLException {
        String validateCodeQuery = "SELECT * FROM CodeRetrait WHERE code = ? AND estUtilise = FALSE " +
                "AND dateExpiration > CURRENT_TIMESTAMP";
        String updateCodeStatusQuery = "UPDATE CodeRetrait SET estUtilise = TRUE WHERE code = ?";
        String updateClientBalanceQuery = "UPDATE Compte SET solde = solde - ? WHERE id_compte = ?";
        String updateAgentBalanceQuery = "UPDATE Compte SET solde = solde + ? WHERE id_compte = (SELECT id_compte FROM CompteAgent WHERE idAgentKiosque = ?)";
        String insertTransactionQuery = "INSERT INTO Transaction (montant, date, heure, statut, id_optiontransaction, comptesource, comptedestinataire) " +
                "VALUES (?, CURRENT_DATE, CURRENT_TIME, 'effectué', " +
                "(SELECT idOption FROM OptionTransaction WHERE nomOption = 'retrait'), ?, ?)";
        String insertTransactionAgentQuery = "INSERT INTO TransactionAgent (idTransaction, idAgentKiosque) " +
                "VALUES (currval('transaction_idtransaction_seq'), ?)";

        connection.setAutoCommit(false);  // Démarrer une transaction SQL
        try (PreparedStatement validateCodeStmt = connection.prepareStatement(validateCodeQuery)) {
            validateCodeStmt.setString(1, withdrawalCode);
            ResultSet rs = validateCodeStmt.executeQuery();

            if (rs.next()) {
                int clientAccountId = rs.getInt("idCompte");
                double montant = rs.getDouble("montant");

                // Vérification du solde du client avant retrait
                String checkClientBalanceQuery = "SELECT solde FROM Compte WHERE id_compte = ?";
                try (PreparedStatement checkClientBalanceStmt = connection.prepareStatement(checkClientBalanceQuery)) {
                    checkClientBalanceStmt.setInt(1, clientAccountId);
                    ResultSet balanceRs = checkClientBalanceStmt.executeQuery();

                    if (balanceRs.next()) {
                        double clientSolde = balanceRs.getDouble("solde");

                        if (clientSolde >= montant) {
                            // Mettre à jour les soldes et les statuts
                            try (PreparedStatement updateCodeStatusStmt = connection.prepareStatement(updateCodeStatusQuery);
                                 PreparedStatement updateClientBalanceStmt = connection.prepareStatement(updateClientBalanceQuery);
                                 PreparedStatement updateAgentBalanceStmt = connection.prepareStatement(updateAgentBalanceQuery);
                                 PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery);
                                 PreparedStatement insertTransactionAgentStmt = connection.prepareStatement(insertTransactionAgentQuery)) {

                                // Marquer le code comme utilisé
                                updateCodeStatusStmt.setString(1, withdrawalCode);
                                updateCodeStatusStmt.executeUpdate();

                                // Mise à jour du solde du client et de l'agent
                                updateClientBalanceStmt.setDouble(1, montant);
                                updateClientBalanceStmt.setInt(2, clientAccountId);
                                updateClientBalanceStmt.executeUpdate();

                                updateAgentBalanceStmt.setDouble(1, montant);
                                updateAgentBalanceStmt.setInt(2, agentId);
                                updateAgentBalanceStmt.executeUpdate();

                                // Insérer la transaction
                                insertTransactionStmt.setDouble(1, montant);
                                insertTransactionStmt.setInt(2, clientAccountId);
                                insertTransactionStmt.setInt(3, clientAccountId);
                                insertTransactionStmt.executeUpdate();

                                // Associer la transaction à l'agent
                                insertTransactionAgentStmt.setInt(1, agentId);
                                insertTransactionAgentStmt.executeUpdate();

                                connection.commit();  // Confirmer la transaction
                                return true;
                            }
                        } else {
                            logger.warning("Solde client insuffisant pour un retrait.");
                            connection.rollback();  // Annuler la transaction
                            return false;
                        }
                    } else {
                        logger.warning("Le solde du client est introuvable.");
                        connection.rollback();  // Annuler la transaction
                        return false;
                    }
                }
            } else {
                connection.rollback();  // Annuler la transaction si le code est invalide
                logger.warning("Code de retrait invalide ou expiré.");
                return false;
            }
        } catch (SQLException e) {
            connection.rollback();  // Annuler la transaction en cas d'erreur
            logger.severe("Erreur lors du retrait : " + e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true);  // Remettre en mode auto-commit
        }
    }

    // Méthode pour vérifier si le solde d'un client est suffisant pour un retrait
    public boolean soldeSuffisantPourRetrait(int clientId, double montant) throws SQLException {
        String checkClientBalanceQuery = "SELECT solde FROM Compte WHERE id_compte = ?";

        try (PreparedStatement ps = connection.prepareStatement(checkClientBalanceQuery)) {
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double clientSolde = rs.getDouble("solde");
                return clientSolde >= montant;  // Retourne true si le solde est suffisant, sinon false
            } else {
                logger.warning("Compte client introuvable pour ID: " + clientId);
                return false;
            }
        } catch (SQLException e) {
            logger.severe("Erreur lors de la vérification du solde pour retrait : " + e.getMessage());
            throw e;
        }
    }

    // Méthode pour vérifier si le solde de l'agent est suffisant pour un dépôt
    public boolean soldeSuffisantPourDepot(int agentId, double montant) throws SQLException {
        String checkAgentBalanceQuery = "SELECT solde FROM CompteAgent ca JOIN Compte c ON ca.id_compte = c.id_compte WHERE ca.idAgentKiosque = ?";
        try (PreparedStatement ps = connection.prepareStatement(checkAgentBalanceQuery)) {
            ps.setInt(1, agentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double soldeAgent = rs.getDouble("solde");
                return soldeAgent >= montant;  // Vérifie si le solde de l'agent est suffisant
            } else {
                logger.warning("Compte agent introuvable pour l'agent ID: " + agentId);
                return false;
            }
        } catch (SQLException e) {
            logger.severe("Erreur lors de la vérification du solde de l'agent : " + e.getMessage());
            throw e;
        }
    }

    public List<Transaction> getLast10Transactions(int agentId) throws SQLException {
        String query = "SELECT " +
            "t.idTransaction, " +
            "t.montant, " +
            "t.date, " +
            "t.heure, " +
            "t.statut, " +
            "t.comptesource, " +
            "t.comptedestinataire, " +
            "opt.nomOption as type_operation, " +
            "u_dest.telephone AS telephone_destinataire, " +
            "u_source.telephone as telephone_source " +
        "FROM Transaction t " +
            "JOIN TransactionAgent ta ON t.idTransaction = ta.idTransaction " +
            "JOIN OptionTransaction opt ON t.id_optiontransaction = opt.idOption " +
            "JOIN CompteAgent ca ON ta.idAgentKiosque = ca.idAgentKiosque " +
            // Pour le destinataire
            "LEFT JOIN Compte c_dest ON t.comptedestinataire = c_dest.id_compte " +
            "LEFT JOIN CompteClient cc_dest ON c_dest.id_compte = cc_dest.id_compte " +
            "LEFT JOIN Client cl_dest ON cc_dest.idClient = cl_dest.idClient " +
            "LEFT JOIN Utilisateur u_dest ON cl_dest.idUtilisateur = u_dest.idUtilisateur " +
            // Pour la source
            "LEFT JOIN Compte c_source ON t.comptesource = c_source.id_compte " +
            "LEFT JOIN CompteClient cc_source ON c_source.id_compte = cc_source.id_compte " +
            "LEFT JOIN Client cl_source ON cc_source.idClient = cl_source.idClient " +
            "LEFT JOIN Utilisateur u_source ON cl_source.idUtilisateur = u_source.idUtilisateur " +
        "WHERE ta.idAgentKiosque = ? " +
        "ORDER BY t.date DESC, t.heure DESC ";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, agentId);
            ResultSet rs = stmt.executeQuery();
            List<Transaction> transactions = new ArrayList<>();

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("idTransaction"));
                transaction.setMontant(rs.getFloat("montant"));
                transaction.setDate(rs.getDate("date"));
                transaction.setHeure(rs.getTime("heure"));
                transaction.setStatut(rs.getString("statut"));
                transaction.setTelephoneSource(rs.getString("telephone_source"));
                transaction.setTelephoneDestinataire(rs.getString("telephone_destinataire"));
                transaction.setTypeTransaction(rs.getString("type_operation"));
                transactions.add(transaction);
            }

            return transactions;
        } catch (SQLException e) {
            logger.severe("Erreur lors de la récupération des transactions : " + e.getMessage());
            throw e;
        }
    }

    // Méthode pour récupérer le solde du compte agent
    public double getSoldeAgent(int agentId) throws SQLException {
        String query = "SELECT c.solde FROM CompteAgent ca " +
                "JOIN Compte c ON ca.id_compte = c.id_compte " +
                "WHERE ca.idAgentKiosque = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, agentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("solde");
            } else {
                return 0;
            }
        }
    }

    // Méthode pour récupérer le gain mensuel d'un agent kiosque
    public double getAgentEarnings(int agentId) throws SQLException {
        String query = "SELECT gainMensuel FROM AgentKiosque WHERE idAgentKiosque = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, agentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("gainMensuel");
            } else {
                return 0;
            }
        }
    }
} // FIN DAO
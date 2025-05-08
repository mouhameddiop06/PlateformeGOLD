package dao;
import utilitaires.ConnectDB;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import metier.Transactions;

public class OperationDAO {
    private Connection connection;

    public OperationDAO(Connection connection) {
        this.connection = connection;
    }

    // Transfert immédiat avec commission
    public void effectuerTransfert(int idCompteSource, String numeroDestinataire, double montant) throws SQLException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Obtenir la connexion à la base de données
            connection = ConnectDB.getInstance().getConnection();
            connection.setAutoCommit(false);  // Démarrer une transaction

            // 1. Vérifier si le numéro de téléphone existe dans la table Contact
            String sqlVerifContact = "SELECT c.idClient, cc.id_compte AS idCompteDestinataire " +
                                     "FROM Contact c " +
                                     "JOIN CompteClient cc ON c.idClient = cc.idClient " +
                                     "WHERE c.numeroTelephone = ?";
            pstmt = connection.prepareStatement(sqlVerifContact);
            pstmt.setString(1, numeroDestinataire);
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Ce numéro n'a pas de compte associé.");
            }

            int idClientDestinataire = rs.getInt("idClient");
            int idCompteDestinataire = rs.getInt("idCompteDestinataire");

            // 2. Vérifier le solde du compte source
            String sqlVerifSolde = "SELECT solde FROM Compte WHERE id_compte = ?";
            pstmt = connection.prepareStatement(sqlVerifSolde);
            pstmt.setInt(1, idCompteSource);
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Compte source non trouvé.");
            }

            double soldeSource = rs.getDouble("solde");
            double commission = montant * 0.01;  // Commission de 1%
            double montantTotal = montant + commission;

            if (soldeSource < montantTotal) {
                throw new SQLException("Solde insuffisant pour effectuer ce transfert.");
            }

            // 3. Débiter le compte source
            String sqlDebit = "UPDATE Compte SET solde = solde - ? WHERE id_compte = ?";
            pstmt = connection.prepareStatement(sqlDebit);
            pstmt.setDouble(1, montantTotal);
            pstmt.setInt(2, idCompteSource);
            pstmt.executeUpdate();
            System.out.println("Débit du compte source réussi : " + montantTotal);

            // 4. Créditer le compte destinataire
            String sqlCredit = "UPDATE Compte SET solde = solde + ? WHERE id_compte = ?";
            pstmt = connection.prepareStatement(sqlCredit);
            pstmt.setDouble(1, montant);
            pstmt.setInt(2, idCompteDestinataire);
            pstmt.executeUpdate();
            System.out.println("Crédit du compte destinataire réussi : " + montant);

            // 5. Ajouter la commission au compte principal de bénéfice
            String sqlCommission = "UPDATE ComptePrincipalBenefice SET montantBenefice = montantBenefice + ? WHERE idBenefice = 1";
            pstmt = connection.prepareStatement(sqlCommission);
            pstmt.setDouble(1, commission);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Commission de " + commission + " ajoutée au ComptePrincipalBenefice");
            } else {
                System.out.println("Échec de l'ajout de la commission au ComptePrincipalBenefice");
                throw new SQLException("Erreur lors de l'ajout de la commission.");
            }

            // 6. Enregistrer la transaction
            String sqlTransaction = "INSERT INTO Transaction (montant, date, statut, heure, id_optiontransaction, comptesource, comptedestinataire) " +
                                    "VALUES (?, CURRENT_DATE, 'effectué', CURRENT_TIME, " +
                                    "(SELECT idOption FROM OptionTransaction WHERE nomOption = 'transfert_immediat'), ?, ?)";
            pstmt = connection.prepareStatement(sqlTransaction);
            pstmt.setDouble(1, montant);
            pstmt.setInt(2, idCompteSource);
            pstmt.setInt(3, idCompteDestinataire);
            pstmt.executeUpdate();
            System.out.println("Transaction enregistrée avec succès.");

            // Si tout s'est bien passé, on valide la transaction
            connection.commit();
            System.out.println("Transaction validée.");

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    System.out.println("Rollback effectué suite à une erreur : " + e.getMessage());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (connection != null) try { connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }


    // Programmation du transfert automatique avec commission
    public void programmerTransfertAutomatique(int idCompteSource, String numeroDestinataire, double montant, String frequence, LocalDate dateDebut) {
        try {
            // Calculer la commission de 1%
            double commission = montant * 0.01;
            double montantTotalDebite = montant + commission;

            // Vérifier si le numéro existe dans la table Contact
            String sqlVerifContact = "SELECT c.idClient FROM Contact c WHERE c.numeroTelephone = ?";
            PreparedStatement pstmtVerifContact = connection.prepareStatement(sqlVerifContact);
            pstmtVerifContact.setString(1, numeroDestinataire);
            ResultSet rsContact = pstmtVerifContact.executeQuery();

            if (rsContact.next()) {
                int idClientDestinataire = rsContact.getInt("idClient");

                // Créer la transaction
                connection.setAutoCommit(false);

                String sqlTransaction = "INSERT INTO Transaction (montant, date, statut, heure, id_optiontransaction, comptesource, comptedestinataire) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmtTransaction = connection.prepareStatement(sqlTransaction, Statement.RETURN_GENERATED_KEYS);
                pstmtTransaction.setDouble(1, montant);
                pstmtTransaction.setDate(2, java.sql.Date.valueOf(dateDebut));
                pstmtTransaction.setString(3, "programmé");
                pstmtTransaction.setTime(4, java.sql.Time.valueOf(LocalTime.now()));
                pstmtTransaction.setInt(5, getIdOptionTransaction("transfert_differe_avec_temps")); // Utiliser une option existante pour différé
                pstmtTransaction.setInt(6, idCompteSource);
                pstmtTransaction.setInt(7, getCompteIdByClientId(idClientDestinataire));
                pstmtTransaction.executeUpdate();

                ResultSet rsTransactionId = pstmtTransaction.getGeneratedKeys();
                if (rsTransactionId.next()) {
                    int idTransaction = rsTransactionId.getInt(1);

                    // Créer le transfert automatique
                    String sqlTransfertAuto = "INSERT INTO TransfertAutomatique (idTransaction, timer, jour, etat, frequence) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement pstmtTransfertAuto = connection.prepareStatement(sqlTransfertAuto);
                    pstmtTransfertAuto.setInt(1, idTransaction);
                    pstmtTransfertAuto.setInt(2, 0); // Timer à 0 pour un transfert récurrent
                    pstmtTransfertAuto.setDate(3, java.sql.Date.valueOf(dateDebut));
                    pstmtTransfertAuto.setString(4, "actif");
                    pstmtTransfertAuto.setString(5, frequence);
                    pstmtTransfertAuto.executeUpdate();

                    // Ajouter la commission au compte des bénéfices
                    String sqlBenefice = "UPDATE ComptePrincipalBenefice SET montantBenefice = montantBenefice + ?";
                    PreparedStatement pstmtBenefice = connection.prepareStatement(sqlBenefice);
                    pstmtBenefice.setDouble(1, commission);
                    pstmtBenefice.executeUpdate();

                    connection.commit();
                    System.out.println("Transfert automatique programmé avec succès");
                }
            } else {
                System.out.println("Numéro de téléphone non trouvé");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la programmation du transfert automatique : " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Erreur lors du rollback : " + ex.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Erreur lors de la réinitialisation de l'autocommit : " + e.getMessage());
            }
        }
    }


    public void programmerTransfertDiffere(int idCompteSource, String numeroDestinataire, double montant, LocalDate dateExecution) {
        try {
            // Vérifier si le numéro existe dans la table Contact
            String sqlVerifContact = "SELECT c.idClient FROM Contact c WHERE c.numeroTelephone = ?";
            PreparedStatement pstmtVerifContact = connection.prepareStatement(sqlVerifContact);
            pstmtVerifContact.setString(1, numeroDestinataire);
            ResultSet rsContact = pstmtVerifContact.executeQuery();

            if (rsContact.next()) {
                int idClientDestinataire = rsContact.getInt("idClient");

                // Vérifier si le solde est suffisant au moment de la programmation
                String sqlVerifSolde = "SELECT solde FROM Compte WHERE id_compte = ?";
                PreparedStatement pstmtVerifSolde = connection.prepareStatement(sqlVerifSolde);
                pstmtVerifSolde.setInt(1, idCompteSource);
                ResultSet rsSolde = pstmtVerifSolde.executeQuery();

                if (rsSolde.next()) {
                    double soldeSource = rsSolde.getDouble("solde");

                    if (soldeSource >= montant) {
                        // Début de la transaction
                        connection.setAutoCommit(false);

                        // Insérer la transaction programmée dans la table Transaction
                        String sqlTransaction = "INSERT INTO Transaction (montant, date, statut, heure, id_optiontransaction, comptesource, comptedestinataire) " +
                                                "VALUES (?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement pstmtTransaction = connection.prepareStatement(sqlTransaction, Statement.RETURN_GENERATED_KEYS);
                        pstmtTransaction.setDouble(1, montant);
                        pstmtTransaction.setDate(2, java.sql.Date.valueOf(dateExecution));
                        pstmtTransaction.setString(3, "programmé");
                        pstmtTransaction.setTime(4, java.sql.Time.valueOf(LocalTime.now()));
                        pstmtTransaction.setInt(5, getIdOptionTransaction("transfert_differe_avec_temps"));
                        pstmtTransaction.setInt(6, idCompteSource);
                        pstmtTransaction.setInt(7, getCompteIdByClientId(idClientDestinataire));
                        pstmtTransaction.executeUpdate();

                        // Récupérer l'ID de la transaction nouvellement créée
                        ResultSet rsTransactionId = pstmtTransaction.getGeneratedKeys();
                        if (rsTransactionId.next()) {
                            int idTransaction = rsTransactionId.getInt(1);

                            // Insérer dans la table spécifique à la gestion des transferts différés
                            String sqlTransfertDiffere = "INSERT INTO TransfertDiffere (idTransaction, dateExecution) VALUES (?, ?)";
                            PreparedStatement pstmtTransfertDiffere = connection.prepareStatement(sqlTransfertDiffere);
                            pstmtTransfertDiffere.setInt(1, idTransaction);
                            pstmtTransfertDiffere.setDate(2, java.sql.Date.valueOf(dateExecution));
                            pstmtTransfertDiffere.executeUpdate();

                            // Valider la transaction
                            connection.commit();
                            System.out.println("Transfert différé programmé avec succès pour la date " + dateExecution);
                        }
                    } else {
                        System.out.println("Solde insuffisant pour programmer le transfert différé");
                    }
                }
            } else {
                System.out.println("Numéro de téléphone non trouvé dans les contacts");
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Erreur lors du rollback : " + ex.getMessage());
            }
            System.out.println("Erreur lors de la programmation du transfert différé : " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Erreur lors de la réinitialisation de l'autocommit : " + e.getMessage());
            }
        }
    }


    // Méthode pour gérer d'autres fonctionnalités comme le retrait, dépôt, épargne, etc.
    // ...
    public void creerEpargne(int idCompteClient, double montantInitial, double objectifEpargne, String frequenceVersement, double montantVersement, LocalDate dateEcheance) {
        try {
            connection.setAutoCommit(false);

            // Vérifier le solde du compte
            String sqlVerifSolde = "SELECT solde FROM Compte c JOIN CompteClient cc ON c.id_compte = cc.id_compte WHERE cc.idCompteClient = ?";
            PreparedStatement pstmtVerifSolde = connection.prepareStatement(sqlVerifSolde);
            pstmtVerifSolde.setInt(1, idCompteClient);
            ResultSet rsSolde = pstmtVerifSolde.executeQuery();

            if (rsSolde.next()) {
                double solde = rsSolde.getDouble("solde");

                if (solde >= montantInitial) {
                    // Débiter le compte
                    String sqlDebit = "UPDATE Compte c JOIN CompteClient cc ON c.id_compte = cc.id_compte SET c.solde = c.solde - ? WHERE cc.idCompteClient = ?";
                    PreparedStatement pstmtDebit = connection.prepareStatement(sqlDebit);
                    pstmtDebit.setDouble(1, montantInitial);
                    pstmtDebit.setInt(2, idCompteClient);
                    pstmtDebit.executeUpdate();

                    // Créer l'épargne
                    String sqlEpargne = "INSERT INTO Epargne (datecreation, dateecheance, montant, objectifEpargne, frequenceVersement, montantVersement, idCompteClient) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmtEpargne = connection.prepareStatement(sqlEpargne);
                    pstmtEpargne.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                    pstmtEpargne.setDate(2, java.sql.Date.valueOf(dateEcheance));
                    pstmtEpargne.setDouble(3, montantInitial);
                    pstmtEpargne.setDouble(4, objectifEpargne);
                    pstmtEpargne.setString(5, frequenceVersement);
                    pstmtEpargne.setDouble(6, montantVersement);
                    pstmtEpargne.setInt(7, idCompteClient);
                    pstmtEpargne.executeUpdate();

                    connection.commit();
                    System.out.println("Épargne créée avec succès");
                } else {
                    System.out.println("Solde insuffisant pour créer l'épargne");
                }
            } else {
                System.out.println("Compte client non trouvé");
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Erreur lors du rollback : " + ex.getMessage());
            }
            System.out.println("Erreur lors de la création de l'épargne : " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Erreur lors de la réinitialisation de l'autocommit : " + e.getMessage());
            }
        }
    }

 // Méthode pour mettre à jour le solde d'un compte
    private void updateSolde(int idCompte, double montant) throws SQLException {
        String sql = "UPDATE Compte SET solde = solde + ? WHERE id_compte = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setDouble(1, montant);
        pstmt.setInt(2, idCompte);
        pstmt.executeUpdate();
    }
 // Méthode pour récupérer le type d'opération en fonction de l'ID de l'option
    private String getTypeOperation(int idOption) throws SQLException {
        String sql = "SELECT nomOption FROM OptionTransaction WHERE idOption = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, idOption);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getString("nomOption");
        }
        throw new SQLException("Type d'opération non trouvé pour l'ID : " + idOption);
    }
    
    
   
    
    public List<Transactions> getHistoriqueTransactions(int idCompte) {
        List<Transactions> transactions = new ArrayList<>();
        try {
            String sql = "SELECT t.*, ot.nomOption, " +
                         "u_source.telephone AS telephone_source, " +
                         "u_dest.telephone AS telephone_destinataire " +
                         "FROM Transaction t " +
                         "JOIN OptionTransaction ot ON t.id_optiontransaction = ot.idOption " +
                         "JOIN Compte c_source ON t.comptesource = c_source.id_compte " +
                         "JOIN CompteClient cc_source ON c_source.id_compte = cc_source.id_compte " +
                         "JOIN Client cl_source ON cc_source.idClient = cl_source.idClient " +
                         "JOIN Utilisateur u_source ON cl_source.idUtilisateur = u_source.idUtilisateur " +
                         "LEFT JOIN Compte c_dest ON t.comptedestinataire = c_dest.id_compte " +
                         "LEFT JOIN CompteClient cc_dest ON c_dest.id_compte = cc_dest.id_compte " +
                         "LEFT JOIN Client cl_dest ON cc_dest.idClient = cl_dest.idClient " +
                         "LEFT JOIN Utilisateur u_dest ON cl_dest.idUtilisateur = u_dest.idUtilisateur " +
                         "WHERE t.comptesource = ? OR t.comptedestinataire = ? " +
                         "ORDER BY t.date DESC, t.heure DESC";

            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, idCompte);
            pstmt.setInt(2, idCompte);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Transactions transaction = new Transactions(
                    rs.getInt("idTransaction"),
                    rs.getDouble("montant"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("statut"),
                    rs.getTime("heure").toLocalTime(),
                    rs.getString("nomOption"),
                    rs.getString("telephone_source"),
                    rs.getString("telephone_destinataire")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'historique des transactions : " + e.getMessage());
        }
        return transactions;
    }
    
    
    // Utilitaires pour récupérer des informations liées aux transactions et comptes
    private int getIdOptionTransaction(String nomOption) throws SQLException {
        String sql = "SELECT idOption FROM OptionTransaction WHERE nomOption = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, nomOption);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("idOption");
        }
        throw new SQLException("Option de transaction non trouvée : " + nomOption);
    }

 // Requête SQL pour récupérer les transactions annulables (moins de 3 jours)
    public List<Transactions> getTransactionsAnnulables(int idCompte) throws SQLException {
        List<Transactions> transactionsAnnulables = new ArrayList<>();
        try {
            String sql = "SELECT t.*, ot.nomOption, " +
                        "u_dest.telephone AS telephone_destinataire " +
                        "FROM Transaction t " +
                        "JOIN OptionTransaction ot ON t.id_optiontransaction = ot.idOption " +
                        "LEFT JOIN Compte c_dest ON t.comptedestinataire = c_dest.id_compte " +
                        "LEFT JOIN CompteClient cc_dest ON c_dest.id_compte = cc_dest.id_compte " +
                        "LEFT JOIN Client cl_dest ON cc_dest.idClient = cl_dest.idClient " +
                        "LEFT JOIN Utilisateur u_dest ON cl_dest.idUtilisateur = u_dest.idUtilisateur " +
                        "WHERE (t.comptesource = ? OR t.comptedestinataire = ?) " +
                        "AND t.date >= ? " +
                        "AND ot.nomOption NOT IN ('depot', 'retrait')" +
                        "AND t.statut != 'annulé' " +
                        "ORDER BY t.date DESC, t.heure DESC";

            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, idCompte);
            pstmt.setInt(2, idCompte);
            pstmt.setDate(3, java.sql.Date.valueOf(LocalDate.now().minusDays(3)));
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Transactions transaction = new Transactions(
                    rs.getInt("idTransaction"),
                    rs.getDouble("montant"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("statut"),
                    rs.getTime("heure").toLocalTime(),
                    rs.getString("nomOption"),
                    "",  // telephoneSource n'est plus utilisé
                    rs.getString("telephone_destinataire")  // Récupération du téléphone destinataire
                );
                
                transaction.setCompteSource(rs.getInt("comptesource"));
                transaction.setCompteDestinataire(rs.getInt("comptedestinataire"));
                
                transactionsAnnulables.add(transaction);
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la récupération des transactions annulables: " + e.getMessage());
        }
        return transactionsAnnulables;
    }
    
    
    
    public boolean annulerTransaction(int idTransaction) throws SQLException {
        boolean transactionAnnulee = false;

        try {
            connection.setAutoCommit(false);

            // Vérifier si la transaction date de moins de 3 jours
            String sqlVerifDate = "SELECT * FROM Transaction WHERE idTransaction = ? AND date >= ?";
            PreparedStatement pstmtVerifDate = connection.prepareStatement(sqlVerifDate);
            pstmtVerifDate.setInt(1, idTransaction);
            pstmtVerifDate.setDate(2, java.sql.Date.valueOf(LocalDate.now().minusDays(3)));
            ResultSet rsTransaction = pstmtVerifDate.executeQuery();

            if (rsTransaction.next()) {
                double montant = rsTransaction.getDouble("montant");
                int compteSource = rsTransaction.getInt("comptesource");
                int compteDestinataire = rsTransaction.getInt("comptedestinataire");
                String typeOperation = getTypeOperation(rsTransaction.getInt("id_optiontransaction"));

                // Annuler la transaction en fonction du type d'opération
                switch (typeOperation) {
                    case "transfert_immediat":
                    case "transfert_differe_avec_temps":
                        // Rembourser le compte source
                        updateSolde(compteSource, montant);
                        // Débiter le compte destinataire
                        updateSolde(compteDestinataire, -montant);
                        break;
                    case "depot":
                        // Débiter le compte destinataire
                        updateSolde(compteDestinataire, -montant);
                        break;
                    case "retrait":
                        // Rembourser le compte source
                        updateSolde(compteSource, montant);
                        break;
                    default:
                        throw new SQLException("Type d'opération non reconnu");
                }

                // Marquer la transaction comme annulée
                String sqlAnnulation = "UPDATE Transaction SET statut = 'annulé' WHERE idTransaction = ?";
                PreparedStatement pstmtAnnulation = connection.prepareStatement(sqlAnnulation);
                pstmtAnnulation.setInt(1, idTransaction);
                int rowsAffected = pstmtAnnulation.executeUpdate();

                // Si au moins une ligne a été mise à jour, l'annulation a réussi
                if (rowsAffected > 0) {
                    transactionAnnulee = true;
                }

                connection.commit();
                System.out.println("Transaction annulée avec succès");
            } else {
                System.out.println("La transaction ne peut pas être annulée (date supérieure à 3 jours ou transaction non trouvée)");
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.out.println("Erreur lors du rollback : " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    System.out.println("Erreur lors de la réinitialisation de l'autocommit : " + e.getMessage());
                }
            }
        }

        return transactionAnnulee;
    }

    // Méthode auxiliaire pour mettre à jour le solde d'un compte
    private void updateSoldeCompte(String telephone, double montant) throws SQLException {
        String sql = "UPDATE comptes SET solde = solde + ? WHERE telephone = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, montant);
            pstmt.setString(2, telephone);
            pstmt.executeUpdate();
        }
    }
    
    
    private int getCompteIdByClientId(int idClient) throws SQLException {
        String sql = "SELECT id_compte FROM CompteClient WHERE idClient = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, idClient);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("id_compte");
        }
        throw new SQLException("Compte non trouvé pour le client : " + idClient);
    }
    
}

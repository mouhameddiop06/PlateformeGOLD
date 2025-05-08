package dao;

import metier.AgentKiosque;

import metier.Personnel;
import utilitaires.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddUtilisateurDAO {
    private Connection connection;

    public AddUtilisateurDAO() {
        try {
            this.connection = ConnectDB.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour générer automatiquement le numeroIdentificationAgent
    public String genererNumeroIdentificationAgent() {
        String query = "SELECT MAX(CAST(SUBSTRING(numeroIdentificationAgent, 3) AS INTEGER)) FROM AgentKiosque";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int maxNum = rs.getInt(1);
                return String.format("AG%04d", maxNum + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "AG0001"; // Retourne un identifiant par défaut si une erreur se produit
    }

    // Méthode pour ajouter un agent de kiosque
    public void ajouterAgentKiosque(AgentKiosque agentKiosque) {
        String query = "INSERT INTO AgentKiosque (idUtilisateur, numeroIdentificationAgent) VALUES (?, ?)";
        String createCompteQuery = "INSERT INTO Compte (solde, datecreation) VALUES (0, CURRENT_DATE)";
        String createCompteAgentQuery = "INSERT INTO CompteAgent (idCompteAgent, id_compte, idAgentKiosque) VALUES (?, ?, ?)";
        
        try {
            connection.setAutoCommit(false); // Début de la transaction
            
            // Insertion de l'Agent Kiosque
            try (PreparedStatement ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, agentKiosque.getIdUtilisateur());
                if (agentKiosque.getNumeroIdentificationAgent() == null || agentKiosque.getNumeroIdentificationAgent().isEmpty()) {
                    agentKiosque.setNumeroIdentificationAgent(genererNumeroIdentificationAgent());
                }
                ps.setString(2, agentKiosque.getNumeroIdentificationAgent());
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("L'insertion de l'agent de kiosque a échoué, aucune ligne affectée.");
                }
                
                // Récupérer l'ID de l'agent kiosque
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idAgentKiosque = generatedKeys.getInt(1);
                        agentKiosque.setIdAgentKiosque(idAgentKiosque);
                        
                        // Étape 2 : Création du compte associé
                        try (PreparedStatement createCompteStmt = connection.prepareStatement(createCompteQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                            createCompteStmt.executeUpdate();
                            
                            try (ResultSet compteKeys = createCompteStmt.getGeneratedKeys()) {
                                if (!compteKeys.next()) throw new SQLException("Impossible d'obtenir l'ID du compte");
                                int idCompte = compteKeys.getInt(1);
                                
                                // Étape 3 : Lier le compte à l'agent kiosque dans la table CompteAgent
                                try (PreparedStatement createCompteAgentStmt = connection.prepareStatement(createCompteAgentQuery)) {
                                    createCompteAgentStmt.setInt(1, idCompte);
                                    createCompteAgentStmt.setInt(2, idCompte); // Utiliser le même ID pour idCompteAgent et idCompte
                                    createCompteAgentStmt.setInt(3, idAgentKiosque);
                                    createCompteAgentStmt.executeUpdate();
                                    
                                    // Validation de la transaction
                                    connection.commit();
                                    System.out.println("Agent kiosque ajouté avec son compte.");
                                }
                            }
                        }
                    } else {
                        throw new SQLException("Impossible d'obtenir l'ID de l'agent kiosque après l'insertion.");
                    }
                }
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException("Erreur lors de l'ajout de l'agent kiosque et du compte.", e);
            } finally {
                connection.setAutoCommit(true); // Réinitialisation de l'auto-commit
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'ajout de l'agent de kiosque et de son compte.", e);
        }
    }


    // Méthode pour générer automatiquement le numeroIdentificationPersonnel
    public String genererNumeroIdentificationPersonnel() {
        String query = "SELECT MAX(CAST(SUBSTRING(numeroIdentificationPers, 5) AS INTEGER)) FROM Personnel";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int maxNum = rs.getInt(1);
                return String.format("PERS%04d", maxNum + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "PERS0001"; // Retourne un identifiant par défaut si une erreur se produit
    }

    // Méthode pour ajouter un personnel
    public void ajouterPersonnel(Personnel personnel) {
        String query = "INSERT INTO Personnel (idUtilisateur, numeroIdentificationPers) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, personnel.getIdUtilisateur());
            if (personnel.getNumeroIdentificationPers() == null || personnel.getNumeroIdentificationPers().isEmpty()) {
                personnel.setNumeroIdentificationPers(genererNumeroIdentificationPersonnel());
            }
            ps.setString(2, personnel.getNumeroIdentificationPers());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("L'insertion du personnel a échoué, aucune ligne affectée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'ajout du personnel", e);
        }
    }
}
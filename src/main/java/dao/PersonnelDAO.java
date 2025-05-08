package dao;

import metier.Personnel;


import utilitaires.ConnectDB;
import metier.PersonnelDAOUtil;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.json.simple.JSONObject;



public class PersonnelDAO {
    private Connection connection;

    public PersonnelDAO() throws SQLException {
        connection = ConnectDB.getInstance().getConnection();
    }

    public void ajouterPersonnel(Personnel personnel) {
        String query = "INSERT INTO Personnel (utilisateurId) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, personnel.getIdUtilisateur());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getIdPersonnelByUserId(int userId) throws SQLException {
        String query = "SELECT idPersonnel FROM Personnel WHERE idUtilisateur = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idPersonnel");
                } else {
                    throw new SQLException("Aucun personnel trouvé pour l'idUtilisateur : " + userId);
                }
            }
        }
    }



 // Méthode pour obtenir la liste des agents gérés par un personnel
    public List<Map<String, Object>> getAgentsKiosque() throws SQLException {
        List<Map<String, Object>> agents = new ArrayList<>();
        
        // Requête mise à jour pour inclure gainMensuel et tauxCommission
        String query = "SELECT ak.idAgentKiosque, ak.numeroIdentificationAgent, ak.gainMensuel, ak.tauxCommission, " +
                       "k.numeroIdentificationKiosque, k.etatKiosque " +
                       "FROM AgentKiosque ak " +
                       "LEFT JOIN Kiosque k ON ak.idAgentKiosque = k.idAgentKiosque";
        
        System.out.println("Exécution de la requête pour récupérer tous les agents kiosque avec les gains et les commissions.");
        
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> agent = new HashMap<>();
                agent.put("idAgentKiosque", rs.getInt("idAgentKiosque"));
                agent.put("numeroIdentificationAgent", rs.getString("numeroIdentificationAgent"));
                agent.put("gainMensuel", rs.getDouble("gainMensuel")); // Récupérer le gain mensuel
                agent.put("tauxCommission", rs.getDouble("tauxCommission")); // Récupérer le taux de commission

                String numeroIdentificationKiosque = rs.getString("numeroIdentificationKiosque");
                String etatKiosque = rs.getString("etatKiosque");

                agent.put("numeroIdentificationKiosque", numeroIdentificationKiosque != null ? numeroIdentificationKiosque : "Non attribué");
                agent.put("etatKiosque", etatKiosque != null ? etatKiosque : "Non attribué");

                agents.add(agent);
                
                System.out.println("Agent récupéré: " + agent);
            }
        }
        
        System.out.println("Nombre total d'agents récupérés: " + agents.size());
        return agents;
    }

    // Méthode pour obtenir la liste des inscriptions en attente
    public List<JSONObject> getInscriptionsEnAttente() throws SQLException {
        List<JSONObject> inscriptions = new ArrayList<>();
        String query = "SELECT * FROM InscriptionEnAttente WHERE statut = 'en_attente'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                JSONObject inscription = new JSONObject();
                inscription.put("idInscription", rs.getInt("idInscription"));
                inscription.put("nom", rs.getString("nom"));
                inscription.put("prenom", rs.getString("prenom"));
                inscription.put("email", rs.getString("email"));
                inscription.put("telephone", rs.getString("telephone"));
                inscription.put("dateInscription", rs.getDate("dateInscription").toString());
                inscriptions.add(inscription);
            }
        }
        return inscriptions;
    }

    public boolean approuverInscription(int idInscription) throws SQLException, MessagingException {
        String selectQuery = "SELECT * FROM InscriptionEnAttente WHERE idInscription = ?";
        String createUserQuery = "INSERT INTO Utilisateur (nom, prenom, email, motdepasse, adresse, telephone, codeSecret) VALUES (?, ?, ?, ?, ?, ?, ?)"; // Ajout du codeSecret
        String addRoleQuery = "INSERT INTO UtilisateurRole (idUtilisateur, idRole) VALUES (?, (SELECT idRole FROM Role WHERE nomRole = 'Client'))";
        String createClientQuery = "INSERT INTO Client (numeroIdentificationClient, idUtilisateur) VALUES (?, ?)";
        String createCompteQuery = "INSERT INTO Compte (solde, datecreation) VALUES (0, CURRENT_DATE)";
        String linkCompteClientQuery = "INSERT INTO CompteClient (idCompteClient, id_compte, idClient) VALUES (?, ?, ?)";
        String deleteInscriptionQuery = "DELETE FROM InscriptionEnAttente WHERE idInscription = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            connection.setAutoCommit(false);  // Début de la transaction
            selectStmt.setInt(1, idInscription);

            System.out.println("Début de l'approbation de l'inscription pour l'ID : " + idInscription);

            // Vérifier si l'inscription existe
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Aucune inscription trouvée pour l'ID : " + idInscription);
                    connection.rollback();
                    return false;
                }

                // Récupérer les informations de l'inscription (y compris le code secret)
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String email = rs.getString("email");
                String telephone = rs.getString("telephone");
                String adresse = rs.getString("adresse");
                int codeSecret = rs.getInt("codeSecret"); // Récupérer le code secret

                // Étape 1 : Création du nouvel utilisateur avec le code secret
                try (PreparedStatement createUserStmt = connection.prepareStatement(createUserQuery, Statement.RETURN_GENERATED_KEYS)) {
                    createUserStmt.setString(1, nom);
                    createUserStmt.setString(2, prenom);
                    createUserStmt.setString(3, email);
                    createUserStmt.setString(4, PersonnelDAOUtil.hacherMotDePasse("motdepasseclient"));  // Mot de passe par défaut
                    createUserStmt.setString(5, adresse);
                    createUserStmt.setString(6, telephone);
                    createUserStmt.setInt(7, codeSecret); // Ajouter le code secret

                    int affectedRows = createUserStmt.executeUpdate();
                    if (affectedRows == 0) throw new SQLException("Échec de la création de l'utilisateur");

                    try (ResultSet generatedKeys = createUserStmt.getGeneratedKeys()) {
                        if (!generatedKeys.next()) throw new SQLException("Impossible d'obtenir l'ID de l'utilisateur");
                        int idUtilisateur = generatedKeys.getInt(1);
                        System.out.println("Utilisateur créé avec l'ID : " + idUtilisateur);

                        // Étape 2 : Ajout du rôle client à l'utilisateur
                        try (PreparedStatement addRoleStmt = connection.prepareStatement(addRoleQuery)) {
                            addRoleStmt.setInt(1, idUtilisateur);
                            addRoleStmt.executeUpdate();
                            System.out.println("Rôle client ajouté pour l'utilisateur ID : " + idUtilisateur);
                        }

                        // Étape 3 : Création du client
                        try (PreparedStatement createClientStmt = connection.prepareStatement(createClientQuery, Statement.RETURN_GENERATED_KEYS)) {
                            String numeroIdentificationClient = PersonnelDAOUtil.genererNumeroIdentificationClient();
                            createClientStmt.setString(1, numeroIdentificationClient);
                            createClientStmt.setInt(2, idUtilisateur);
                            createClientStmt.executeUpdate();

                            try (ResultSet clientKeys = createClientStmt.getGeneratedKeys()) {
                                if (!clientKeys.next()) throw new SQLException("Impossible d'obtenir l'ID du client");
                                int idClient = clientKeys.getInt(1);
                                System.out.println("Client créé avec l'ID : " + idClient);

                                // Étape 4 : Création du compte client avec un solde initial de 0
                                try (PreparedStatement createCompteStmt = connection.prepareStatement(createCompteQuery, Statement.RETURN_GENERATED_KEYS)) {
                                    createCompteStmt.executeUpdate();

                                    try (ResultSet compteKeys = createCompteStmt.getGeneratedKeys()) {
                                        if (!compteKeys.next()) throw new SQLException("Impossible d'obtenir l'ID du compte");
                                        int idCompte = compteKeys.getInt(1);
                                        System.out.println("Compte créé avec l'ID : " + idCompte);

                                        // Étape 5 : Lier le compte au client
                                        try (PreparedStatement linkCompteClientStmt = connection.prepareStatement(linkCompteClientQuery)) {
                                            linkCompteClientStmt.setInt(1, idCompte);
                                            linkCompteClientStmt.setInt(2, idCompte);
                                            linkCompteClientStmt.setInt(3, idClient);
                                            linkCompteClientStmt.executeUpdate();
                                            System.out.println("Compte lié au client avec succès.");
                                        }

                                        // Étape 6 : Supprimer l'inscription en attente
                                        try (PreparedStatement deleteInscriptionStmt = connection.prepareStatement(deleteInscriptionQuery)) {
                                            deleteInscriptionStmt.setInt(1, idInscription);
                                            deleteInscriptionStmt.executeUpdate();
                                            System.out.println("Inscription en attente supprimée.");
                                        }

                                        // Étape 7 : Envoyer un email de confirmation
                                        PersonnelDAOUtil.envoyerEmailConfirmation(email, numeroIdentificationClient);
                                        System.out.println("Email de confirmation envoyé à : " + email);

                                        // Validation de la transaction
                                        connection.commit();
                                        System.out.println("Approbation de l'inscription réussie.");
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            System.err.println("Échec de l'approbation pour l'inscription ID : " + idInscription);
            e.printStackTrace();
            return false;
        } finally {
            connection.setAutoCommit(true);
            System.out.println("Réinitialisation de l'état de autoCommit.");
        }
    }

        
    

    

    // Méthode pour ajouter une question au support client
    public boolean ajouterQuestionSupport(int idCompte, String question) throws SQLException {
        String query = "INSERT INTO SupportClient (question, reponse, status, date, id_compte) VALUES (?, '', 'en_attente', CURRENT_DATE, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, question);
            pstmt.setInt(2, idCompte);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Méthode pour obtenir les questions en attente de réponse
    public List<JSONObject> getQuestionsEnAttente() throws SQLException {
        List<JSONObject> questions = new ArrayList<>();
        String query = "SELECT * FROM SupportClient WHERE status = 'en_attente'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                JSONObject question = new JSONObject();
                question.put("idSupport", rs.getInt("idSupport"));
                question.put("question", rs.getString("question"));
                question.put("date", rs.getDate("date").toString());
                question.put("id_compte", rs.getInt("id_compte"));
                questions.add(question);
            }
        }
        return questions;
    }

    // Méthode pour répondre à une question du support client
    public boolean repondreQuestion(int idSupport, String reponse, int idPersonnel) throws SQLException {
        String query = "UPDATE SupportClient SET reponse = ?, status = 'repondu', idPers = ? WHERE idSupport = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, reponse);
            pstmt.setInt(2, idPersonnel);
            pstmt.setInt(3, idSupport);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
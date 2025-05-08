package mvc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import dao.OperationDAO;
import metier.Transactions;
import utilitaires.ConnectDB;

@WebServlet("/operations")
public class OperationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Méthode doPost pour gérer les requêtes POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        System.out.println("Requête POST reçue avec action : " + action);

        JSONObject jsonResponse = new JSONObject();

        try (Connection connection = ConnectDB.getInstance().getConnection()) {
            OperationDAO operationDAO = new OperationDAO(connection);

            switch (action) {
                case "transfert_immediat":
                    handleTransfertImmediat(request, jsonResponse, operationDAO);
                    break;
                case "transfert_differe":
                    handleTransfertDiffere(request, jsonResponse, operationDAO);
                    break;
                case "transfert_automatique":
                    handleTransfertAutomatique(request, jsonResponse, operationDAO);
                    break;
                case "annuler_transaction":
                    handleAnnulerTransaction(request, jsonResponse, operationDAO);
                    break;
                default:
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Action non reconnue");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL dans doPost : " + e.getMessage());
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Erreur SQL : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur dans doPost : " + e.getMessage());
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Une erreur est survenue : " + e.getMessage());
        }

        sendJsonResponse(response, jsonResponse);
    }

    // Méthode doGet pour gérer les requêtes GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        System.out.println("Requête GET reçue avec action : " + action);

        JSONObject jsonResponse = new JSONObject();

        try (Connection connection = ConnectDB.getInstance().getConnection()) {
            OperationDAO operationDAO = new OperationDAO(connection);

            switch (action) {
                case "historique":
                    handleHistorique(request, jsonResponse, operationDAO);
                    break;
                case "transactions_annulables":
                    handleTransactionsAnnulables(request, jsonResponse, operationDAO);
                    break;
                default:
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Action non reconnue");
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL dans doGet : " + e.getMessage());
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Erreur SQL : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur dans doGet : " + e.getMessage());
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Une erreur est survenue : " + e.getMessage());
        }

        sendJsonResponse(response, jsonResponse);
    }

    // Gestion du transfert immédiat
    private void handleTransfertImmediat(HttpServletRequest request, JSONObject jsonResponse, OperationDAO operationDAO) {
        try {
            String idCompteSourceStr = request.getParameter("idCompteSource");
            String numeroDestinataire = request.getParameter("numeroDestinataire");
            String montantStr = request.getParameter("montant");

            // Debug
            System.out.println("idCompteSource: " + idCompteSourceStr);
            System.out.println("numeroDestinataire: " + numeroDestinataire);
            System.out.println("montant: " + montantStr);

            // Vérification des paramètres
            if (idCompteSourceStr == null || idCompteSourceStr.isEmpty() ||
                numeroDestinataire == null || numeroDestinataire.isEmpty() ||
                montantStr == null || montantStr.isEmpty()) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Tous les paramètres sont requis (idCompteSource, numeroDestinataire, montant).");
                return;
            }

            int idCompteSource = Integer.parseInt(idCompteSourceStr);
            double montant = Double.parseDouble(montantStr);

            if (montant <= 0) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Le montant doit être supérieur à zéro.");
                return;
            }

            // Exécution du transfert immédiat
            operationDAO.effectuerTransfert(idCompteSource, numeroDestinataire, montant);
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Transfert immédiat effectué avec succès");
        } catch (NumberFormatException e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Erreur de format des paramètres : " + e.getMessage());
        } catch (SQLException e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Erreur SQL : " + e.getMessage());
        } catch (Exception e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Une erreur inattendue s'est produite : " + e.getMessage());
        }
    }

    // Gestion du transfert différé
    private void handleTransfertDiffere(HttpServletRequest request, JSONObject jsonResponse, OperationDAO operationDAO) throws SQLException {
        int idCompteSource = Integer.parseInt(request.getParameter("idCompteSource"));
        String numeroDestinataire = request.getParameter("numeroDestinataire");
        double montant = Double.parseDouble(request.getParameter("montant"));
        LocalDate date = LocalDate.parse(request.getParameter("date"));

        System.out.println("Transfert différé de " + idCompteSource + " à " + numeroDestinataire + " pour la date " + date);
        operationDAO.programmerTransfertDiffere(idCompteSource, numeroDestinataire, montant, date);

        jsonResponse.put("status", "success");
        jsonResponse.put("message", "Transfert différé programmé avec succès");
    }

    // Gestion du transfert automatique
    private void handleTransfertAutomatique(HttpServletRequest request, JSONObject jsonResponse, OperationDAO operationDAO) throws SQLException {
        int idCompteSource = Integer.parseInt(request.getParameter("idCompteSource"));
        String numeroDestinataire = request.getParameter("numeroDestinataire");
        double montant = Double.parseDouble(request.getParameter("montant"));
        String frequence = request.getParameter("frequence");
        LocalDate dateDebut = LocalDate.parse(request.getParameter("dateDebut"));

        System.out.println("Transfert automatique de " + idCompteSource + " à " + numeroDestinataire + " montant : " + montant + ", fréquence : " + frequence);
        operationDAO.programmerTransfertAutomatique(idCompteSource, numeroDestinataire, montant, frequence, dateDebut);

        jsonResponse.put("status", "success");
        jsonResponse.put("message", "Transfert automatique programmé avec succès");
    }

    // Gestion de l'annulation d'une transaction
    private void handleAnnulerTransaction(HttpServletRequest request, JSONObject jsonResponse, OperationDAO operationDAO) throws SQLException {
        int idTransaction = Integer.parseInt(request.getParameter("idTransaction"));

        System.out.println("Annulation de la transaction ID : " + idTransaction);
        boolean annulationReussie = operationDAO.annulerTransaction(idTransaction);

        if (annulationReussie) {
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Transaction annulée avec succès");
        } else {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Impossible d'annuler la transaction (plus de 3 jours ou autre problème).");
        }
    }

    // Gestion de l'historique des transactions
    private void handleHistorique(HttpServletRequest request, JSONObject jsonResponse, OperationDAO operationDAO) throws SQLException {
        int idCompte = Integer.parseInt(request.getParameter("idCompte"));

        List<Transactions> transactions = operationDAO.getHistoriqueTransactions(idCompte);
        System.out.println("Récupération de l'historique des transactions pour le compte ID : " + idCompte);

        JSONArray transactionsArray = new JSONArray();
        for (Transactions transaction : transactions) {
            JSONObject transactionJson = new JSONObject();
            transactionJson.put("id", transaction.getId());
            transactionJson.put("montant", transaction.getMontant());
            transactionJson.put("date", transaction.getDate().toString());
            transactionJson.put("statut", transaction.getStatut());
            transactionJson.put("heure", transaction.getHeure().toString());
            transactionJson.put("typeOperation", transaction.getType());
            transactionJson.put("telephoneSource", transaction.getTelephoneSource());
            transactionJson.put("telephoneDestinataire", transaction.getTelephoneDestinataire());
            transactionsArray.add(transactionJson);
        }
        jsonResponse.put("status", "success");
        jsonResponse.put("transactions", transactionsArray);
    }
   

    // Gestion des transactions annulables (moins de 3 jours)
    private void handleTransactionsAnnulables(HttpServletRequest request, JSONObject jsonResponse, OperationDAO operationDAO) throws SQLException {
        int idCompte = Integer.parseInt(request.getParameter("idCompte"));

        List<Transactions> transactions = operationDAO.getTransactionsAnnulables(idCompte);
        System.out.println("Récupération des transactions annulables pour le compte ID : " + idCompte);

        JSONArray transactionsArray = new JSONArray();
        for (Transactions transaction : transactions) {
            JSONObject transactionJson = new JSONObject();
            transactionJson.put("id", transaction.getId());
            transactionJson.put("montant", transaction.getMontant());
            transactionJson.put("date", transaction.getDate().toString());
            transactionJson.put("statut", transaction.getStatut());
            transactionJson.put("heure", transaction.getHeure().toString());
            transactionJson.put("type", transaction.getType());
            transactionJson.put("compteSource", transaction.getCompteSource());
            transactionJson.put("compteDestinataire", transaction.getCompteDestinataire());
            transactionJson.put("telephoneDestinataire", transaction.getTelephoneDestinataire());
            transactionsArray.add(transactionJson);
        }
        
        jsonResponse.put("status", "success");
        jsonResponse.put("transactions", transactionsArray);
    }

    // Méthode utilitaire pour envoyer une réponse JSON
    private void sendJsonResponse(HttpServletResponse response, JSONObject jsonResponse) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toJSONString());
        out.flush();
    }
}

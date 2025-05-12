package mvc;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import dao.CodeRetraitDAO;
import utilitaires.ConnectDB;

@WebServlet("/gold/client/retrait")
public class CodeRetraitServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CodeRetraitDAO codeRetraitDAO;

    @Override
    public void init() {
        try {
            Connection connection = ConnectDB.getInstance().getConnection();
            codeRetraitDAO = new CodeRetraitDAO(connection);
            System.out.println("Initialisation du CodeRetraitDAO réussie");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'initialisation du CodeRetraitDAO : " + e.getMessage());
            throw new RuntimeException("Impossible d'initialiser CodeRetraitDAO.", e);  // Assurez-vous que le servlet ne fonctionne pas sans DAO
        }
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        JSONObject jsonResponse = new JSONObject();

        System.out.println("Action reçue: " + action);

        if ("generer_code".equals(action)) {
            try {
                String idCompteStr = request.getParameter("idCompte");
                String montantStr = request.getParameter("montant");

                System.out.println("idCompte reçu: " + idCompteStr);
                System.out.println("montant reçu: " + montantStr);

                if (idCompteStr == null || montantStr == null || idCompteStr.isEmpty() || montantStr.isEmpty()) {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Paramètres manquants : idCompte et montant sont obligatoires.");
                    System.out.println("Erreur: Paramètres manquants");
                } else {
                    int idCompte = Integer.parseInt(idCompteStr);
                    double montant = Double.parseDouble(montantStr);

                    System.out.println("idCompte parsé: " + idCompte);
                    System.out.println("montant parsé: " + montant);

                    if (montant <= 0) {
                        jsonResponse.put("status", "error");
                        jsonResponse.put("message", "Le montant doit être supérieur à zéro.");
                        System.out.println("Erreur: Montant invalide");
                    } else {
                        // Appel au DAO pour générer le code de retrait
                        try {
                            String codeRetrait = codeRetraitDAO.genererCodeRetrait(idCompte, montant);
                            jsonResponse.put("status", "success");
                            jsonResponse.put("codeRetrait", codeRetrait);
                            System.out.println("Code de retrait généré : " + codeRetrait);
                        } catch (SQLException e) {
                            jsonResponse.put("status", "error");
                            jsonResponse.put("message", "Erreur lors de la génération du code de retrait : " + e.getMessage());
                            System.out.println("Erreur SQL: " + e.getMessage());
                        }
                    }
                }
            } catch (NumberFormatException e) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Erreur de format des paramètres : " + e.getMessage());
                System.out.println("Erreur de format: " + e.getMessage());
            }
        } else {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Action non reconnue.");
            System.out.println("Erreur: Action non reconnue");
        }

        // Envoi de la réponse JSON
        sendJsonResponse(response, jsonResponse);
    }

    // Méthode utilitaire pour envoyer des réponses JSON
    private void sendJsonResponse(HttpServletResponse response, JSONObject jsonResponse) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse.toJSONString());
    }
}

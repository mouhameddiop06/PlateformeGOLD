package mvc;

import dao.UtilisateurDAO;
import metier.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/supprimerUtilisateur")
public class SupprimerUtilisateurServlet extends HttpServlet {
    private UtilisateurDAO utilisateurDAO;

    @Override
    public void init() throws ServletException {
        utilisateurDAO = new UtilisateurDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Log tous les paramètres reçus
        System.out.println("Tous les paramètres reçus:");
        request.getParameterMap().forEach((key, value) -> 
            System.out.println(key + ": " + String.join(", ", value))
        );

        String idUtilisateurStr = request.getParameter("idUtilisateur");
        String role = request.getParameter("role");

        System.out.println("idUtilisateur reçu: " + idUtilisateurStr);
        System.out.println("role reçu: " + role);

        // Validation des paramètres
        if (idUtilisateurStr == null || idUtilisateurStr.trim().isEmpty()) {
            sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, false, "ID utilisateur manquant.");
            return;
        }

        int idUtilisateur;
        try {
            idUtilisateur = Integer.parseInt(idUtilisateurStr);
        } catch (NumberFormatException e) {
            sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, false, "ID utilisateur invalide: " + idUtilisateurStr);
            return;
        }

        try {
            boolean suppressionReussie = utilisateurDAO.supprimerUtilisateur(idUtilisateur);
            if (suppressionReussie) {
                sendJsonResponse(response, HttpServletResponse.SC_OK, true, "Utilisateur supprimé avec succès.");
            } else {
                sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, "Erreur lors de la suppression de l'utilisateur.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception lors de la suppression de l'utilisateur: " + e.getMessage());
            sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, "Une erreur est survenue lors de la suppression de l'utilisateur: " + e.getMessage());
        }
    }

    private void sendJsonResponse(HttpServletResponse response, int statusCode, boolean success, String message) throws IOException {
        response.setStatus(statusCode);
        String jsonResponse = "{\"success\": " + success + ", \"message\": \"" + message + "\"}";
        System.out.println("Réponse envoyée: " + jsonResponse);
        response.getWriter().write(jsonResponse);
    }
}
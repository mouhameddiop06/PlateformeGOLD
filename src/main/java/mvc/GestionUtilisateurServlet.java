package mvc;

import dao.UtilisateurDAO;
import metier.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/gestionUtilisateur")
public class GestionUtilisateurServlet extends HttpServlet {
    private UtilisateurDAO utilisateurDAO;

    @Override
    public void init() throws ServletException {
        utilisateurDAO = new UtilisateurDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("ajouter".equals(action)) {
            modifierUtilisateur(request, response);
        } else if ("modifier".equals(action)) {
            modifierUtilisateur(request, response);
        } else if ("activer".equals(action) || "desactiver".equals(action)) {
            changerStatutUtilisateur(request, response);
        } else if ("supprimer".equals(action)) {
            supprimerUtilisateur(request, response);
        }
    }

   
    private void modifierUtilisateur(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int idUtilisateur = Integer.parseInt(request.getParameter("idUtilisateur"));
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String telephone = request.getParameter("telephone");
        String adresse = request.getParameter("adresse");
        String motdepasse = request.getParameter("motdepasse");
        String codesecret = request.getParameter("codesecret");

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdUtilisateur(idUtilisateur);
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(email);
        utilisateur.setTelephone(telephone);
        utilisateur.setAdresse(adresse);
        utilisateur.setMotdepasse(motdepasse);
        utilisateur.setCodesecret(Integer.parseInt(codesecret));

        boolean success = utilisateurDAO.modifierUtilisateur(utilisateur);
        if (success) {
            response.sendRedirect("listerUtilisateurs?success=modification");
        } else {
            response.sendRedirect("listerUtilisateurs?error=email_existe");
        }
    }

    private void changerStatutUtilisateur(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int idUtilisateur = Integer.parseInt(request.getParameter("idUtilisateur"));
        String statut = "activer".equals(request.getParameter("action")) ? "active" : "inactive";

        boolean success = utilisateurDAO.changerStatutUtilisateur(idUtilisateur, statut);
        if (success) {
            response.sendRedirect("listerUtilisateurs?success=status_changed");
        } else {
            response.sendRedirect("listerUtilisateurs?error=status_change_failed");
        }
    }

    private void supprimerUtilisateur(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            int idUtilisateur = Integer.parseInt(request.getParameter("idUtilisateur"));
            
            // Appel à la DAO pour supprimer l'utilisateur
            boolean success = utilisateurDAO.supprimerUtilisateur(idUtilisateur);

            // Si la suppression a réussi
            if (success) {
                // Réponse JSON pour indiquer la réussite de la suppression
                response.getWriter().write("{\"success\": true}");
            } else {
                // Réponse JSON pour indiquer l'échec de la suppression
                response.getWriter().write("{\"success\": false, \"message\": \"Erreur lors de la suppression\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Réponse JSON en cas d'erreur du serveur
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Erreur du serveur\"}");
        }
    }

}

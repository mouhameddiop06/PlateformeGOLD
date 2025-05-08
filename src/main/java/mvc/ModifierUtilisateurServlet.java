package mvc;

import dao.UtilisateurDAO;
import metier.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/modifierUtilisateur")
public class ModifierUtilisateurServlet extends HttpServlet {
    private UtilisateurDAO utilisateurDAO;

    @Override
    public void init() throws ServletException {
        utilisateurDAO = new UtilisateurDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Récupération des données du formulaire
            int idUtilisateur = Integer.parseInt(request.getParameter("idUtilisateur"));
            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");
            String email = request.getParameter("email");
            String telephone = request.getParameter("telephone");
            String adresse = request.getParameter("adresse");
            String motdepasse = request.getParameter("motdepasse");
            int codesecret = Integer.parseInt(request.getParameter("codesecret"));

            // Création de l'objet utilisateur
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setIdUtilisateur(idUtilisateur);
            utilisateur.setNom(nom);
            utilisateur.setPrenom(prenom);
            utilisateur.setEmail(email);
            utilisateur.setTelephone(telephone);
            utilisateur.setAdresse(adresse);
            utilisateur.setMotdepasse(motdepasse);
            utilisateur.setCodesecret(codesecret);

            // Mise à jour de l'utilisateur
            boolean success = utilisateurDAO.modifierUtilisateur(utilisateur);

            // Envoi de la réponse JSON
            response.getWriter().write("{\"success\": " + success + "}");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\": false, \"message\": \"Erreur lors de la modification de l'utilisateur.\"}");
        }
    }
}

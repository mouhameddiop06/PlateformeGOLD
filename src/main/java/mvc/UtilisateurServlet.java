package mvc;

import dao.UtilisateurDAO;
import metier.Utilisateur;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/admin/utilisateur")
public class UtilisateurServlet extends HttpServlet {
    private UtilisateurDAO utilisateurDAO;

    @Override
    public void init() throws ServletException {
        utilisateurDAO = new UtilisateurDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action) {
            case "lister":
                listerUtilisateurs(request, response);
                break;
            case "getUtilisateur":
                getUtilisateurById(request, response);
                break;
            default:
                response.sendRedirect("gestion_utilisateur.jsp");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action) {
            case "ajouter":
                ajouterUtilisateur(request, response);
                break;
            case "modifier":
                modifierUtilisateur(request, response);
                break;
            case "activer":
            case "desactiver":
                changerStatutUtilisateur(request, response);
                break;
            default:
                response.sendRedirect("gestion_utilisateur.jsp");
                break;
        }
    }

    // Méthode pour lister les utilisateurs par rôle (par exemple, client, agent kiosque)
    private void listerUtilisateurs(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String role = request.getParameter("role");
        List<Utilisateur> utilisateurs = utilisateurDAO.listerUtilisateursParRole(role);

        request.setAttribute("utilisateurs", utilisateurs);
        request.getRequestDispatcher("/administrateur/gestion_utilisateur.jsp").forward(request, response);
    }

    // Méthode pour ajouter un utilisateur
    private void ajouterUtilisateur(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String telephone = request.getParameter("telephone");
        String adresse = request.getParameter("adresse");
        String motdepasse = request.getParameter("motdepasse");
        int codesecret = Integer.parseInt(request.getParameter("codesecret"));  // Code secret est un entier
        String role = request.getParameter("role");

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(email);
        utilisateur.setTelephone(telephone);
        utilisateur.setAdresse(adresse);
        utilisateur.setMotdepasse(motdepasse);
        utilisateur.setCodesecret(codesecret);  // Code secret est bien un int
        utilisateur.setStatus("active");  // Par défaut, l'utilisateur est actif
        utilisateur.setRole(role);

        int idUtilisateur = utilisateurDAO.ajouterUtilisateur(utilisateur);

        // Associer le rôle à l'utilisateur si l'ajout a réussi
        if (idUtilisateur > 0) {
            utilisateurDAO.ajouterUtilisateurRole(idUtilisateur, role);
        }

        response.sendRedirect("utilisateur?action=lister&role=" + role);
    }

 // Méthode pour obtenir un utilisateur par ID et envoyer les détails en JSON
    private void getUtilisateurById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idUtilisateur = Integer.parseInt(request.getParameter("idUtilisateur"));
            Utilisateur utilisateur = utilisateurDAO.getUtilisateurById(idUtilisateur);

            if (utilisateur != null) {
                JSONObject jsonUser = new JSONObject();
                jsonUser.put("idUtilisateur", utilisateur.getIdUtilisateur());
                jsonUser.put("nom", utilisateur.getNom());
                jsonUser.put("prenom", utilisateur.getPrenom());
                jsonUser.put("email", utilisateur.getEmail());
                jsonUser.put("telephone", utilisateur.getTelephone());
                jsonUser.put("adresse", utilisateur.getAdresse());
                jsonUser.put("motdepasse", utilisateur.getMotdepasse());
                jsonUser.put("codesecret", utilisateur.getCodesecret());

                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(jsonUser.toJSONString());
                out.flush();
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utilisateur non trouvé.");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utilisateur invalide.");
        }
    }

 // Méthode pour modifier un utilisateur
    private void modifierUtilisateur(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idUtilisateur = Integer.parseInt(request.getParameter("idUtilisateur"));
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setIdUtilisateur(idUtilisateur);
            utilisateur.setNom(request.getParameter("nom"));
            utilisateur.setPrenom(request.getParameter("prenom"));
            utilisateur.setEmail(request.getParameter("email"));
            utilisateur.setTelephone(request.getParameter("telephone"));
            utilisateur.setAdresse(request.getParameter("adresse"));
            utilisateur.setMotdepasse(request.getParameter("motdepasse"));
            utilisateur.setCodesecret(Integer.parseInt(request.getParameter("codesecret")));

            boolean success = utilisateurDAO.modifierUtilisateur(utilisateur);

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            JSONObject jsonResponse = new JSONObject();
            if (success) {
                jsonResponse.put("success", true);
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Erreur lors de la modification.");
            }
            out.print(jsonResponse.toJSONString());
            out.flush();
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la modification: " + e.getMessage());
        }
    }
 // Méthode pour désactiver un utilisateur
    private void desactiverUtilisateur(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idUtilisateur = Integer.parseInt(request.getParameter("idUtilisateur"));
            boolean success = utilisateurDAO.changerStatutUtilisateur(idUtilisateur, "inactive");

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            JSONObject jsonResponse = new JSONObject();
            if (success) {
                jsonResponse.put("success", true);
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Erreur lors de la désactivation.");
            }
            out.print(jsonResponse.toJSONString());
            out.flush();
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utilisateur invalide.");
        }
    }

    // Méthode pour activer ou désactiver un utilisateur
    private void changerStatutUtilisateur(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idUtilisateurStr = request.getParameter("idUtilisateur");
        
        if (idUtilisateurStr == null || idUtilisateurStr.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"message\": \"ID utilisateur invalide.\"}");
            return;
        }

        int idUtilisateur;
        try {
            idUtilisateur = Integer.parseInt(idUtilisateurStr);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"message\": \"ID utilisateur invalide.\"}");
            return;
        }

        String statut = "inactive"; // Par défaut, on désactive l'utilisateur
        boolean success = utilisateurDAO.changerStatutUtilisateur(idUtilisateur, statut);

        if (success) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": true}");
        } else {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": false, \"message\": \"Erreur lors du changement de statut de l'utilisateur.\"}");
        }
    }

}

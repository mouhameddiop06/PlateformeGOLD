package mvc;

import dao.AddUtilisateurDAO;
import dao.UtilisateurDAO;
import metier.Utilisateur;
import metier.Personnel;
import metier.AgentKiosque;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/admin/ajouterUtilisateur")
public class AjouterUtilisateurServlet extends HttpServlet {
    private AddUtilisateurDAO addUtilisateurDAO;
    private UtilisateurDAO utilisateurDAO;

    @Override
    public void init() throws ServletException {
        addUtilisateurDAO = new AddUtilisateurDAO();
        utilisateurDAO = new UtilisateurDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Collecter les informations du formulaire
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String telephone = request.getParameter("telephone");
        String role = request.getParameter("role");
        String motdepasse = request.getParameter("motdepasse");
        String codesecret = request.getParameter("codesecret");

        try {
            // Vérifier si l'email existe déjà
            if (utilisateurDAO.emailExiste(email)) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"message\": \"email_exists\"}");
                return;
            }

            // Créer l'objet utilisateur
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setNom(nom);
            utilisateur.setPrenom(prenom);
            utilisateur.setEmail(email);
            utilisateur.setTelephone(telephone);
            utilisateur.setMotdepasse(motdepasse);
            utilisateur.setCodesecret(Integer.parseInt(codesecret));
            utilisateur.setStatus("active");
            utilisateur.setRole(role);

            // Ajouter l'utilisateur dans la table Utilisateur
            int idUtilisateur = utilisateurDAO.ajouterUtilisateur(utilisateur);

            // Vérifier si l'utilisateur a été correctement ajouté
            if (idUtilisateur > 0) {
                // Ajouter l'utilisateur dans la table UtilisateurRole
                boolean roleAjoute = utilisateurDAO.ajouterUtilisateurRole(idUtilisateur, role);

                if (!roleAjoute) {
                    throw new Exception("Erreur lors de l'ajout du rôle.");
                }

                // Ajouter l'utilisateur dans la table correspondante (Personnel ou AgentKiosque)
                if (role.equals("Personnel")) {
                    String numeroIdentificationPersonnel = addUtilisateurDAO.genererNumeroIdentificationPersonnel();
                    Personnel personnel = new Personnel();
                    personnel.setIdUtilisateur(idUtilisateur);
                    personnel.setNumeroIdentificationPers(numeroIdentificationPersonnel);
                    addUtilisateurDAO.ajouterPersonnel(personnel);
                } else if (role.equals("AgentKiosque")) {
                    String numeroIdentificationAgent = addUtilisateurDAO.genererNumeroIdentificationAgent();
                    AgentKiosque agentKiosque = new AgentKiosque();
                    agentKiosque.setIdUtilisateur(idUtilisateur);
                    agentKiosque.setNumeroIdentificationAgent(numeroIdentificationAgent);
                    addUtilisateurDAO.ajouterAgentKiosque(agentKiosque);
                }

                // Réponse JSON succès
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": true}");
            } else {
                throw new Exception("Erreur lors de l'ajout de l'utilisateur.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Réponse JSON échec
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}

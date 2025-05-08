package mvc;

import dao.AuthDAO;
import dao.RoleDAO;
import dao.AgentKiosqueDAO;
import dao.PersonnelDAO;
import dao.AdministrateurDAO;
import metier.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private AuthDAO authDAO;
    private RoleDAO roleDAO;
    private AgentKiosqueDAO agentKiosqueDAO;
    private PersonnelDAO personnelDAO;
    private AdministrateurDAO administrateurDAO;

    @Override
    public void init() throws ServletException {
        try {
            authDAO = new AuthDAO();
            roleDAO = new RoleDAO();
            agentKiosqueDAO = new AgentKiosqueDAO();
            personnelDAO = new PersonnelDAO();
            administrateurDAO = new AdministrateurDAO();
        } catch (Exception e) {
            throw new ServletException("Erreur d'initialisation", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String motdepasse = request.getParameter("motdepasse");

        try {
            // Authentification de l'utilisateur
            Utilisateur utilisateur = authDAO.authentifier(email, motdepasse);
            if (utilisateur != null) {
                // Récupérer le rôle de l'utilisateur
                String role = roleDAO.getRoleByUserId(utilisateur.getIdUtilisateur());

                // Créer une session et stocker les informations de l'utilisateur
                HttpSession session = request.getSession();
                session.setAttribute("utilisateur", utilisateur);
                session.setAttribute("role", role);
                session.setAttribute("idUtilisateur", utilisateur.getIdUtilisateur());

                // Récupérer et stocker l'ID spécifique à l'acteur (Administrateur, Personnel, Agent Kiosque)
                switch (role) {
                    case "Admin":
                        int idAdmin = administrateurDAO.getIdAdministrateurByUserId(utilisateur.getIdUtilisateur());
                        session.setAttribute("idAdministrateur", idAdmin);
                        break;
                    case "Personnel":
                        int idPersonnel = personnelDAO.getIdPersonnelByUserId(utilisateur.getIdUtilisateur());
                        session.setAttribute("idPersonnel", idPersonnel);
                        break;
                    case "AgentKiosque":
                        int idAgentKiosque = agentKiosqueDAO.getIdAgentKiosqueByUserId(utilisateur.getIdUtilisateur());
                        session.setAttribute("idAgentKiosque", idAgentKiosque);
                        break;
                }

                // Rediriger l'utilisateur vers le tableau de bord en fonction de son rôle
                redirectBasedOnRole(request, response, role);
            } else {
                // Redirection si les informations d'authentification sont invalides
                response.sendRedirect("utilisateur/connexion.jsp?error=invalid_credentials");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log l'exception pour le débogage
            response.sendRedirect("utilisateur/connexion.jsp?error=server_error");
        }
    }

    private void redirectBasedOnRole(HttpServletRequest request, HttpServletResponse response, String role) throws IOException {
        LOGGER.info("Redirection basée sur le rôle : " + role);
        switch (role) {
            case "Admin":
                LOGGER.info("Redirection vers dashboard_administrateur.jsp");
                response.sendRedirect(request.getContextPath() + "/administrateur/dashboard_administrateur.jsp");
                break;
            case "Personnel":
                LOGGER.info("Redirection vers dashboard_personnel.jsp");
                response.sendRedirect(request.getContextPath() + "/personnels/dashboard_personnel.jsp");
                break;
            case "AgentKiosque":
                LOGGER.info("Redirection vers dashboard_agentkiosque.jsp");
                response.sendRedirect(request.getContextPath() + "/agentkiosque/dashboard_agentkiosque.jsp");
                break;
            default:
                LOGGER.warning("Rôle invalide : " + role);
                response.sendRedirect(request.getContextPath() + "/utilisateur/connexion.jsp?error=invalid_role");
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("utilisateur/connexion.jsp");
    }
}
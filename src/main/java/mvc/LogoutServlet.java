package mvc;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtenir la session en cours s'il y en a une
        HttpSession session = request.getSession(false); // false signifie qu'on ne crée pas une nouvelle session si elle n'existe pas
        if (session != null) {
            // Invalider la session pour déconnecter l'utilisateur
            session.invalidate();
        }
        
        // Rediriger l'utilisateur vers la page de connexion ou la page d'accueil
        response.sendRedirect("utilisateur/connexion.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Appeler doGet pour gérer la déconnexion
        doGet(request, response);
    }
}

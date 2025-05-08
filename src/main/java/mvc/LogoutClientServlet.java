package mvc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LogoutClientServlet")
public class LogoutClientServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupérer la session actuelle
        HttpSession session = request.getSession(false);  // false signifie ne pas créer une nouvelle session s'il n'y en a pas

        // Vérifier s'il y a une session active et l'invalider
        if (session != null) {
            session.invalidate();  // Invalider la session
        }

        // Rediriger vers la page de connexion
        response.sendRedirect("client/connexion.jsp");
    }
}
 
package mvc;

import dao.ComptePrincipalBeneficeDAO;
import utilitaires.ConnectDB;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/admin/gestion-service")
public class GestionBeneficeServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            ComptePrincipalBeneficeDAO dao = new ComptePrincipalBeneficeDAO();
            double montantBenefice = dao.getMontantBenefice();
            
            // Stocker le montant dans l'attribut de la requête
            request.setAttribute("montantBenefice", Double.valueOf(montantBenefice));
            
            // Forward vers la page JSP
            request.getRequestDispatcher("/administrateur/gestion_service.jsp").forward(request, response);
            
        } catch (Exception e) {
            // Log l'erreur
            e.printStackTrace();
            
            // Stocker le message d'erreur
            request.setAttribute("erreur", "Une erreur est survenue lors de la récupération du montant : " + e.getMessage());
            
            // Forward vers la page JSP avec l'erreur
            request.getRequestDispatcher("/administrateur/gestion_service.jsp").forward(request, response);
        }
    }
}
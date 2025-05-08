package mvc;



import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utilitaires.ConnectDB;

@WebServlet("/LoginClientServlet")
public class LoginClientServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupérer les paramètres du formulaire de connexion
        String telephone = request.getParameter("telephone");
        String codeSecret = request.getParameter("codeSecret");

        // Connexion à la base de données
        try (Connection connection = ConnectDB.getInstance().getConnection()) {
            String query = "SELECT idClient FROM Client c JOIN Utilisateur u ON c.idUtilisateur = u.idUtilisateur WHERE u.telephone = ? AND u.codeSecret = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, telephone);
                ps.setInt(2, Integer.parseInt(codeSecret));

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Client trouvé, on démarre une session
                        int idClient = rs.getInt("idClient");
                        HttpSession session = request.getSession();
                        session.setAttribute("idClient", idClient);  // Stocker l'ID du client dans la session

                        // Rediriger vers la page du client
                        response.sendRedirect("client/compleclient.jsp");
                    } else {
                        // Informations de connexion incorrectes, rediriger vers la page de connexion avec un message d'erreur
                        response.sendRedirect("client/connexion.jsp?error=invalid_credentials");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("connexion.jsp?error=server_error");
        }
    }
}

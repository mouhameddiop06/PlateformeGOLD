package mvc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utilitaires.ConnectDB;

@WebServlet("/InscriptionClientServlet")
public class InscriptionClientServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupérer les données du formulaire
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String telephone = request.getParameter("telephone");
        String adresse = request.getParameter("adresse");
        int codeSecret = Integer.parseInt(request.getParameter("codeSecret"));

        // Débogage des données reçues
        System.out.println("Nom : " + nom);
        System.out.println("Prénom : " + prenom);
        System.out.println("Email : " + email);
        System.out.println("Téléphone : " + telephone);
        System.out.println("Adresse : " + adresse);
        System.out.println("Code secret : " + codeSecret);

        // Connexion à la base de données et insertion des données d'inscription
        try (Connection connection = ConnectDB.getInstance().getConnection()) {
            System.out.println("Connexion à la base de données réussie.");
            // Mise à jour de la requête SQL sans idPersonnel
            String query = "INSERT INTO InscriptionEnAttente (nom, prenom, email, telephone, adresse, dateInscription, statut, codeSecret) " +
                           "VALUES (?, ?, ?, ?, ?, CURRENT_DATE, 'en_attente', ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, nom);
                ps.setString(2, prenom);
                ps.setString(3, email);
                ps.setString(4, telephone);
                ps.setString(5, adresse);
                ps.setInt(6, codeSecret);

                // Exécution de l'insertion
                int rowsInserted = ps.executeUpdate();
                System.out.println("Nombre de lignes insérées : " + rowsInserted);

                if (rowsInserted > 0) {
                    // Redirection vers une page de confirmation
                    System.out.println("Inscription réussie, redirection vers la page de confirmation.");
                    response.sendRedirect("client/confirmation.jsp");
                } else {
                    System.out.println("Aucune ligne insérée, redirection vers la page d'erreur.");
                    response.sendRedirect("client/erreur.jsp");
                }
            }
        } catch (SQLException e) {
            // Log de l'erreur SQL
            System.out.println("Erreur SQL lors de l'insertion : " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("client/erreur.jsp");
        } catch (Exception e) {
            // Log de toute autre erreur
            System.out.println("Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("client/erreur.jsp");
        }
    }
}

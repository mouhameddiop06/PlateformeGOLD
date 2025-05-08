package mvc;

import dao.EpargneDAO;
import metier.Epargne;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utilitaires.ConnectDB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/epargne")
public class EpargneServlet extends HttpServlet {

    // Méthode doPost pour gérer les requêtes POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        System.out.println("Action reçue : " + action);

        try (Connection connection = ConnectDB.getInstance().getConnection()) {
            EpargneDAO epargneDAO = new EpargneDAO(connection);

            switch (action) {
                case "creerEpargne":
                    creerEpargne(request, response, epargneDAO, connection);
                    break;
                case "ajouterMontant":
                    ajouterMontant(request, response, epargneDAO);
                    break;
                case "retirerMontant":
                    retirerMontant(request, response, epargneDAO);
                    break;
                case "supprimerEpargne":
                    supprimerEpargne(request, response, epargneDAO);
                    break;
                case "getEpargnesClient":
                    getEpargnesClient(request, response, epargneDAO, connection);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action inconnue");
                    break;
            }
        } catch (SQLException e) {
            sendErrorResponse(response, "Erreur lors de la gestion de la base de données : " + e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(response, "Erreur générale : " + e.getMessage());
        }
    }

    // Méthode doGet pour gérer les requêtes GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);  // Redirige toutes les requêtes GET vers la méthode doPost
    }

    // Méthode pour créer une nouvelle épargne
    private void creerEpargne(HttpServletRequest request, HttpServletResponse response, EpargneDAO epargneDAO, Connection connection) throws IOException {
        try {
            System.out.println("Début création épargne");

            Integer idClient = (Integer) request.getSession().getAttribute("idClient");
            if (idClient == null) {
                sendErrorResponse(response, "ID du client manquant dans la session.");
                return;
            }

            // Récupérer l'ID du compte client associé à cet ID client
            Integer idCompteClient = null;
            String sql = "SELECT cc.id_compte FROM CompteClient cc WHERE cc.idClient = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, idClient);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idCompteClient = rs.getInt("id_compte");
                    } else {
                        throw new SQLException("Aucun compte client trouvé pour cet utilisateur.");
                    }
                }
            }

            System.out.println("ID du compte client récupéré : " + idCompteClient);

            String nomEpargne = request.getParameter("nomEpargne");
            String dateEcheanceStr = request.getParameter("dateEcheance");
            String objectifEpargneStr = request.getParameter("objectifEpargne");

            if (nomEpargne == null || dateEcheanceStr == null || objectifEpargneStr == null) {
                sendErrorResponse(response, "Paramètres manquants.");
                return;
            }

            Date dateCreation = new Date(System.currentTimeMillis());
            Date dateEcheance = Date.valueOf(dateEcheanceStr);
            BigDecimal objectifEpargne = new BigDecimal(objectifEpargneStr);

            Epargne epargne = new Epargne();
            epargne.setNomEpargne(nomEpargne);
            epargne.setDateCreation(dateCreation);
            epargne.setDateEcheance(dateEcheance);
            epargne.setObjectifEpargne(objectifEpargne);
            epargne.setMontantActuel(BigDecimal.ZERO);
            epargne.setStatut("actif");
            epargne.setIdCompteClient(idCompteClient);

            epargneDAO.creerEpargne(epargne);
            System.out.println("Épargne créée avec succès, ID: " + epargne.getIdEpargne());

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Épargne créée avec succès.");
            jsonResponse.put("idEpargne", epargne.getIdEpargne());

            response.setContentType("application/json");
            response.getWriter().write(jsonResponse.toJSONString());
            
        } catch (SQLException e) {
            sendErrorResponse(response, "Erreur lors de la création de l'épargne : " + e.getMessage());
        }
    }

    // Méthode pour ajouter un montant à l'épargne
    private void ajouterMontant(HttpServletRequest request, HttpServletResponse response, EpargneDAO epargneDAO)
            throws SQLException, IOException {
        try {
            System.out.println("Ajout d'un montant à l'épargne...");

            String idEpargneStr = request.getParameter("idEpargne");
            String montantStr = request.getParameter("montant");

            if (idEpargneStr == null || montantStr == null || idEpargneStr.isEmpty() || montantStr.isEmpty()) {
                sendErrorResponse(response, "Paramètres manquants ou incorrects.");
                return;
            }

            int idEpargne = Integer.parseInt(idEpargneStr);
            BigDecimal montant = new BigDecimal(montantStr);

            epargneDAO.ajouterMontantEpargne(idEpargne, montant);
            System.out.println("Montant ajouté à l'épargne ID : " + idEpargne);

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Montant ajouté avec succès.");
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse.toJSONString());
        } catch (Exception e) {
            sendErrorResponse(response, "Erreur lors de l'ajout du montant : " + e.getMessage());
        }
    }

    // Méthode pour retirer un montant de l'épargne
    private void retirerMontant(HttpServletRequest request, HttpServletResponse response, EpargneDAO epargneDAO)
            throws SQLException, IOException {
        try {
            System.out.println("Retrait d'un montant de l'épargne...");

            String idEpargneStr = request.getParameter("idEpargne");
            String montantStr = request.getParameter("montant");

            if (idEpargneStr == null || montantStr == null || idEpargneStr.isEmpty() || montantStr.isEmpty()) {
                sendErrorResponse(response, "Paramètres manquants.");
                return;
            }

            int idEpargne = Integer.parseInt(idEpargneStr);
            BigDecimal montant = new BigDecimal(montantStr);

            epargneDAO.retirerMontantEpargne(idEpargne, montant);
            System.out.println("Montant retiré de l'épargne ID : " + idEpargne);

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Montant retiré avec succès.");
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse.toJSONString());
        } catch (Exception e) {
            sendErrorResponse(response, "Erreur lors du retrait : " + e.getMessage());
        }
    }

    // Méthode pour récupérer les épargnes du client
    private void getEpargnesClient(HttpServletRequest request, HttpServletResponse response, EpargneDAO epargneDAO, Connection connection)
            throws IOException {
        try {
            Integer idClient = (Integer) request.getSession().getAttribute("idClient");
            if (idClient == null) {
                sendErrorResponse(response, "ID du client manquant.");
                return;
            }

            // Récupérer l'ID du compte client associé à cet ID client
            Integer idCompteClient = null;
            String sql = "SELECT cc.id_compte FROM CompteClient cc WHERE cc.idClient = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, idClient);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idCompteClient = rs.getInt("id_compte");
                    } else {
                        throw new SQLException("Aucun compte client trouvé pour cet utilisateur.");
                    }
                }
            }

            System.out.println("ID du compte client récupéré : " + idCompteClient);
            System.out.println("Récupération des épargnes pour le compte client: " + idCompteClient);

            List<Epargne> epargnes = epargneDAO.getEpargnesByCompteClient(idCompteClient);
            JSONArray jsonArray = new JSONArray();

            for (Epargne epargne : epargnes) {
                JSONObject jsonEpargne = new JSONObject();
                jsonEpargne.put("idEpargne", epargne.getIdEpargne());
                jsonEpargne.put("nomEpargne", epargne.getNomEpargne());
                jsonEpargne.put("dateCreation", epargne.getDateCreation().toString());
                jsonEpargne.put("dateEcheance", epargne.getDateEcheance().toString());
                jsonEpargne.put("montantActuel", epargne.getMontantActuel().toString());
                jsonEpargne.put("objectifEpargne", epargne.getObjectifEpargne().toString());
                jsonEpargne.put("statut", epargne.getStatut());
                jsonArray.add(jsonEpargne);
            }

            System.out.println("Nombre d'épargnes récupérées: " + epargnes.size());
            response.setContentType("application/json");
            response.getWriter().write(jsonArray.toJSONString());
        } catch (Exception e) {
            sendErrorResponse(response, "Erreur lors de la récupération des épargnes : " + e.getMessage());
        }
    }

    // Méthode pour supprimer une épargne
    private void supprimerEpargne(HttpServletRequest request, HttpServletResponse response, EpargneDAO epargneDAO)
            throws SQLException, IOException {
        try {
            System.out.println("Suppression de l'épargne...");

            String idEpargneStr = request.getParameter("idEpargne");
            if (idEpargneStr == null || idEpargneStr.isEmpty()) {
                sendErrorResponse(response, "ID de l'épargne manquant.");
                return;
            }

            int idEpargne = Integer.parseInt(idEpargneStr);
            epargneDAO.supprimerEpargne(idEpargne);
            System.out.println("Épargne supprimée avec succès, ID : " + idEpargne);

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Épargne supprimée avec succès.");
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse.toJSONString());
        } catch (Exception e) {
            sendErrorResponse(response, "Erreur lors de la suppression de l'épargne : " + e.getMessage());
        }
    }

    // Méthode utilitaire pour envoyer un message d'erreur au format JSON
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        JSONObject jsonError = new JSONObject();
        jsonError.put("status", "error");
        jsonError.put("message", message);
        response.getWriter().write(jsonError.toJSONString());
    }
}

package mvc;

import dao.UtilisateurDAO;

import dao.KiosqueDAO;
import dao.TransactionDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.simple.JSONArray;  // Ajoutez ceci pour gérer les tableaux JSON
import org.json.simple.JSONObject;

@WebServlet("/admin/dashboard")
public class DashboardAdminServlet extends HttpServlet {
    private UtilisateurDAO utilisateurDAO;
    private KiosqueDAO kiosqueDAO;
    private TransactionDAO transactionDAO;

    @Override
    public void init() throws ServletException {
        utilisateurDAO = new UtilisateurDAO();
        kiosqueDAO = new KiosqueDAO();
        transactionDAO = new TransactionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupérer les statistiques des utilisateurs
        int nbClients = utilisateurDAO.compterUtilisateursParRole("Client");
        int nbPersonnels = utilisateurDAO.compterUtilisateursParRole("Personnel");
        int nbAgentsKiosque = utilisateurDAO.compterUtilisateursParRole("AgentKiosque");

        // Récupérer les statistiques des kiosques
        int totalKiosquesActifs = kiosqueDAO.compterKiosquesActifs();
        int totalKiosquesInactifs = kiosqueDAO.compterKiosquesInactifs();

        // Récupérer les statistiques des transactions
        int totalTransactions = transactionDAO.totalTransactions();
        int transactionsAujourdhui = transactionDAO.transactionsDuJour();
        int[] transactionsParMois = transactionDAO.transactionsParMois();

        // Conversion du tableau transactionsParMois en JSONArray
        JSONArray transactionsParMoisJson = new JSONArray();
        for (int transaction : transactionsParMois) {
            transactionsParMoisJson.add(transaction);  // Ajout de chaque élément du tableau à JSONArray
        }

        // Créer un objet JSON pour renvoyer les données
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("nbClients", nbClients);
        jsonResponse.put("nbPersonnels", nbPersonnels);
        jsonResponse.put("nbAgentsKiosque", nbAgentsKiosque);
        jsonResponse.put("totalKiosquesActifs", totalKiosquesActifs);
        jsonResponse.put("totalKiosquesInactifs", totalKiosquesInactifs);
        jsonResponse.put("totalTransactions", totalTransactions);
        jsonResponse.put("transactionsAujourdhui", transactionsAujourdhui);
        jsonResponse.put("transactionsParMois", transactionsParMoisJson);  // Ajouter le JSONArray dans l'objet JSON

        // Configurer la réponse pour renvoyer du JSON
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toJSONString());
        out.flush();
    }
}

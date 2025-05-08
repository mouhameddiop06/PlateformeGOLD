package mvc;

import dao.AgentKiosqueDAO;
import metier.AgentKiosque;
import metier.Transaction;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/administrateur/AgentKiosqueServlet", "/agentkiosque/AgentKiosqueServlet", "/gold/LogoutServlet"})
public class AgentKiosqueServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(AgentKiosqueServlet.class.getName());
    private AgentKiosqueDAO agentKiosqueDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            agentKiosqueDAO = new AgentKiosqueDAO();
            logger.info("AgentKiosqueDAO initialized successfully");
        } catch (SQLException e) {
            logger.severe("Error initializing AgentKiosqueDAO: " + e.getMessage());
            throw new ServletException("Error initializing AgentKiosqueDAO", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("doGet method called");
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        logger.info("Action: " + action);

        switch (action) {
            case "list":
                listAgentsDisponibles(request, response);
                break;
            case "transactions":
                listAgentTransactions(request, response);
                break;
            case "earnings":
                getAgentEarnings(request, response);
                break;
            case "balance":
                getAgentBalance(request, response);
                break;
            default:
                logger.warning("Unknown action: " + action);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action inconnue");
        }
    }

    // Méthode pour lister les agents disponibles
    private void listAgentsDisponibles(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<AgentKiosque> agentsDisponibles = agentKiosqueDAO.listerAgentsNonAffectes();
            JSONArray jsonArray = new JSONArray();
            for (AgentKiosque agent : agentsDisponibles) {
                JSONObject jsonAgent = new JSONObject();
                jsonAgent.put("idAgentKiosque", agent.getIdAgentKiosque());
                jsonAgent.put("numeroIdentificationAgent", agent.getNumeroIdentificationAgent());
                jsonAgent.put("email", agent.getEmail());
                jsonArray.add(jsonAgent);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();
            out.print(jsonArray.toJSONString());
            out.flush();
        } catch (Exception e) {
            logger.severe("Erreur lors de la récupération des agents disponibles : " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error retrieving available agents: " + e.getMessage());
        }
    }

 // Méthode mise à jour dans le servlet
    private void listAgentTransactions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int agentId = Integer.parseInt(request.getSession().getAttribute("idAgentKiosque").toString());

        try {
            List<Transaction> transactions = agentKiosqueDAO.getLast10Transactions(agentId);

            JSONArray jsonArray = new JSONArray();
            for (Transaction transaction : transactions) {
                JSONObject jsonTransaction = new JSONObject();
                jsonTransaction.put("idTransaction", transaction.getId());
                jsonTransaction.put("montant", transaction.getMontant());
                jsonTransaction.put("date", transaction.getDate().toString());
                jsonTransaction.put("heure", transaction.getHeure().toString());
                jsonTransaction.put("statut", transaction.getStatut());
                jsonTransaction.put("telephoneSource", transaction.getTelephoneSource());
                jsonTransaction.put("clientPhone", transaction.getTelephoneDestinataire());
                jsonTransaction.put("typeTransaction", transaction.getTypeTransaction());
                jsonArray.add(jsonTransaction);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(jsonArray.toJSONString());
            out.flush();
        } catch (SQLException e) {
            logger.severe("Erreur lors de la récupération des transactions : " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des transactions.");
        }
    }

    // Méthode pour afficher le gain mensuel et le solde de l'agent kiosque
    private void getAgentEarnings(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int agentId = Integer.parseInt(request.getSession().getAttribute("idAgentKiosque").toString());

        try {
            double earnings = agentKiosqueDAO.getAgentEarnings(agentId);
            double balance = agentKiosqueDAO.getSoldeAgent(agentId);

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("earnings", earnings);
            jsonResponse.put("balance", balance);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();
            out.print(jsonResponse.toJSONString());
            out.flush();
        } catch (SQLException e) {
            logger.severe("Erreur lors de la récupération des gains ou du solde : " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des gains ou du solde.");
        }
    }

    // Méthode pour afficher le solde de l'agent kiosque
    private void getAgentBalance(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int agentId = Integer.parseInt(request.getSession().getAttribute("idAgentKiosque").toString());

        try {
            double balance = agentKiosqueDAO.getSoldeAgent(agentId);

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("balance", balance);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonResponse.toJSONString());
        } catch (SQLException e) {
            logger.severe("Erreur lors de la récupération du solde : " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Erreur lors de la récupération du solde.\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action non spécifiée");
            return;
        }

        switch (action) {
            case "deposit":
                handleDeposit(request, response);
                break;
            case "withdraw":
                handleWithdrawal(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action inconnue");
                break;
        }
    }

    // Méthode pour gérer les dépôts
    private void handleDeposit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String clientPhoneNumber = request.getParameter("phoneNumber");
        double amount = Double.parseDouble(request.getParameter("amount"));
        int agentId = Integer.parseInt(request.getSession().getAttribute("idAgentKiosque").toString());

        try {
            if (agentKiosqueDAO.soldeSuffisantPourDepot(agentId, amount)) {
                boolean success = agentKiosqueDAO.faireDepot(clientPhoneNumber, amount, agentId);
                if (success) {
                    response.getWriter().write("Dépôt effectué avec succès.");
                    logger.info("Dépôt réussi pour le client " + clientPhoneNumber);
                } else {
                    response.getWriter().write("Erreur: Client non trouvé ou solde insuffisant.");
                    logger.warning("Dépôt échoué pour le client " + clientPhoneNumber);
                }
            } else {
                response.getWriter().write("Erreur: Solde insuffisant sur le compte de l'agent.");
                logger.warning("Erreur: Solde insuffisant sur le compte de l'agent pour effectuer un dépôt.");
            }
        } catch (SQLException e) {
            logger.severe("Erreur lors du dépôt : " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du dépôt.");
        }
    }

    // Méthode pour gérer les retraits avec validation du code de retrait
    private void handleWithdrawal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String withdrawalCode = request.getParameter("withdrawalCode");
        int agentId = Integer.parseInt(request.getSession().getAttribute("idAgentKiosque").toString());

        try {
            boolean success = agentKiosqueDAO.validerRetrait(withdrawalCode, agentId);
            if (success) {
                response.getWriter().write("Retrait effectué avec succès.");
                logger.info("Retrait validé avec succès pour le code: " + withdrawalCode);
            } else {
                response.getWriter().write("Erreur: Code de retrait invalide ou solde insuffisant.");
                logger.warning("Échec du retrait pour le code: " + withdrawalCode);
            }
        } catch (SQLException e) {
            logger.severe("Erreur lors du retrait : " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du retrait.");
        }
    }
}

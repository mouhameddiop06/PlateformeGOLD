package mvc;

import dao.PersonnelDAO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.BufferedReader;
import java.sql.SQLException;
import java.util.List;
import javax.mail.MessagingException;

@WebServlet("/personnel/*")
public class PersonnelServlet extends HttpServlet {
    private PersonnelDAO personnelDAO;

    @Override
    public void init() throws ServletException {
        try {
            personnelDAO = new PersonnelDAO();
            System.out.println("PersonnelDAO initialisé avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de PersonnelDAO: " + e.getMessage());
            throw new ServletException("Erreur d'initialisation de PersonnelDAO", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession();
        Integer idPersonnel = (Integer) session.getAttribute("idPersonnel");

        System.out.println("Requête GET reçue: " + pathInfo);
        System.out.println("ID du personnel: " + idPersonnel);

        if (idPersonnel == null) {
            System.out.println("Utilisateur non authentifié");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utilisateur non authentifié");
            return;
        }

        try {
            if ("/inscriptionsEnAttente".equals(pathInfo)) {
                System.out.println("Récupération des inscriptions en attente");
                getInscriptionsEnAttente(response);
            } else if ("/questionsEnAttente".equals(pathInfo)) {
                System.out.println("Récupération des questions en attente");
                getQuestionsEnAttente(response);
            } else {
                System.out.println("Route non trouvée: " + pathInfo);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des données");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession();
        Integer idPersonnel = (Integer) session.getAttribute("idPersonnel");

        System.out.println("Requête POST reçue: " + pathInfo);
        System.out.println("ID du personnel: " + idPersonnel);

        if (idPersonnel == null) {
            System.out.println("Utilisateur non authentifié");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utilisateur non authentifié");
            return;
        }

        try {
            if ("/approuverInscription".equals(pathInfo)) {
                System.out.println("Approbation d'une inscription");
                approuverInscription(request, response);
            } else if ("/repondreQuestion".equals(pathInfo)) {
                System.out.println("Réponse à une question");
                repondreQuestion(request, response, idPersonnel);
            } else {
                System.out.println("Route non trouvée: " + pathInfo);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException | MessagingException e) {
            System.err.println("Erreur lors du traitement de la requête: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du traitement de la requête");
        }
    }

    private void getInscriptionsEnAttente(HttpServletResponse response) throws SQLException, IOException {
        List<JSONObject> inscriptions = personnelDAO.getInscriptionsEnAttente();
        System.out.println("Nombre d'inscriptions en attente: " + inscriptions.size());
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(inscriptions);
        sendJsonResponse(response, jsonArray);
    }

    private void getQuestionsEnAttente(HttpServletResponse response) throws SQLException, IOException {
        List<JSONObject> questions = personnelDAO.getQuestionsEnAttente();
        System.out.println("Nombre de questions en attente: " + questions.size());
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(questions);
        sendJsonResponse(response, jsonArray);
    }

    private void approuverInscription(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, MessagingException {
        JSONObject jsonRequest = parseJsonRequest(request);
        int idInscription = ((Long) jsonRequest.get("idInscription")).intValue();
        
        System.out.println("Approbation de l'inscription ID: " + idInscription);
        
        boolean success = personnelDAO.approuverInscription(idInscription);
        JSONObject jsonResponse = new JSONObject();
        if (success) {
            System.out.println("Inscription approuvée avec succès");
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Inscription approuvée. Un email avec le code temporaire a été envoyé au client.");
        } else {
            System.out.println("Échec de l'approbation de l'inscription");
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Erreur lors de l'approbation de l'inscription.");
        }
        sendJsonResponse(response, jsonResponse);
    }

    private void repondreQuestion(HttpServletRequest request, HttpServletResponse response, int idPersonnel) throws SQLException, IOException {
        JSONObject jsonRequest = parseJsonRequest(request);
        int idSupport = ((Long) jsonRequest.get("idSupport")).intValue();
        String reponse = (String) jsonRequest.get("reponse");
        
        System.out.println("Réponse à la question ID: " + idSupport);
        System.out.println("Réponse: " + reponse);
        
        boolean success = personnelDAO.repondreQuestion(idSupport, reponse, idPersonnel);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", success);
        if (success) {
            System.out.println("Réponse enregistrée avec succès");
        } else {
            System.out.println("Échec de l'enregistrement de la réponse");
        }
        sendJsonResponse(response, jsonResponse);
    }

    private JSONObject parseJsonRequest(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(sb.toString());
            System.out.println("Requête JSON reçue: " + jsonObject.toJSONString());
            return jsonObject;
        } catch (ParseException e) {
            System.err.println("Erreur lors de l'analyse du JSON: " + e.getMessage());
            throw new IOException("Erreur lors de l'analyse du JSON", e);
        }
    }

    private void sendJsonResponse(HttpServletResponse response, JSONObject jsonObject) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        System.out.println("Réponse JSON envoyée: " + jsonObject.toJSONString());
        response.getWriter().write(jsonObject.toJSONString());
    }

    private void sendJsonResponse(HttpServletResponse response, JSONArray jsonArray) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        System.out.println("Réponse JSON Array envoyée: " + jsonArray.toJSONString());
        response.getWriter().write(jsonArray.toJSONString());
    }
}
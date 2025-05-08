package mvc;

import dao.PersonnelDAO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/personnel/agentsKiosque")
public class PersonnelAgentsServlet extends HttpServlet {
    private PersonnelDAO personnelDAO;

    @Override
    public void init() throws ServletException {
        try {
            personnelDAO = new PersonnelDAO();
        } catch (SQLException e) {
            throw new ServletException("Erreur d'initialisation de PersonnelDAO", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Début de la méthode doGet");
        
        HttpSession session = request.getSession();
        Integer idPersonnel = (Integer) session.getAttribute("idPersonnel");
        
        System.out.println("ID Personnel récupéré: " + idPersonnel);
        
        if (idPersonnel == null) {
            System.out.println("Utilisateur non authentifié");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utilisateur non authentifié");
            return;
        }
        
        try {
            List<Map<String, Object>> agents = personnelDAO.getAgentsKiosque();
            System.out.println("Nombre d'agents récupérés: " + agents.size());
            
            JSONArray jsonArray = new JSONArray();
            for (Map<String, Object> agent : agents) {
                JSONObject jsonAgent = new JSONObject();
                jsonAgent.put("idAgentKiosque", agent.get("idAgentKiosque"));
                jsonAgent.put("numeroIdentificationAgent", agent.get("numeroIdentificationAgent"));
                jsonAgent.put("gainMensuel", agent.get("gainMensuel"));
                jsonAgent.put("tauxCommission", agent.get("tauxCommission"));
                jsonAgent.put("numeroIdentificationKiosque", agent.get("numeroIdentificationKiosque"));
                jsonAgent.put("etatKiosque", agent.get("etatKiosque"));
                jsonArray.add(jsonAgent);
            }
            
            System.out.println("JSON à envoyer: " + jsonArray.toJSONString());
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            response.getWriter().write(jsonArray.toJSONString());
            System.out.println("Réponse JSON envoyée avec succès");
            
        } catch (SQLException e) {
            System.out.println("Erreur SQL: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des agents kiosque");
        }
    }
}
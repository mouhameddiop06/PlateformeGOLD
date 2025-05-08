package mvc;

import dao.UtilisateurDAO;
import metier.Utilisateur;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/listerUtilisateurs")
public class ListerUtilisateurServlet extends HttpServlet {
    private UtilisateurDAO utilisateurDAO;

    @Override
    public void init() throws ServletException {
        utilisateurDAO = new UtilisateurDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupérer la liste des clients
        List<Utilisateur> clients = utilisateurDAO.listerUtilisateursParRole("Client");
        System.out.println("Clients:");
        for (Utilisateur client : clients) {
            System.out.println("Nom: " + client.getNom() + ", Email: " + client.getEmail());
        }

        // Récupérer la liste des personnels
        List<Utilisateur> personnels = utilisateurDAO.listerUtilisateursParRole("Personnel");
        System.out.println("Personnels:");
        for (Utilisateur personnel : personnels) {
            System.out.println("Nom: " + personnel.getNom() + ", Email: " + personnel.getEmail());
        }

        // Récupérer la liste des agents de kiosque
        List<Utilisateur> agentsKiosque = utilisateurDAO.listerUtilisateursParRole("AgentKiosque");
        System.out.println("Agents Kiosque:");
        for (Utilisateur agent : agentsKiosque) {
            System.out.println("Nom: " + agent.getNom() + ", Email: " + agent.getEmail());
        }

        // Construire l'objet JSON
        JSONObject jsonResponse = new JSONObject();
        JSONArray clientsArray = new JSONArray();
        JSONArray personnelsArray = new JSONArray();
        JSONArray agentsArray = new JSONArray();

        // Ajouter les clients dans le JSON
        for (Utilisateur client : clients) {
            JSONObject clientJson = new JSONObject();
            clientJson.put("id", client.getIdUtilisateur());
            clientJson.put("nom", client.getNom());
            clientJson.put("prenom", client.getPrenom());
            clientJson.put("email", client.getEmail());
            clientJson.put("telephone", client.getTelephone());
            clientsArray.add(clientJson);
        }
        jsonResponse.put("clients", clientsArray);

        // Ajouter les personnels dans le JSON
        for (Utilisateur personnel : personnels) {
            JSONObject personnelJson = new JSONObject();
            personnelJson.put("id", personnel.getIdUtilisateur());
            personnelJson.put("nom", personnel.getNom());
            personnelJson.put("prenom", personnel.getPrenom());
            personnelJson.put("email", personnel.getEmail());
            personnelJson.put("telephone", personnel.getTelephone());
            personnelsArray.add(personnelJson);
        }
        jsonResponse.put("personnels", personnelsArray);

        // Ajouter les agents de kiosque dans le JSON
        for (Utilisateur agent : agentsKiosque) {
            JSONObject agentJson = new JSONObject();
            agentJson.put("id", agent.getIdUtilisateur());
            agentJson.put("nom", agent.getNom());
            agentJson.put("prenom", agent.getPrenom());
            agentJson.put("email", agent.getEmail());
            agentJson.put("telephone", agent.getTelephone());
            agentsArray.add(agentJson);
        }
        jsonResponse.put("agentsKiosque", agentsArray);

        // Configuration de la réponse
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Envoyer le JSON en réponse
        response.getWriter().write(jsonResponse.toJSONString());
    }
}

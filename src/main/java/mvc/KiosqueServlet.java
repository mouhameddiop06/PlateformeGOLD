package mvc;

import dao.KiosqueDAO;
import dao.AgentKiosqueDAO;
import metier.Kiosque;
import metier.AgentKiosque;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/administrateur/KiosqueServlet")
public class KiosqueServlet extends HttpServlet {
    private KiosqueDAO kiosqueDAO;
    private AgentKiosqueDAO agentKiosqueDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            kiosqueDAO = new KiosqueDAO();
            agentKiosqueDAO = new AgentKiosqueDAO();
        } catch (SQLException e) {
            throw new ServletException("Error initializing DAOs", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "list":
                listKiosques(request, response);
                break;
            case "get":
                getKiosque(request, response); // Ajout pour récupérer les détails d'un kiosque spécifique
                break;
            case "getAgentsDisponibles":
                getAgentsDisponibles(request, response);
                break;
            default:
                listKiosques(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action) {
            case "add":
                addKiosque(request, response);
                break;
            case "update":
                updateKiosque(request, response);
                break;
            case "delete":
                deleteKiosque(request, response);
                break;
            default:
                listKiosques(request, response);
        }
    }

    // Méthode pour récupérer la liste des kiosques
    private void listKiosques(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Kiosque> kiosques = kiosqueDAO.obtenirTousLesKiosques();
            JSONArray jsonArray = new JSONArray();

            for (Kiosque kiosque : kiosques) {
                JSONObject jsonKiosque = new JSONObject();
                jsonKiosque.put("idKiosque", kiosque.getIdKiosque());
                jsonKiosque.put("numeroIdentificationKiosque", kiosque.getNumeroIdentificationKiosque());
                jsonKiosque.put("etatKiosque", kiosque.getEtatKiosque());
                jsonKiosque.put("latitude", kiosque.getLatitude());
                jsonKiosque.put("longitude", kiosque.getLongitude());
                jsonKiosque.put("adresse", kiosque.getAdresse());
                jsonKiosque.put("emailAgent", kiosque.getEmailAgent());
                jsonArray.add(jsonKiosque);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(jsonArray.toJSONString());
            out.flush();
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error retrieving kiosques: " + e.getMessage());
        }
    }

    private void getKiosque(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int idKiosque = Integer.parseInt(request.getParameter("idKiosque"));
            Kiosque kiosque = kiosqueDAO.getKiosqueById(idKiosque);

            if (kiosque != null) {
                JSONObject jsonKiosque = new JSONObject();
                jsonKiosque.put("idKiosque", kiosque.getIdKiosque());
                jsonKiosque.put("numeroIdentificationKiosque", kiosque.getNumeroIdentificationKiosque());
                jsonKiosque.put("etatKiosque", kiosque.getEtatKiosque());
                jsonKiosque.put("latitude", kiosque.getLatitude());
                jsonKiosque.put("longitude", kiosque.getLongitude());
                jsonKiosque.put("adresse", kiosque.getAdresse());
                jsonKiosque.put("idAgentKiosque", kiosque.getIdAgentKiosque());
                jsonKiosque.put("horaires", kiosque.getHoraires());

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.print(jsonKiosque.toJSONString());
                out.flush();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Kiosque non trouvé");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid kiosk ID: " + e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erreur lors de la récupération du kiosque: " + e.getMessage());
        }
    }


    // Méthode pour ajouter un kiosque
    private void addKiosque(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Kiosque kiosque = new Kiosque();
            kiosque.setEtatKiosque(request.getParameter("etatKiosque"));
            kiosque.setLatitude(Double.parseDouble(request.getParameter("latitude")));
            kiosque.setLongitude(Double.parseDouble(request.getParameter("longitude")));
            kiosque.setAdresse(request.getParameter("adresse"));
            kiosque.setIdAgentKiosque(Integer.parseInt(request.getParameter("idAgentKiosque")));

            // Récupérer l'ID de l'administrateur depuis la session
            HttpSession session = request.getSession();
            Integer idAdministrateur = (Integer) session.getAttribute("idAdministrateur");
            if (idAdministrateur == null) {
                throw new ServletException("L'ID de l'administrateur n'est pas disponible. Veuillez vous reconnecter.");
            }
            kiosque.setIdAdministrateur(idAdministrateur);

            // Gérer les horaires
            String horairesJson = request.getParameter("horaires");
            kiosque.setHoraires(horairesJson);

            boolean success = kiosqueDAO.ajouterKiosque(kiosque);

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", success);
            if (success) {
                jsonResponse.put("message", "Kiosque ajouté avec succès");
                jsonResponse.put("numeroIdentification", kiosque.getNumeroIdentificationKiosque());
            } else {
                jsonResponse.put("message", "Erreur lors de l'ajout du kiosque");
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(jsonResponse.toJSONString());
            out.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erreur lors de l'ajout du kiosque: " + e.getMessage());
        }
    }

    // Méthode pour mettre à jour un kiosque
    private void updateKiosque(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int idKiosque = Integer.parseInt(request.getParameter("idKiosque"));
            Kiosque kiosque = kiosqueDAO.getKiosqueById(idKiosque);

            if (kiosque != null) {
                kiosque.setEtatKiosque(request.getParameter("etatKiosque"));
                kiosque.setLatitude(Double.parseDouble(request.getParameter("latitude")));
                kiosque.setLongitude(Double.parseDouble(request.getParameter("longitude")));
                kiosque.setAdresse(request.getParameter("adresse"));
                kiosque.setIdAgentKiosque(Integer.parseInt(request.getParameter("idAgentKiosque")));

                // Gérer les horaires
                String horairesJson = request.getParameter("horaires");
                kiosque.setHoraires(horairesJson);

                boolean success = kiosqueDAO.modifierKiosque(kiosque);

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("success", success);
                if (success) {
                    jsonResponse.put("message", "Kiosque modifié avec succès");
                } else {
                    jsonResponse.put("message", "Erreur lors de la modification du kiosque");
                }

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.print(jsonResponse.toJSONString());
                out.flush();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Kiosque non trouvé");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erreur lors de la modification du kiosque: " + e.getMessage());
        }
    }

    // Méthode pour supprimer un kiosque
    private void deleteKiosque(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int idKiosque = Integer.parseInt(request.getParameter("idKiosque"));
            boolean success = kiosqueDAO.supprimerKiosque(idKiosque);

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", success);
            if (success) {
                jsonResponse.put("message", "Kiosque supprimé avec succès");
            } else {
                jsonResponse.put("message", "Erreur lors de la suppression du kiosque");
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(jsonResponse.toJSONString());
            out.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erreur lors de la suppression du kiosque: " + e.getMessage());
        }
    }

    // Méthode pour récupérer les agents kiosques disponibles
    private void getAgentsDisponibles(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<AgentKiosque> agentsDisponibles = agentKiosqueDAO.listerAgentsDisponibles();
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
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error retrieving available agents: " + e.getMessage());
        }
    }
}


package dao;
import dao.AgentKiosqueDAO;
import metier.AgentKiosque;

import java.sql.SQLException;
import java.util.List;

public class TestAgentKiosqueDAO {

    public static void main(String[] args) {
        try {
            // Instanciation du DAO
            AgentKiosqueDAO agentKiosqueDAO = new AgentKiosqueDAO();

            // Appel de la méthode listerAgentsNonAffectes pour obtenir la liste des agents disponibles
            List<AgentKiosque> agentsDisponibles = agentKiosqueDAO.listerAgentsNonAffectes();

            // Vérification et affichage des résultats
            if (agentsDisponibles != null && !agentsDisponibles.isEmpty()) {
                System.out.println("Liste des agents kiosques non affectés :");
                for (AgentKiosque agent : agentsDisponibles) {
                    System.out.println("ID: " + agent.getIdAgentKiosque() +
                                       ", Email: " + agent.getEmail() +
                                       ", Numéro Identification: " + agent.getNumeroIdentificationAgent());
                }
            } else {
                System.out.println("Aucun agent kiosque disponible.");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation du DAO : " + e.getMessage());
            e.printStackTrace();
        }
    }
}


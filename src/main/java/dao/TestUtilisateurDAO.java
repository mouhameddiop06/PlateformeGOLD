package dao;

import dao.UtilisateurDAO;

public class TestUtilisateurDAO {

    public static void main(String[] args) {
        // Créer une instance de votre DAO
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

        // Test de la méthode compterUtilisateursParRole
        try {
            // Vérifiez le nombre d'utilisateurs pour chaque rôle
            int nbClients = utilisateurDAO.compterUtilisateursParRole("Client");
            int nbPersonnels = utilisateurDAO.compterUtilisateursParRole("Personnel");
            int nbAgentsKiosque = utilisateurDAO.compterUtilisateursParRole("AgentKiosque");

            // Afficher les résultats pour vérifier
            System.out.println("Nombre de Clients: " + nbClients);
            System.out.println("Nombre de Personnels: " + nbPersonnels);
            System.out.println("Nombre d'Agents Kiosque: " + nbAgentsKiosque);

        } catch (Exception e) {
            e.printStackTrace();  // Afficher les erreurs s'il y en a
        }
    }
}

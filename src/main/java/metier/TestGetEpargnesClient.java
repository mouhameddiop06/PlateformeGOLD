package metier;

import dao.EpargneDAO;

import metier.Epargne;
import utilitaires.ConnectDB;

import java.sql.Connection;
import java.util.List;

public class TestGetEpargnesClient {
    public static void main(String[] args) {
        // Initialiser la connexion à la base de données
        try (Connection connection = ConnectDB.getInstance().getConnection()) {
            // Créer une instance du DAO avec la connexion
            EpargneDAO epargneDAO = new EpargneDAO(connection);

            // ID du compte client que vous souhaitez tester
            int idCompteClient = 8; // Remplacez par l'ID de votre compte client

            // Récupérer les épargnes du compte client
            List<Epargne> epargnes = epargneDAO.getEpargnesByCompteClient(idCompteClient);

            // Afficher les épargnes récupérées
            if (epargnes.isEmpty()) {
                System.out.println("Aucune épargne trouvée pour le compte client ID: " + idCompteClient);
            } else {
                System.out.println("Nombre d'épargnes récupérées: " + epargnes.size());
                for (Epargne epargne : epargnes) {
                    System.out.println("Épargne ID: " + epargne.getIdEpargne());
                    System.out.println("Nom: " + epargne.getNomEpargne());
                    System.out.println("Montant Actuel: " + epargne.getMontantActuel());
                    System.out.println("Objectif: " + epargne.getObjectifEpargne());
                    System.out.println("Date de Création: " + epargne.getDateCreation());
                    System.out.println("Date d'Échéance: " + epargne.getDateEcheance());
                    System.out.println("Statut: " + epargne.getStatut());
                    System.out.println("--------------------------------------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

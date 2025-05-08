package mvc;

import java.sql.Connection;
import java.util.List;

import dao.OperationDAO;
import metier.Transactions;
import utilitaires.ConnectDB;

public class TestGetTransactionsAnnulables {

    public static void main(String[] args) {
        Connection connection = null;
        OperationDAO operationDAO = null;

        try {
            // 1. Connexion à la base de données
            connection = ConnectDB.getInstance().getConnection();
            operationDAO = new OperationDAO(connection);

            // 2. ID du compte à tester
            int idCompte = 8; // Remplacez avec un ID de compte valide dans votre base de données

            // 3. Récupérer les transactions annulables
            List<Transactions> transactionsAnnulables = operationDAO.getTransactionsAnnulables(idCompte);

            // 4. Vérifier s'il y a des transactions annulables et les afficher
            if (transactionsAnnulables.isEmpty()) {
                System.out.println("Aucune transaction annulable trouvée pour le compte ID: " + idCompte);
            } else {
                System.out.println("Transactions annulables pour le compte ID: " + idCompte);
                for (Transactions transaction : transactionsAnnulables) {
                    System.out.println("ID Transaction: " + transaction.getId() +
                                       ", Montant: " + transaction.getMontant() +
                                       ", Date: " + transaction.getDate() +
                                       ", Heure: " + transaction.getHeure() +
                                       ", Statut: " + transaction.getStatut() +
                                       ", Compte Source: " + transaction.getCompteSource() +
                                       ", telephoneDestinataire: " + transaction.getTelephoneDestinataire() +
                                       ", Type: " + transaction.getType());
                }
            }

        } catch (Exception e) {
            System.out.println("Erreur lors du test des transactions annulables : " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception e) {
                System.out.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}

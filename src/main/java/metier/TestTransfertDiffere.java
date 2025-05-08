package metier;

import dao.TransfertDiffereDAO;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import utilitaires.ConnectDB;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class TestTransfertDiffere {

    public static void main(String[] args) {
        try {
            Connection connection = utilitaires.ConnectDB.getInstance().getConnection();
            TransfertDiffereDAO dao = new TransfertDiffereDAO(connection);

            long idCompteSource = 8;
            String numeroDestinataire = "777231600";
            double montant = 100;
            LocalDateTime dateExecution = LocalDateTime.now().withSecond(0).withNano(0).plusMinutes(1);

            try {
                dao.programmerTransfertDiffere((int)idCompteSource, numeroDestinataire, montant, dateExecution);
                System.out.println("Transfert différé programmé avec succès pour le " +
                        dateExecution.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

                System.out.println("\nDétails du transfert différé programmé :");
                System.out.println("Compte source : " + idCompteSource);
                System.out.println("Numéro destinataire : " + numeroDestinataire);
                System.out.println("Montant : " + montant);
                System.out.println("Date d'exécution : " + dateExecution);

                System.out.println("\nSolde avant exécution :");
                JSONObject detailsCompteSource = dao.getDetailsCompte(idCompteSource);
                System.out.println(detailsCompteSource.toJSONString());

                System.out.println("\nAttente de l'exécution du transfert différé...");
                Thread.sleep(1 * 60 * 1000); // Attendre 2 minutes

                System.out.println("\nExécution des transferts différés en attente...");
                dao.executerTransfertsDifferes();

                System.out.println("\nSolde après exécution :");
                detailsCompteSource = dao.getDetailsCompte(idCompteSource);
                System.out.println(detailsCompteSource.toJSONString());

                System.out.println("\nVérification du statut des transferts différés :");
                JSONArray transferts = dao.getTransfertsDifferes();
                System.out.println(transferts.toJSONString());

            } catch (Exception e) {
                System.err.println("Erreur lors de la programmation ou l'exécution du transfert différé : " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
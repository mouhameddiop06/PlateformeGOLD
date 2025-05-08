package metier;



import dao.EpargneDAO;
import metier.Epargne;
import utilitaires.ConnectDB;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TestEpargneDAO {

    public static void main(String[] args) {
        try {
            // Obtenir une connexion à la base de données
            Connection connection = ConnectDB.getInstance().getConnection();
            EpargneDAO epargneDAO = new EpargneDAO(connection);

            // 1. Test de la création d'une nouvelle épargne
            Epargne nouvelleEpargne = new Epargne();
            nouvelleEpargne.setNomEpargne("Test Epargne");
            nouvelleEpargne.setDateCreation(new java.sql.Date(System.currentTimeMillis()));
            nouvelleEpargne.setDateEcheance(java.sql.Date.valueOf("2024-12-31"));
            nouvelleEpargne.setObjectifEpargne(new BigDecimal("100000"));
            nouvelleEpargne.setIdCompteClient(8);  // Assurez-vous d'utiliser un ID compte client existant

            epargneDAO.creerEpargne(nouvelleEpargne);
            System.out.println("Épargne créée avec succès avec l'ID: " + nouvelleEpargne.getIdEpargne());

            // 2. Test de l'ajout d'un montant à l'épargne
            BigDecimal montantAjout = new BigDecimal("50000");
            epargneDAO.ajouterMontantEpargne(nouvelleEpargne.getIdEpargne(), montantAjout);
            System.out.println("Montant de " + montantAjout + " ajouté avec succès à l'épargne ID: " + nouvelleEpargne.getIdEpargne());

            // 3. Test de la récupération des épargnes pour un client donné
            int idCompteClient = 8;  // Utilisez un ID compte client existant
            List<Epargne> epargnes = epargneDAO.getEpargnesByCompteClient(idCompteClient);

            if (epargnes.isEmpty()) {
                System.out.println("Aucune épargne trouvée pour le compte client ID: " + idCompteClient);
            } else {
                System.out.println("Épargnes trouvées pour le compte client ID: " + idCompteClient + ":");
                for (Epargne epargne : epargnes) {
                    System.out.println("- " + epargne.getNomEpargne() + " (Montant actuel: " + epargne.getMontantActuel() + " FCFA, Objectif: " + epargne.getObjectifEpargne() + " FCFA)");
                }
            }

            // 4. Test du retrait d'un montant de l'épargne
            BigDecimal montantRetrait = new BigDecimal("20000");
            epargneDAO.retirerMontantEpargne(nouvelleEpargne.getIdEpargne(), montantRetrait);
            System.out.println("Montant de " + montantRetrait + " retiré avec succès de l'épargne ID: " + nouvelleEpargne.getIdEpargne());

           
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors des opérations sur l'épargne : " + e.getMessage());
        }
    }
}

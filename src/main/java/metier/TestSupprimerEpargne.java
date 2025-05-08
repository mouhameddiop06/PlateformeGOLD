package metier;
import dao.EpargneDAO;

import java.sql.Connection;
import java.sql.SQLException;

public class TestSupprimerEpargne {
    public static void main(String[] args) {
        try {
            Connection connection = utilitaires.ConnectDB.getInstance().getConnection();
            EpargneDAO epargneDAO = new EpargneDAO(connection);

            int idEpargne = 16; // ID de l'épargne à supprimer

            epargneDAO.supprimerEpargne(idEpargne);
            System.out.println("Suppression de l'épargne avec ID: " + idEpargne + " terminée.");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

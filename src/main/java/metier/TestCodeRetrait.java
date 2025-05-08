package metier;

import java.sql.Connection;
import java.sql.SQLException;

import dao.CodeRetraitDAO;
import utilitaires.ConnectDB;




public class TestCodeRetrait {
    public static void main(String[] args) throws SQLException {
        Connection connection = ConnectDB.getInstance().getConnection();
        CodeRetraitDAO codeRetraitDAO = new CodeRetraitDAO(connection);

        try {
            String code = codeRetraitDAO.genererCodeRetrait(7, 1000); // Exemple avec idCompte 1 et montant 10 000 FCFA
            System.out.println("Code de retrait généré : " + code);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

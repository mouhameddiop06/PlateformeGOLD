
package dao;
public class UtilisateurDAOTest {
    public static void main(String[] args) {
        UtilisateurDAO dao = new UtilisateurDAO();
        int idUtilisateur = 3; // L'ID que vous essayez de supprimer

        try {
            boolean result = dao.supprimerUtilisateur(idUtilisateur);
            System.out.println("Résultat de la suppression: " + (result ? "Réussi" : "Échoué"));
        } catch (Exception e) {
            System.err.println("Exception lors du test de suppression: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
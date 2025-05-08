package metier;



import javax.mail.MessagingException;
import metier.PersonnelDAOUtil;

public class TestEmail {

    public static void main(String[] args) {
        // Adresse email du destinataire pour le test
        String emailDestinataire = "diopbirame8@gmail.com"; // Remplacez par une adresse email de test valide
        String numeroIdentificationClient = PersonnelDAOUtil.genererNumeroIdentificationClient();

        try {
            // Test d'envoi d'email avec la méthode envoyerEmailConfirmation
            System.out.println("Envoi d'email en cours...");
            PersonnelDAOUtil.envoyerEmailConfirmation(emailDestinataire, numeroIdentificationClient);
            System.out.println("Test réussi : Email envoyé avec succès !");
        } catch (MessagingException e) {
            System.out.println("Test échoué : Une erreur s'est produite lors de l'envoi de l'email.");
            e.printStackTrace();
        }
    }
}

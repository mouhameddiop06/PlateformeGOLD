package metier;

import java.security.SecureRandom;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class PersonnelDAOUtil {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    // Méthode pour générer un numéro d'identification client unique
    public static String genererNumeroIdentificationClient() {
        String prefixe = "CLI";
        long timestamp = System.currentTimeMillis();
        int random = secureRandom.nextInt(1000);
        return String.format("%s%d%03d", prefixe, timestamp, random);
    }

    // Méthode pour envoyer un email de confirmation au client après l'approbation
    
    public static void envoyerEmailConfirmation(String email, String numeroIdentificationClient) throws MessagingException {
        // Configuration des propriétés SMTP pour Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // Informations d'authentification Gmail
        final String username = "systemgold18@gmail.com";
        final String password = "kbyr bfay omdo cfde"; // Utilisez de préférence une variable d'environnement

        // Créer une session avec l'authentification Gmail
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // Activer le mode debug pour voir les détails de la connexion SMTP
        session.setDebug(true);

        // Préparer l'email
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject("Confirmation de création de votre compte GOLD");
        message.setText("Bonjour,\n\nVotre inscription a été approuvée avec succès ! \nVoici votre numéro d'identification client : "
                + numeroIdentificationClient
                + "\n\nVous pouvez maintenant vous connecter avec votre numero de telephone et votre code secret pour accéder à votre compte."
                + "\n\nCordialement,\nL'équipe de support");

        // Envoyer l'email
        Transport.send(message);
        System.out.println("Email envoyé avec succès à " + email);
    }


        // Méthode pour hacher le mot de passe (utilisez une bibliothèque comme BCrypt pour sécuriser cette étape)
        public static String hacherMotDePasse(String motDePasse) {
            // Implémentez avec une bibliothèque de hachage sécurisée (ex. BCrypt)
            // Exemple : return BCrypt.hashpw(motDePasse, BCrypt.gensalt());
            return motDePasse; // À remplacer par une vraie méthode de hachage
        }

        // Méthode pour vérifier un mot de passe haché
        public static boolean verifierMotDePasse(String motDePasse, String motDePasseHache) {
            // Implémentez avec une bibliothèque de vérification sécurisée (ex. BCrypt)
            // Exemple : return BCrypt.checkpw(motDePasse, motDePasseHache);
            return motDePasse.equals(motDePasseHache); // À remplacer par une vraie méthode de vérification
        } 
      }
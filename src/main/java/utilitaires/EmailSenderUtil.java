package utilitaires;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSenderUtil {
    public static void envoyerEmailConfirmation(String toEmail, String motDePasseTemporaire, String numeroIdentificationClient) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "localhost");
        props.put("mail.smtp.port", "1025");  // Port pour FakeSMTP

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("", "");
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("from@votredomaine.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Confirmation de création de compte");
        
        String contenuEmail = "Bonjour,\n\n"
                + "Votre compte a été créé avec succès.\n"
                + "Votre numéro d'identification client est : " + numeroIdentificationClient + "\n"
                + "Votre mot de passe temporaire est : " + motDePasseTemporaire + "\n\n"
                + "Veuillez changer votre mot de passe lors de votre première connexion.\n\n"
                + "Cordialement,\nVotre équipe de support";
        
        message.setText(contenuEmail);
        Transport.send(message);
    }
}
package utilitaires;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {
    private static final String USERNAME = "systemgold18@gmail.com";
    private static final String PASSWORD = "kbyr bfay omdo cfde";
    private static final Properties props;

    static {
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
    }

    private static Session getSession() {
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
    }

    public static void envoyerCodeResetEmail(String email, String nom, String prenom, String codeSecret) throws MessagingException {
        Session session = getSession();
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(USERNAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject("Réinitialisation de votre code secret GOLD");

        String htmlContent = String.format(
            "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>" +
            "<div style='background-color: #2563eb; padding: 20px; text-align: center; border-radius: 10px 10px 0 0;'>" +
            "<h1 style='color: white; margin: 0;'>GOLD</h1>" +
            "</div>" +
            "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 0 0 10px 10px;'>" +
            "<h2 style='color: #2563eb; margin-bottom: 20px;'>Réinitialisation du code secret</h2>" +
            "<p>Bonjour %s %s,</p>" +
            "<p>Vous avez demandé la réinitialisation de votre code secret.</p>" +
            "<div style='background-color: #e9ecef; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0;'>" +
            "<h3 style='margin: 0; color: #2563eb;'>Votre nouveau code secret est :</h3>" +
            "<p style='font-size: 24px; font-weight: bold; margin: 10px 0;'>%s</p>" +
            "</div>" +
            "<p style='color: #dc3545;'>Pour des raisons de sécurité, veuillez changer ce code dès votre prochaine connexion.</p>" +
            "<p style='color: #6c757d; font-size: 14px; margin-top: 30px;'>Si vous n'êtes pas à l'origine de cette demande, " +
            "veuillez contacter notre support immédiatement.</p>" +
            "</div>" +
            "<div style='text-align: center; margin-top: 20px; color: #6c757d;'>" +
            "<p>Cordialement,<br>L'équipe GOLD</p>" +
            "</div>" +
            "</div>",
            nom, prenom, codeSecret
        );

        message.setContent(htmlContent, "text/html; charset=utf-8");
        Transport.send(message);
    }
}
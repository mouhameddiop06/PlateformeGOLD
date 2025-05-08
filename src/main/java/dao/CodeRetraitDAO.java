package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Random;

public class CodeRetraitDAO {
    private Connection connection;

    public CodeRetraitDAO(Connection connection) {
        this.connection = connection;
    }

    // Méthode pour générer un code alphanumérique de 6 caractères
    private String generateWithdrawalCode() {
        int length = 6;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }

        return code.toString();
    }

    // Méthode pour générer un code de retrait
    public String genererCodeRetrait(int idCompteClient, double montant) throws SQLException {
        String verifyBalanceQuery = "SELECT solde FROM Compte WHERE id_compte = ?";
        String insertCodeRetraitQuery = "INSERT INTO CodeRetrait (code, idCompte, montant, dateExpiration, estUtilise) " +
                "VALUES (?, ?, ?, ?, FALSE)";

        connection.setAutoCommit(false); // Démarrer une transaction SQL
        try (PreparedStatement verifyBalanceStmt = connection.prepareStatement(verifyBalanceQuery)) {
            verifyBalanceStmt.setInt(1, idCompteClient);
            ResultSet rs = verifyBalanceStmt.executeQuery();

            if (rs.next()) {
                double solde = rs.getDouble("solde");

                if (solde >= montant) {
                    // Générer un code alphanumérique de 6 caractères
                    String code = generateWithdrawalCode();
                    LocalDateTime expiration = LocalDateTime.now().plusMinutes(15); // Code valable pour 15 minutes

                    try (PreparedStatement insertCodeRetraitStmt = connection.prepareStatement(insertCodeRetraitQuery)) {
                        insertCodeRetraitStmt.setString(1, code);
                        insertCodeRetraitStmt.setInt(2, idCompteClient);
                        insertCodeRetraitStmt.setBigDecimal(3, BigDecimal.valueOf(montant)); // Utilisation de BigDecimal pour le montant
                        insertCodeRetraitStmt.setTimestamp(4, java.sql.Timestamp.valueOf(expiration));
                        insertCodeRetraitStmt.executeUpdate();

                        connection.commit(); // Confirmer la transaction
                        return code;
                    }
                } else {
                    throw new SQLException("Solde insuffisant pour effectuer le retrait.");
                }
            } else {
                throw new SQLException("Compte client introuvable.");
            }
        } catch (SQLException e) {
            connection.rollback(); // Annuler la transaction en cas d'erreur
            throw e;
        } finally {
            connection.setAutoCommit(true); // Remettre en mode auto-commit
        }
    }
}

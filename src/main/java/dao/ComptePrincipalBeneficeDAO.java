package dao;

import metier.ComptePrincipalBenefice;
import utilitaires.ConnectDB;
import java.sql.*;

public class ComptePrincipalBeneficeDAO {
    // Suppression du champ connection car nous utiliserons ConnectDB
    
    public double getMontantBenefice() throws SQLException {
        double montant = 0.0;
        String sql = "SELECT montantBenefice FROM ComptePrincipalBenefice WHERE idBenefice = 1";
        
        try (PreparedStatement pstmt = ConnectDB.creerStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                montant = rs.getDouble("montantBenefice");
            }
        }
        return montant;
    }
}
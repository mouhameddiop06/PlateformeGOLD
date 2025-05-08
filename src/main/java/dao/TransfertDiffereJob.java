package dao;

import org.quartz.Job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransfertDiffereJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("dbConnection");
        
        try {
            // Mettre à jour les transferts différés dont la date d'exécution est passée
            String sql = "UPDATE TransfertDiffere SET statut = 'à_exécuter' WHERE dateExecution <= CURRENT_TIMESTAMP AND statut = 'en_attente'";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new JobExecutionException("Erreur lors de l'exécution des transferts différés", e);
        }
    }
}
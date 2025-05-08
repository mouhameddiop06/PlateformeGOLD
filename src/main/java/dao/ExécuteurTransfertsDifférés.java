package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import dao.TransfertDiffereDAO;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import utilitaires.ConnectDB;

public class ExécuteurTransfertsDifférés implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try (Connection connection = ConnectDB.getInstance().getConnection()) {
            TransfertDiffereDAO dao = new TransfertDiffereDAO(connection);
            exécuterTransfertsDifférés(dao);
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'exécution des transferts différés : " + e.getMessage());
            e.printStackTrace();
            throw new JobExecutionException(e);
        }
    }

    private void exécuterTransfertsDifférés(TransfertDiffereDAO dao) throws SQLException {
        List<Long> transfertsAExécuter = dao.getTransfertsDifferesAExecuter();
        for (Long idTransfertDifféré : transfertsAExécuter) {
            dao.executerTransfertDiffere(idTransfertDifféré);
        }
    }
}
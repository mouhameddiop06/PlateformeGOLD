package metier;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import dao.TransactionDAO;

public class TransfertAutomatiqueJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        int idTransaction = dataMap.getInt("idTransaction");
        double montant = dataMap.getDouble("montant");
        int compteSource = dataMap.getInt("compteSource");
        int compteDestinataire = dataMap.getInt("compteDestinataire");

        TransactionDAO transactionDAO = new TransactionDAO();
        try {
            transactionDAO.effectuerTransfertAutomatique(idTransaction, montant, compteSource, compteDestinataire);
        } catch (Exception e) {
            throw new JobExecutionException("Erreur lors de l'exécution du transfert automatique", e);
        }
    }
}
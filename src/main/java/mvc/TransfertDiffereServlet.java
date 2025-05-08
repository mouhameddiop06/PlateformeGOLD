package mvc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import dao.ExécuteurTransfertsDifférés;
import dao.TransfertDiffereDAO;
import utilitaires.ConnectDB;

@WebServlet("/transfertDiffere")
public class TransfertDiffereServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
        super.init();
        initialiserPlanificateurQuartz();
    }

    private void initialiserPlanificateurQuartz() {
        try {
            Scheduler scheduler = org.quartz.impl.StdSchedulerFactory.getDefaultScheduler();
            JobDetail job = JobBuilder.newJob(ExécuteurTransfertsDifférés.class)
                    .withIdentity("exécuteurTransfertsDifférés", "groupe1")
                    .build();
            scheduler.scheduleJob(job, TriggerBuilder.newTrigger()
                    .withIdentity("déclencheurTransfertsDifférés", "groupe1")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 * * ? * *")) // Exécuter toutes les minutes
                    .build());
            scheduler.start();
        } catch (SchedulerException e) {
            System.err.println("Erreur lors de l'initialisation du planificateur Quartz : " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Integer idClient = (Integer) session.getAttribute("idClient");

        if (idClient == null) {
            response.sendRedirect("connexion.jsp");
            return;
        }

        try (Connection connection = ConnectDB.getInstance().getConnection()) {
            TransfertDiffereDAO dao = new TransfertDiffereDAO(connection);

            if ("liste".equals(action)) {
                System.out.println("Récupération de la liste des transferts différés");
                JSONArray transferts = dao.getTransfertsDifferes();
                request.setAttribute("transferts", transferts);
                request.getRequestDispatcher("/WEB-INF/transfertsDifferes.jsp").forward(request, response);
            } else {
                int idCompte = (Integer) session.getAttribute("idCompte");
                JSONObject detailsCompte = dao.getDetailsCompte(idCompte);
                request.setAttribute("detailsCompte", detailsCompte);
                request.getRequestDispatcher("/WEB-INF/transfertDiffere.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL dans doGet : " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("erreur", "Une erreur est survenue lors de la récupération des données.");
            request.getRequestDispatcher("/WEB-INF/erreur.jsp").forward(request, response);
        }
    }

    
    
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Integer idClient = (Integer) session.getAttribute("idClient");

        if (idClient == null) {
            response.sendRedirect("connexion.jsp");
            return;
        }

        if ("programmer".equals(action)) {
            try (Connection connection = ConnectDB.getInstance().getConnection()) {
                TransfertDiffereDAO dao = new TransfertDiffereDAO(connection);

                int idCompteSource = (Integer) session.getAttribute("idCompte");
                String numeroDestinataire = request.getParameter("numeroDestinataire");
                double montant = Double.parseDouble(request.getParameter("montant"));
                LocalDateTime dateExecution = LocalDateTime.parse(request.getParameter("dateExecution"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                System.out.println("Programmation d'un transfert différé : " + idCompteSource + " -> " + numeroDestinataire + ", Montant: " + montant + ", Date: " + dateExecution);

                dao.programmerTransfertDiffere(idCompteSource, numeroDestinataire, montant, dateExecution);

                // Exécution des transferts différés en attente
                dao.executerTransfertsDifferes();

                JSONObject recu = new JSONObject();
                recu.put("montant", montant);
                recu.put("dateExecution", dateExecution.toString());
                recu.put("numeroDestinataire", numeroDestinataire);

                request.setAttribute("recu", recu);
                request.setAttribute("message", "Le transfert différé a été programmé avec succès.");
                request.getRequestDispatcher("/WEB-INF/confirmationTransfert.jsp").forward(request, response);

            } catch (SQLException e) {
                System.err.println("Erreur SQL dans doPost : " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("erreur", "Une erreur est survenue lors de la programmation du transfert différé.");
                request.getRequestDispatcher("/WEB-INF/erreur.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                System.err.println("Erreur de format de nombre : " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("erreur", "Le montant saisi n'est pas valide.");
                request.getRequestDispatcher("/WEB-INF/erreur.jsp").forward(request, response);
            }
        }
    }
}
package mvc;

import dao.TransactionDAO;

import metier.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/admin/visualiserTransactions")
public class VisualiserTransactionsServlet extends HttpServlet {
    private TransactionDAO transactionDAO;

    @Override
    public void init() throws ServletException {
        transactionDAO = new TransactionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Transaction> transactions = transactionDAO.getAllTransactions();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONArray transactionsArray = new JSONArray();
        
        for (Transaction transaction : transactions) {
            JSONObject transactionJson = new JSONObject();
            transactionJson.put("id", transaction.getId());
            transactionJson.put("montant", transaction.getMontant());
            transactionJson.put("date", transaction.getDate().toString());
            transactionJson.put("heure", transaction.getHeure().toString());
            transactionJson.put("statut", transaction.getStatut());
            transactionJson.put("telephoneSource", transaction.getTelephoneSource());
            transactionJson.put("telephoneDestinataire", transaction.getTelephoneDestinataire());
            transactionJson.put("typeTransaction", transaction.getTypeTransaction()); // Ajout de l'option
            transactionsArray.add(transactionJson);
        }

        PrintWriter out = response.getWriter();
        out.print(transactionsArray.toJSONString());
        out.flush();
    }
}

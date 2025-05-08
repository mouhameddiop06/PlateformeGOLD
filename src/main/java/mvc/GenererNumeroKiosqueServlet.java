package mvc;

import dao.KiosqueDAO;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/genererNumeroKiosque")
public class GenererNumeroKiosqueServlet extends HttpServlet {
    private KiosqueDAO kiosqueDAO;

    @Override
    public void init() throws ServletException {
        kiosqueDAO = new KiosqueDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Utiliser la méthode pour générer le numéro du kiosque
        String numeroIdentificationKiosque = kiosqueDAO.genererNumeroIdentificationKiosque();

        // Créer un objet JSON contenant le numéro généré
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("numeroIdentificationKiosque", numeroIdentificationKiosque);

        // Réponse JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(jsonResponse.toJSONString());
    }
}

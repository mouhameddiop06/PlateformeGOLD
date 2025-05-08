package mvc;

import dao.ResetCodeDAO;
import utilitaires.EmailService;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/ResetCodeServlet")
public class ResetCodeServlet extends HttpServlet {
    
    private ResetCodeDAO resetCodeDAO;
    
    @Override
    public void init() throws ServletException {
        resetCodeDAO = new ResetCodeDAO();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        
        try {
            String telephone = request.getParameter("telephone");
            
            // Logs détaillés du numéro reçu
            System.out.println("Numéro reçu: '" + telephone + "'");
            System.out.println("Longueur du numéro: " + (telephone != null ? telephone.length() : "null"));
            System.out.println("Caractères du numéro: ");
            if (telephone != null) {
                for (char c : telephone.toCharArray()) {
                    System.out.println("'" + c + "' - code ASCII: " + (int)c);
                }
            }
            
            // Nettoyage du numéro (enlever les espaces et caractères spéciaux)
            if (telephone != null) {
                telephone = telephone.trim().replaceAll("\\s+", "");
            }
            
            System.out.println("Numéro après nettoyage: '" + telephone + "'");
            
            // Vérification de la validité du numéro
            if (telephone == null || !telephone.matches("^[0-9]{9}$")) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Format de numéro invalide. Le numéro doit contenir 9 chiffres.");
                out.print(jsonResponse.toString());
                return;
            }
            
            // Test de connexion avec le numéro nettoyé
            resetCodeDAO.testConnexion(telephone);
            
            JSONObject userInfo = resetCodeDAO.verifierUtilisateur(telephone);
            
            if (userInfo != null) {
                String nouveauCode = resetCodeDAO.genererNouveauCode();
                
                if (resetCodeDAO.updateCodeSecret(telephone, nouveauCode)) {
                    try {
                        EmailService.envoyerCodeResetEmail(
                            userInfo.get("email").toString(),
                            userInfo.get("nom").toString(),
                            userInfo.get("prenom").toString(),
                            nouveauCode
                        );
                        
                        jsonResponse.put("success", true);
                        jsonResponse.put("message", "Un nouveau code secret a été envoyé à l'adresse " + userInfo.get("email"));
                    } catch (Exception e) {
                        jsonResponse.put("success", false);
                        jsonResponse.put("message", "Erreur lors de l'envoi de l'email: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Erreur lors de la mise à jour du code secret.");
                }
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Aucun utilisateur trouvé avec le numéro " + telephone);
            }
            
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Une erreur est survenue: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Réponse finale: " + jsonResponse.toString());
        out.print(jsonResponse.toString());
        out.flush();
    }
}
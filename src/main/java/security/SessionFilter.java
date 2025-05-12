package security;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/administrateur/*", "/personnel/*", "/agentkiosque/*"})
public class SessionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation du filtre si nécessaire
    } 

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        boolean isLoggedIn = (session != null && session.getAttribute("idUtilisateur") != null);
        String loginURI = httpRequest.getContextPath() + "/LoginServlet";
        boolean isLoginRequest = httpRequest.getRequestURI().equals(loginURI);

        if (isLoggedIn || isLoginRequest) {
            // L'utilisateur est connecté ou essaie de se connecter, continuez la chaîne de filtres
            chain.doFilter(request, response);
        } else {
            // L'utilisateur n'est pas connecté, redirigez-le vers la page de connexion
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/utilisateur/connexion.jsp");
        }
    }

    @Override
    public void destroy() {
        // Nettoyage des ressources si nécessaire
    }
}

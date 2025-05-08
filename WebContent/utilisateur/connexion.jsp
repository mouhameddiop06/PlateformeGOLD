<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion - GOLD</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css" rel="stylesheet">
    <style>
        body, html {
            height: 100%;
            margin: 0;
            font-family: 'Arial', sans-serif;
        }
        body { 
            background-color: #f5f5f5; /* Fond gris clair */
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .login-container {
            background-color: rgba(255, 255, 255, 0.9); /* Fond blanc légèrement transparent */
            border-radius: 15px;
            padding: 40px;
            box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
            max-width: 500px;
            width: 100%;
            border: 2px solid #FFD700; /* Bordure dorée */
        }
        .logo {
            text-align: center;
            margin-bottom: 20px;
        }
        .logo img {
            max-width: 150px;
            height: auto;
        }
        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 30px;
        }
        .welcome-message {
            text-align: center;
            font-size: 24px;
            color: #FF0000;
            margin-bottom: 30px;
            font-weight: bold;
            text-shadow: 1px 1px 2px rgba(0,0,0,0.1);
        }
        .form-group label {
            color: #333;
            font-weight: bold;
        }
        .form-control {
            border-radius: 20px;
            background-color: #ffffff;
            border: 1px solid #ced4da;
        }
        .btn-primary {
            background-color: #FF0000;
            border: none;
            border-radius: 20px;
            padding: 10px 20px;
            font-weight: bold;
            color: #ffffff;
        }
        .btn-primary:hover {
            background-color: #CC0000;
        }
        .alert-danger {
            background-color: #f8d7da;
            border-color: #f5c6cb;
            color: #721c24;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="logo">
            <img src="${pageContext.request.contextPath}/images/logo_og.png" alt="Logo">
        </div>
        <h1>Connexion</h1>
        <div class="welcome-message">BIENVENUE CHEZ GOLD</div>
        
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger">
                <c:choose>
                    <c:when test="${param.error == 'access_denied'}">
                        Vous devez être connecté pour accéder à cette page.
                    </c:when>
                    <c:when test="${param.error == 'invalid_credentials'}">
                        Email ou mot de passe incorrect. Veuillez réessayer.
                    </c:when>
                    <c:otherwise>
                        Une erreur inconnue est survenue. Veuillez réessayer.
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/LoginServlet" method="post">
            <div class="form-group">
                <label for="email"><i class="fas fa-envelope"></i> Email</label>
                <input type="email" class="form-control" id="email" name="email" required>
            </div>
            <div class="form-group">
                <label for="motdepasse"><i class="fas fa-lock"></i> Mot de passe</label>
                <input type="password" class="form-control" id="motdepasse" name="motdepasse" required>
            </div>
            <button type="submit" class="btn btn-primary btn-block">Se connecter</button>
        </form>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.3/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>
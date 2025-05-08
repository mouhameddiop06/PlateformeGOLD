<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription - GOLD</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --primary-color: #2563eb;
            --secondary-color: #1e40af;
            --accent-color: #3b82f6;
        }

        body {
            font-family: 'Inter', sans-serif;
            background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
            min-height: 100vh;
            padding: 3rem 0;
            position: relative;
            padding-bottom: 6rem;
        }

        .registration-container {
            background: white;
            border-radius: 1rem;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
            padding: 2.5rem;
            margin-bottom: 2rem;
            animation: fadeIn 0.5s ease;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .registration-header {
            text-align: center;
            margin-bottom: 2.5rem;
        }

        .registration-header .logo {
            font-size: 2rem;
            color: var(--primary-color);
            margin-bottom: 1rem;
        }

        .form-label {
            font-weight: 500;
            color: #4b5563;
            margin-bottom: 0.5rem;
            display: flex;
            align-items: center;
        }

        .form-label i {
            margin-right: 0.5rem;
            color: var(--primary-color);
        }

        .form-control {
            padding: 0.75rem 1rem;
            border-radius: 0.5rem;
            border: 1px solid #e5e7eb;
            transition: all 0.3s ease;
        }

        .form-control:focus {
            border-color: var(--accent-color);
            box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
        }

        .form-control.is-invalid {
            border-color: #dc2626;
            background-image: none;
        }

        .password-wrapper {
            position: relative;
        }

        .toggle-password {
            position: absolute;
            right: 1rem;
            top: 50%;
            transform: translateY(-50%);
            cursor: pointer;
            color: #6b7280;
            background: none;
            border: none;
            padding: 0;
        }

        .btn-register {
            background: var(--primary-color);
            color: white;
            padding: 1rem;
            border-radius: 0.5rem;
            font-weight: 600;
            transition: all 0.3s ease;
        }

        .btn-register:hover {
            background: var(--secondary-color);
            transform: translateY(-2px);
        }

        .error-message {
            color: #dc2626;
            font-size: 0.875rem;
            margin-top: 0.25rem;
            display: none;
        }

        .form-section {
            background: #f8fafc;
            padding: 1.5rem;
            border-radius: 0.5rem;
            margin-bottom: 1.5rem;
        }

        .form-section-title {
            color: var(--primary-color);
            font-weight: 600;
            margin-bottom: 1rem;
            font-size: 1.1rem;
        }

        .footer {
            background: white;
            padding: 1.5rem 0;
            position: absolute;
            bottom: 0;
            width: 100%;
            box-shadow: 0 -4px 6px -1px rgba(0, 0, 0, 0.1);
        }

        .social-links {
            display: flex;
            justify-content: center;
            gap: 1rem;
            margin-top: 1rem;
        }

        .social-link {
            color: var(--primary-color);
            font-size: 1.5rem;
            transition: transform 0.3s ease;
        }

        .social-link:hover {
            transform: translateY(-2px);
            color: var(--secondary-color);
        }

        .progress-container {
            margin-bottom: 2rem;
        }

        .progress-steps {
            display: flex;
            justify-content: space-between;
            margin-bottom: 1rem;
        }

        .step {
            flex: 1;
            text-align: center;
            position: relative;
        }

        .step::after {
            content: '';
            position: absolute;
            width: 100%;
            height: 2px;
            background: #e5e7eb;
            top: 50%;
            left: 50%;
            z-index: 0;
        }

        .step:last-child::after {
            display: none;
        }

        .step-circle {
            width: 2rem;
            height: 2rem;
            background: white;
            border: 2px solid var(--primary-color);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
            z-index: 1;
            margin: 0 auto 0.5rem;
        }

        .step.active .step-circle {
            background: var(--primary-color);
            color: white;
        }

        .step-label {
            font-size: 0.875rem;
            color: #6b7280;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="registration-container">
                    <div class="registration-header">
                        <div class="logo">
                            <i class="fas fa-exchange-alt"></i>
                        </div>
                        <h2>Créez votre compte GOLD</h2>
                        <p class="text-muted">Commencez à effectuer des transferts d'argent en toute sécurité</p>
                    </div>

                    <form id="registrationForm" action="${pageContext.request.contextPath}/InscriptionClientServlet" method="post" class="needs-validation" novalidate>
                        <div class="form-section">
                            <div class="form-section-title">
                                <i class="fas fa-user me-2"></i>Informations personnelles
                            </div>
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">
                                        <i class="fas fa-user"></i>Nom
                                    </label>
                                    <input type="text" class="form-control" id="nom" name="nom" required>
                                    <div class="error-message">Veuillez entrer votre nom</div>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label class="form-label">
                                        <i class="fas fa-user"></i>Prénom
                                    </label>
                                    <input type="text" class="form-control" id="prenom" name="prenom" required>
                                    <div class="error-message">Veuillez entrer votre prénom</div>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">
                                    <i class="fas fa-envelope"></i>Email
                                </label>
                                <input type="email" class="form-control" id="email" name="email" required>
                                <div class="error-message">Veuillez entrer une adresse email valide</div>
                            </div>
                        </div>

                        <div class="form-section">
                            <div class="form-section-title">
                                <i class="fas fa-phone me-2"></i>Contact et localisation
                            </div>

                            <div class="mb-3">
                                <label class="form-label">
                                    <i class="fas fa-phone"></i>Téléphone
                                </label>
                                <input type="tel" class="form-control" id="telephone" name="telephone" 
                                       required pattern="[0-9]{9}" maxlength="9">
                                <div class="error-message">Le numéro doit comporter 9 chiffres</div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">
                                    <i class="fas fa-map-marker-alt"></i>Adresse
                                </label>
                                <input type="text" class="form-control" id="adresse" name="adresse" required>
                                <div class="error-message">Veuillez entrer votre adresse</div>
                            </div>
                        </div>

                        <div class="form-section">
                            <div class="form-section-title">
                                <i class="fas fa-lock me-2"></i>Sécurité
                            </div>

                            <div class="mb-4">
                                <label class="form-label">
                                    <i class="fas fa-key"></i>Code Secret (4 chiffres)
                                </label>
                                <div class="password-wrapper">
                                    <input type="password" class="form-control" id="codeSecret" name="codeSecret" 
                                           required pattern="\d{4}" maxlength="4">
                                    <button type="button" class="toggle-password" onclick="togglePassword()">
                                        <i class="far fa-eye" id="toggleIcon"></i>
                                    </button>
                                </div>
                                <div class="error-message">Le code secret doit contenir 4 chiffres</div>
                            </div>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-register">
                                <i class="fas fa-user-plus me-2"></i>Créer mon compte
                            </button>
                        </div>

                        <p class="text-center mt-3">
                            Déjà inscrit ? <a href="connexion.jsp" class="text-primary">Connectez-vous</a>
                        </p>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <footer class="footer">
        <div class="container">
            <div class="text-center">
                <p class="mb-2">&copy; 2024 GOLD. Tous droits réservés.</p>
                <div class="social-links">
                    <a href="#" class="social-link"><i class="fab fa-facebook"></i></a>
                    <a href="#" class="social-link"><i class="fab fa-twitter"></i></a>
                    <a href="#" class="social-link"><i class="fab fa-instagram"></i></a>
                    <a href="#" class="social-link"><i class="fab fa-linkedin"></i></a>
                </div>
            </div>
        </div>
    </footer>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
    <script>
        // Afficher/Masquer le code secret
        function togglePassword() {
            const passwordInput = document.getElementById('codeSecret');
            const toggleIcon = document.getElementById('toggleIcon');
            
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                toggleIcon.className = 'far fa-eye-slash';
            } else {
                passwordInput.type = 'password';
                toggleIcon.className = 'far fa-eye';
            }
        }

        // Validation du formulaire
        (function () {
            'use strict'
            const form = document.getElementById('registrationForm');
            
            form.addEventListener('submit', function (event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                    
                    // Afficher les messages d'erreur personnalisés
                    const inputs = form.querySelectorAll('input');
                    inputs.forEach(input => {
                        if (!input.validity.valid) {
                            input.nextElementSibling.style.display = 'block';
                        } else {
                            input.nextElementSibling.style.display = 'none';
                        }
                    });
                }
                
                form.classList.add('was-validated');
            }, false);

            // Validation en temps réel
            const inputs = form.querySelectorAll('input');
            inputs.forEach(input => {
                input.addEventListener('input', function() {
                    if (!this.validity.valid) {
                        this.nextElementSibling.style.display = 'block';
                    } else {
                        this.nextElementSibling.style.display = 'none';
                    }
                });
            });
        })();
    </script>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion - GOLD</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <!-- SweetAlert2 -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    
    <style>
        body {
            background-color: #f0f9ff;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .connexion-card {
            background: white;
            border-radius: 15px;
            padding: 2.5rem;
            width: 100%;
            max-width: 450px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
        }

        .logo-icon {
            font-size: 24px;
            color: #2563eb;
            margin-bottom: 1rem;
        }

        .title {
            text-align: center;
            margin-bottom: 2rem;
        }

        .title h2 {
            font-size: 1.5rem;
            margin-bottom: 0.5rem;
            color: #1f2937;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-label {
            display: flex;
            align-items: center;
            color: #4b5563;
            margin-bottom: 0.5rem;
            font-size: 0.95rem;
        }

        .form-label i {
            margin-right: 0.5rem;
        }

        .form-control {
            border: 1px solid #e5e7eb;
            border-radius: 8px;
            padding: 0.75rem 1rem;
        }

        .form-control:focus {
            border-color: #2563eb;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
        }

        .password-field {
            position: relative;
        }

        .password-toggle {
            position: absolute;
            right: 1rem;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            color: #6b7280;
            cursor: pointer;
        }

        .forgot-link {
            display: block;
            text-align: right;
            color: #2563eb;
            text-decoration: none;
            font-size: 0.9rem;
            margin-bottom: 1.5rem;
        }

        .btn-connect {
            width: 100%;
            background: #2563eb;
            color: white;
            border: none;
            padding: 0.75rem;
            border-radius: 8px;
            font-weight: 500;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
        }

        .btn-connect:hover {
            background: #1d4ed8;
        }

        .signup-text {
            text-align: center;
            margin-top: 1.5rem;
            color: #6b7280;
        }

        .signup-text a {
            color: #2563eb;
            text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="connexion-card">
        <div class="title">
            <div class="text-center">
                <i class="fas fa-exchange-alt logo-icon"></i>
            </div>
            <h2>Connexion à GOLD</h2>
            <p>Accédez à votre compte en toute sécurité</p>
        </div>

        <form action="${pageContext.request.contextPath}/LoginClientServlet" method="post" id="loginForm">
            <div class="form-group">
                <label class="form-label">
                    <i class="fas fa-phone"></i>
                    Numéro de téléphone
                </label>
                <input type="tel" 
                       class="form-control" 
                       name="telephone" 
                       placeholder="Entrez votre numéro"
                       required 
                       pattern="[0-9]{9}" 
                       maxlength="9">
            </div>

            <div class="form-group">
                <label class="form-label">
                    <i class="fas fa-lock"></i>
                    Code secret
                </label>
                <div class="password-field">
                    <input type="password" 
                           class="form-control" 
                           name="codeSecret" 
                           placeholder="Entrez votre code"
                           required 
                           maxlength="4" 
                           pattern="\d{4}">
                    <button type="button" class="password-toggle" id="togglePassword">
                        <i class="far fa-eye"></i>
                    </button>
                </div>
            </div>

            <a href="#" class="forgot-link" id="forgotLink">Code secret oublié ?</a>

            <button type="submit" class="btn-connect">
                <i class="fas fa-sign-in-alt"></i>
                Se connecter
            </button>

            <div class="signup-text">
                Pas encore de compte ? <a href="inscription.jsp">Créer un compte</a>
            </div>
        </form>
    </div>

    <!-- Modal -->
    <div class="modal fade" id="resetModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Réinitialisation du code secret</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="form-label">Numéro de téléphone</label>
                        <input type="tel" 
                               class="form-control" 
                               id="resetTelephone"
                               placeholder="Entrez votre numéro" 
                               maxlength="9">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                    <button type="button" class="btn btn-primary" id="sendResetButton">Envoyer</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap Bundle avec Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Initialisation du modal
            let resetModal = new bootstrap.Modal(document.getElementById('resetModal'));
            
            // Gestionnaire pour le lien "Code secret oublié"
            document.getElementById('forgotLink').addEventListener('click', function(e) {
                e.preventDefault();
                resetModal.show();
            });

            // Gestionnaire pour le toggle du mot de passe
            document.getElementById('togglePassword').addEventListener('click', function() {
                const passwordInput = document.querySelector('input[name="codeSecret"]');
                const icon = this.querySelector('i');
                
                if (passwordInput.type === 'password') {
                    passwordInput.type = 'text';
                    icon.className = 'far fa-eye-slash';
                } else {
                    passwordInput.type = 'password';
                    icon.className = 'far fa-eye';
                }
            });

            // Gestionnaire pour le bouton d'envoi de réinitialisation
            document.getElementById('sendResetButton').addEventListener('click', function() {
                const telephone = document.getElementById('resetTelephone').value;
                
                if (!telephone.match(/^[0-9]{9}$/)) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Erreur',
                        text: 'Veuillez entrer un numéro de téléphone valide (9 chiffres)',
                        confirmButtonColor: '#2563eb'
                    });
                    return;
                }

                this.disabled = true;
                this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Envoi en cours...';
             // Modifions la partie fetch dans le script :
                fetch('${pageContext.request.contextPath}/ResetCodeServlet', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `telephone=${telephone}`
                })
                .then(response => response.json())
                .then(data => {
                    resetModal.hide();
                    if (data.success) {
                        Swal.fire({
                            icon: 'success',
                            title: 'Succès',
                            text: 'Un nouveau code secret a été envoyé à votre adresse email.',
                            confirmButtonColor: '#2563eb'
                        });
                    } else {
                        throw new Error(data.message || 'Une erreur est survenue');
                    }
                })
                .catch(error => {
                    Swal.fire({
                        icon: 'error',
                        title: 'Erreur',
                        text: error.message || 'Une erreur est survenue lors de la réinitialisation',
                        confirmButtonColor: '#2563eb'
                    });
                })
                .finally(() => {
                    this.disabled = false;
                    this.innerHTML = 'Envoyer';
                    document.getElementById('resetTelephone').value = '';
                });
            });

            // Validation du formulaire de connexion
            document.getElementById('loginForm').addEventListener('submit', function(e) {
                const telephone = this.querySelector('input[name="telephone"]').value;
                const codeSecret = this.querySelector('input[name="codeSecret"]').value;

                if (!telephone.match(/^[0-9]{9}$/)) {
                    e.preventDefault();
                    Swal.fire({
                        icon: 'error',
                        title: 'Erreur',
                        text: 'Numéro de téléphone invalide',
                        confirmButtonColor: '#2563eb'
                    });
                } else if (!codeSecret.match(/^[0-9]{4}$/)) {
                    e.preventDefault();
                    Swal.fire({
                        icon: 'error',
                        title: 'Erreur',
                        text: 'Le code secret doit contenir 4 chiffres',
                        confirmButtonColor: '#2563eb'
                    });
                }
            });
        });
    </script>
</body>
</html>
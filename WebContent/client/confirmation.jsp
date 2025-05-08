<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription en attente - GOLD</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --primary-color: #2563eb;
            --secondary-color: #1e40af;
            --success-color: #10b981;
        }

        body {
            font-family: 'Inter', sans-serif;
            background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            padding: 2rem 0;
        }

        .success-container {
            background: white;
            border-radius: 1rem;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
            padding: 3rem 2rem;
            text-align: center;
            animation: slideUp 0.5s ease-out;
        }

        @keyframes slideUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .success-icon {
            width: 100px;
            height: 100px;
            background: #f0fdf4;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 2rem;
            animation: pulse 2s infinite;
        }

        @keyframes pulse {
            0% {
                box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.4);
            }
            70% {
                box-shadow: 0 0 0 20px rgba(16, 185, 129, 0);
            }
            100% {
                box-shadow: 0 0 0 0 rgba(16, 185, 129, 0);
            }
        }

        .success-icon i {
            font-size: 3rem;
            color: var(--success-color);
        }

        .success-title {
            color: #1f2937;
            font-size: 1.75rem;
            font-weight: 600;
            margin-bottom: 1rem;
        }

        .success-message {
            color: #6b7280;
            font-size: 1.1rem;
            margin-bottom: 2rem;
            line-height: 1.6;
        }

        .steps-container {
            background: #f9fafb;
            border-radius: 0.75rem;
            padding: 1.5rem;
            margin: 2rem 0;
            text-align: left;
        }

        .step {
            display: flex;
            align-items: center;
            margin-bottom: 1rem;
        }

        .step:last-child {
            margin-bottom: 0;
        }

        .step-number {
            width: 28px;
            height: 28px;
            background: var(--primary-color);
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            margin-right: 1rem;
            flex-shrink: 0;
        }

        .step-text {
            color: #4b5563;
            font-size: 1rem;
        }

        .btn-home {
            background: var(--primary-color);
            color: white;
            padding: 0.75rem 2rem;
            border-radius: 0.5rem;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s ease;
            display: inline-flex;
            align-items: center;
        }

        .btn-home:hover {
            background: var(--secondary-color);
            color: white;
            transform: translateY(-2px);
        }

        .contact-info {
            margin-top: 2rem;
            font-size: 0.95rem;
            color: #6b7280;
        }

        .contact-info a {
            color: var(--primary-color);
            text-decoration: none;
        }

        .contact-info a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                <div class="success-container">
                    <div class="success-icon">
                        <i class="fas fa-clock"></i>
                    </div>

                    <h1 class="success-title">
                        Inscription en cours de validation
                    </h1>

                    <p class="success-message">
                        Merci de votre inscription à GOLD ! Votre demande a été reçue et est actuellement en cours d'examen par notre équipe.
                    </p>

                    <div class="steps-container">
                        <div class="step">
                            <div class="step-number">1</div>
                            <div class="step-text">Vérification des informations fournies</div>
                        </div>
                        <div class="step">
                            <div class="step-number">2</div>
                            <div class="step-text">Validation par notre équipe de sécurité</div>
                        </div>
                        <div class="step">
                            <div class="step-number">3</div>
                            <div class="step-text">Activation de votre compte</div>
                        </div>
                    </div>

                    <p class="success-message">
                        Vous recevrez un e-mail de confirmation dès que votre compte sera activé.
                    </p>

                    <a href="index.jsp" class="btn btn-home">
                        <i class="fas fa-home me-2"></i>
                        Retour à l'accueil
                    </a>

                    <div class="contact-info">
                        Une question ? Contactez notre support au 
                        <a href="tel:+221777231600">+221777231600</a>
                        ou par email à 
                        <a href="mailto:support@transferpro.com">support@gold.com</a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
</body>
</html>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accueil - Plateforme de Transfert d'Argent</title>
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
            background-color: #f8fafc;
        }

        .navbar {
            background-color: white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            padding: 1rem 0;
        }

        .navbar-brand {
            color: var(--primary-color);
            font-weight: 600;
            font-size: 1.5rem;
        }

        .hero-section {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            padding: 6rem 0;
            position: relative;
            overflow: hidden;
        }

        .hero-section::before {
            content: '';
            position: absolute;
            width: 100%;
            height: 100%;
            top: 0;
            left: 0;
            background: url('/api/placeholder/1920/1080') center/cover;
            opacity: 0.1;
        }

        .hero-content {
            position: relative;
            z-index: 1;
            color: white;
        }

        .display-4 {
            font-weight: 700;
            margin-bottom: 1.5rem;
        }

        .lead {
            font-size: 1.25rem;
            margin-bottom: 2rem;
        }

        .btn-custom {
            padding: 0.8rem 2rem;
            border-radius: 50px;
            font-weight: 600;
            transition: all 0.3s ease;
        }

        .btn-primary {
            background-color: white;
            color: var(--primary-color);
            border: none;
        }

        .btn-primary:hover {
            background-color: var(--accent-color);
            color: white;
            transform: translateY(-2px);
        }

        .btn-outline-light:hover {
            background-color: rgba(255,255,255,0.1);
            transform: translateY(-2px);
        }

        .features-section {
            padding: 5rem 0;
            background-color: white;
        }

        .feature-card {
            padding: 2rem;
            text-align: center;
            border-radius: 1rem;
            box-shadow: 0 4px 6px rgba(0,0,0,0.05);
            transition: transform 0.3s ease;
        }

        .feature-card:hover {
            transform: translateY(-10px);
        }

        .feature-icon {
            font-size: 2.5rem;
            color: var(--primary-color);
            margin-bottom: 1.5rem;
        }

        footer {
            background-color: #1f2937;
            color: white;
            padding: 3rem 0;
        }

        .footer-icon {
            margin: 0 15px;
            color: white;
            font-size: 1.5rem;
            transition: color 0.3s ease;
        }

        .footer-icon:hover {
            color: var(--accent-color);
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg sticky-top">
        <div class="container">
            <a class="navbar-brand" href="#">
                <i class="fas fa-exchange-alt me-2"></i>
                GOLD
            </a>
        </div>
    </nav>

    <section class="hero-section">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-lg-8 text-center hero-content">
                    <h1 class="display-4">Transférez votre argent en toute simplicité</h1>
                    <p class="lead">Une solution rapide, sécurisée et économique pour vos transferts d'argent </p>
                    <div class="d-flex justify-content-center gap-3">
                        <a href="connexion.jsp" class="btn btn-primary btn-custom">
                            <i class="fas fa-sign-in-alt me-2"></i>Connexion
                        </a>
                        <a href="inscription.jsp" class="btn btn-outline-light btn-custom">
                            <i class="fas fa-user-plus me-2"></i>Inscription
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section class="features-section">
        <div class="container">
            <div class="row g-4">
                <div class="col-md-4">
                    <div class="feature-card">
                        <i class="fas fa-shield-alt feature-icon"></i>
                        <h3>Sécurité Maximale</h3>
                        <p>Vos transactions sont protégées par un système transparent et avancé</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <i class="fas fa-bolt feature-icon"></i>
                        <h3>Transferts Instantanés</h3>
                        <p>Envoyez de l'argent en quelques secondes partout dans le monde</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <i class="fas fa-percentage feature-icon"></i>
                        <h3>Frais Réduits</h3>
                        <p>Profitez des meilleurs taux et des frais les plus bas du marché</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <footer>
        <div class="container">
            <div class="text-center">
                <div class="mb-4">
                    <a href="#" class="footer-icon"><i class="fab fa-facebook"></i></a>
                    <a href="#" class="footer-icon"><i class="fab fa-twitter"></i></a>
                    <a href="#" class="footer-icon"><i class="fab fa-instagram"></i></a>
                    <a href="#" class="footer-icon"><i class="fab fa-linkedin"></i></a>
                </div>
                <p class="mb-0">&copy; 2024 GOLD. Tous droits réservés.</p>
            </div>
        </div>
    </footer>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="metier.Utilisateur" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%!
    // Fonction utilitaire pour formater la date pour le nom de fichier
    public String formatDateForFilename(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
        return sdf.format(date);
    }
%>
<%
    // Vérification de la session
    Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
    if (session == null || utilisateur == null || !"Admin".equals(session.getAttribute("role"))) {
        response.sendRedirect("../utilisateur/connexion.jsp?error=session_expired");
        return;
    }
    
    // Récupération du montant des bénéfices avec valeur par défaut
    Double montantBenefice = (Double) request.getAttribute("montantBenefice");
    if (montantBenefice == null) {
        response.sendRedirect(request.getContextPath() + "/admin/gestion-service");
        return;
    }
    
    // Format de la date pour l'affichage
    Date currentDate = new Date();
    String dateMAJ = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(currentDate);
    String dateForFilename = formatDateForFilename(currentDate);
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Services - Gold</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        body { 
            background-color: #f8f9fa;
            min-height: 100vh;
        }
        .benefit-card {
            background: linear-gradient(135deg, #FFD700, #FFA500);
            border: none;
            border-radius: 15px;
            box-shadow: 0 10px 20px rgba(0,0,0,0.2);
            transition: transform 0.3s ease;
        }
        .benefit-card:hover {
            transform: translateY(-5px);
        }
        .amount-display {
            font-size: 2.5rem;
            font-weight: bold;
            color: #fff;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
        }
        .stat-icon {
            font-size: 4rem;
            color: rgba(255,255,255,0.8);
        }
        .card-footer {
            background: rgba(255,255,255,0.1);
            border-top: 1px solid rgba(255,255,255,0.2);
        }
        .last-update {
            color: rgba(255,255,255,0.8);
            font-size: 0.9rem;
        }
        @media print {
            .no-print {
                display: none !important;
            }
            .benefit-card {
                box-shadow: none;
            }
            body {
                background-color: white;
            }
        }
    </style>
</head>
<body>

<!-- Barre de navigation -->
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">
            <i class="fas fa-coins me-2"></i>Gold Services
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <span class="nav-link disabled">
                        <i class="fas fa-user me-1"></i>
                        <%= utilisateur.getPrenom() %> <%= utilisateur.getNom() %>
                    </span>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/LogoutServlet">
                        <i class="fas fa-sign-out-alt me-1"></i>Déconnexion
                    </a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container py-5">
    <!-- Fil d'Ariane -->
    <nav aria-label="breadcrumb" class="mb-4">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/administrateur/dashboard_administrateur.jsp">Retour Dashboard Admininistrateur</a></li>
            <li class="breadcrumb-item active">Gestion des Services</li>
        </ol>
    </nav>

    <h1 class="text-center mb-5">
        <i class="fas fa-chart-line me-3"></i>
        Gestion des Services
    </h1>

    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card benefit-card">
                <div class="card-body text-center p-5">
                    <div class="mb-4">
                        <i class="fas fa-money-bill-wave stat-icon"></i>
                    </div>
                    <h3 class="text-white mb-4">Solde Total des Bénéfices</h3>
                    <div class="amount-display mb-3" id="montantBenefice" 
                         data-montant="<%= String.format("%,d", Math.round(montantBenefice.doubleValue())) %>"
                         data-filename="benefices_<%= dateForFilename %>">
                        <%= String.format("%,d", Math.round(montantBenefice.doubleValue())) %> FCFA
                    </div>
                    <div class="mt-4 no-print">
                        <button class="btn btn-light btn-lg me-2" onclick="exporterDonnees()">
                            <i class="fas fa-download me-2"></i>Exporter
                        </button>
                        <button class="btn btn-light btn-lg" onclick="window.print()">
                            <i class="fas fa-print me-2"></i>Imprimer
                        </button>
                    </div>
                </div>
                <div class="card-footer text-center py-3">
                    <span class="last-update" id="lastUpdate" data-date="<%= dateMAJ %>">
                        <i class="fas fa-clock me-1"></i>
                        Dernière mise à jour : <%= dateMAJ %>
                    </span>
                </div>
            </div>
        </div>
    </div>

    <!-- Boutons de navigation -->
    <div class="row mt-5">
        <div class="col-12 text-center">
            <a href="${pageContext.request.contextPath}/administrateur/dashboard_administrateur.jsp" class="btn btn-secondary btn-lg me-2">
                <i class="fas fa-arrow-left me-2"></i>Retour au Dashboard
            </a>
            <button class="btn btn-primary btn-lg" onclick="window.location.reload()">
                <i class="fas fa-sync-alt me-2"></i>Actualiser
            </button>
        </div>
    </div>
</div>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Animation du montant
    document.addEventListener('DOMContentLoaded', function() {
        const amountDisplay = document.querySelector('.amount-display');
        amountDisplay.style.opacity = '0';
        setTimeout(() => {
            amountDisplay.style.transition = 'opacity 1s ease-in-out';
            amountDisplay.style.opacity = '1';
        }, 300);
    });

    // Fonction pour exporter les données en CSV
    function exporterDonnees() {
        const montantElement = document.getElementById('montantBenefice');
        const lastUpdateElement = document.getElementById('lastUpdate');
        
        const montant = montantElement.dataset.montant + ' FCFA';
        const dateMAJ = lastUpdateElement.dataset.date;
        const filename = montantElement.dataset.filename;
        
        // Création des données CSV
        const csvContent = [
            ['Date', 'Montant des Bénéfices'],
            [dateMAJ, montant]
        ].map(row => row.join(',')).join('\n');

        // Création du Blob et du lien de téléchargement
        const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        
        // Configuration du lien de téléchargement
        link.setAttribute('href', url);
        link.setAttribute('download', `${filename}.csv`);
        document.body.appendChild(link);
        
        // Déclenchement du téléchargement
        link.click();
        
        // Nettoyage
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
    }
</script>

</body>
</html>
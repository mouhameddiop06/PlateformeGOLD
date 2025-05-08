<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="metier.Utilisateur" %>
<%
    // Gestion de session
   // HttpSession session = request.getSession(false);
    Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

    // Vérifier si l'utilisateur est connecté et a le rôle Admin
    if (session == null || utilisateur == null || !"Admin".equals(session.getAttribute("role"))) {
        response.sendRedirect("utilisateur/connexion.jsp?error=session_expired");
        return;
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tableau de bord Administrateur</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        body { background-color: #f8f9fa; }
        .card { box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15); transition: all 0.3s; }
        .card:hover { transform: translateY(-5px); }
        .stat-icon { font-size: 2.5rem; margin-bottom: 0.5rem; }
        .stat-badge { position: absolute; top: -5px; right: -5px; font-size: 0.8rem; }
        .chart-container { position: relative; height: 300px; width: 100%; }
    </style>
</head>
<body>

<!-- Barre de navigation -->
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">Dashboard Admin</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <span class="nav-link disabled">Bienvenue, <strong><%= utilisateur.getPrenom() %> <%= utilisateur.getNom() %></strong></span>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/gold/LogoutServlet">Déconnexion</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container-fluid py-4">
    <h1 class="text-center mb-4">Tableau de bord Administrateur</h1>

    <!-- Cartes de statistiques -->
    <div class="row g-4 mb-4">
        <div class="col-md-4">
            <div class="card h-100">
                <div class="card-body">
                    <h5 class="card-title text-primary">Statistiques des Utilisateurs</h5>
                    <div class="row">
                        <div class="col-4 text-center position-relative">
                            <i class="fas fa-user-tie stat-icon text-primary"></i>
                            <span class="badge bg-danger stat-badge" id="nbPersonnels">...</span>
                            <p class="mb-0">Personnel</p>
                        </div>
                        <div class="col-4 text-center position-relative">
                            <i class="fas fa-store stat-icon text-success"></i>
                            <span class="badge bg-danger stat-badge" id="nbAgentsKiosque">...</span>
                            <p class="mb-0">Agent Kiosque</p>
                        </div>
                        <div class="col-4 text-center position-relative">
                            <i class="fas fa-users stat-icon text-info"></i>
                            <span class="badge bg-danger stat-badge" id="nbClients">...</span>
                            <p class="mb-0">Client</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Statistiques Kiosques -->
        <div class="col-md-4">
            <div class="card h-100">
                <div class="card-body">
                    <h5 class="card-title text-success">Statistiques des Kiosques</h5>
                    <div class="row">
                        <div class="col-6 text-center position-relative">
                            <i class="fas fa-store-alt stat-icon text-success"></i>
                            <span class="badge bg-danger stat-badge" id="totalKiosquesActifs">...</span>
                            <p class="mb-0">Kiosques Actifs</p>
                        </div>
                        <div class="col-6 text-center position-relative">
                            <i class="fas fa-store-alt-slash stat-icon text-secondary"></i>
                            <span class="badge bg-danger stat-badge" id="totalKiosquesInactifs">...</span>
                            <p class="mb-0">Kiosques Inactifs</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Statistiques Transactions -->
        <div class="col-md-4">
            <div class="card h-100">
                <div class="card-body">
                    <h5 class="card-title text-info">Statistiques des Transactions</h5>
                    <div class="row">
                        <div class="col-6 text-center">
                            <i class="fas fa-money-bill-wave stat-icon text-info"></i>
                            <p class="mb-0">Total Transactions</p>
                            <h4 id="totalTransactions">...</h4>
                        </div>
                        <div class="col-6 text-center">
                            <i class="fas fa-chart-line stat-icon text-warning"></i>
                            <p class="mb-0">Aujourd'hui</p>
                            <h4 id="transactionsAujourdhui">...</h4>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Graphiques -->
    <div class="row g-4">
        <!-- Graphique des utilisateurs -->
        <div class="col-md-6">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Répartition des Utilisateurs</h5>
                    <div class="chart-container">
                        <canvas id="userChart"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- Graphique des transactions -->
        <div class="col-md-6">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Évolution des Transactions</h5>
                    <div class="chart-container">
                        <canvas id="transactionChart"></canvas>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Liens vers les autres pages -->
    <div class="row mt-4">
        <div class="col-12 text-center">
            <a href="gestion_utilisateur.jsp" class="btn btn-primary m-2">Gérer les utilisateurs</a>
            <a href="gestion_kiosque.jsp" class="btn btn-success m-2">Gérer les kiosques</a>
            <a href="Visualiser_transactions.jsp" class="btn btn-info m-2">Voir les transactions</a>
            <a href="gestion_service.jsp" class="btn btn-warning m-2">Gérer les services</a>
        </div>
    </div>
</div>

<script>
$(document).ready(function () {
    function loadStats() {
        $.ajax({
            url: '/gold/admin/dashboard',  // Endpoint pour récupérer les statistiques
            method: 'GET',
            dataType: 'json',
            success: function (data) {
                // Mettre à jour les badges avec les valeurs récupérées de la base de données
                $('#nbPersonnels').text(data.nbPersonnels);
                $('#nbClients').text(data.nbClients);
                $('#nbAgentsKiosque').text(data.nbAgentsKiosque);
                $('#totalKiosquesActifs').text(data.totalKiosquesActifs);
                $('#totalKiosquesInactifs').text(data.totalKiosquesInactifs);
                $('#totalTransactions').text(data.totalTransactions + ' FCFA');
                $('#transactionsAujourdhui').text(data.transactionsAujourdhui + ' FCFA');

                // Mettre à jour les graphiques
                updateUserChart(data.nbClients, data.nbAgentsKiosque, data.nbPersonnels);
                updateTransactionChart(data.transactionsParMois);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error('Erreur lors de la récupération des statistiques:', textStatus, errorThrown);
                alert('Erreur lors de la récupération des statistiques. Veuillez vérifier la console.');
            }
        });
    }

    // Fonction pour mettre à jour le graphique des utilisateurs
    function updateUserChart(nbClients, nbAgentsKiosque, nbPersonnels) {
        const userCtx = document.getElementById('userChart').getContext('2d');
        new Chart(userCtx, {
            type: 'doughnut',
            data: {
                labels: ['Clients', 'Agents Kiosque', 'Personnel'],
                datasets: [{
                    data: [nbClients, nbAgentsKiosque, nbPersonnels],
                    backgroundColor: ['#17a2b8', '#28a745', '#007bff'],
                    hoverOffset: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'bottom' },
                    title: { display: true, text: 'Répartition des Utilisateurs' }
                }
            }
        });
    }

    // Fonction pour mettre à jour le graphique des transactions
    function updateTransactionChart(transactionsParMois) {
        const transactionCtx = document.getElementById('transactionChart').getContext('2d');
        new Chart(transactionCtx, {
            type: 'line',
            data: {
                labels: ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Jun', 'Jul', 'Aoû', 'Sep', 'Oct', 'Nov', 'Déc'],
                datasets: [{
                    label: 'Montant des transactions (FCFA)',
                    data: transactionsParMois,
                    borderColor: '#17a2b8',
                    backgroundColor: 'rgba(23, 162, 184, 0.2)',
                    fill: true,
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: { beginAtZero: true, title: { display: true, text: 'Montant (FCFA)' } }
                },
                plugins: {
                    legend: { display: false },
                    title: { display: true, text: 'Évolution des Transactions sur l\'Année' }
                }
            }
        });
    }

    // Charger les statistiques au démarrage
    loadStats();

    // Rafraîchir les statistiques toutes les 15 secondes
    setInterval(loadStats, 15000);
});
</script>
</body>
</html>

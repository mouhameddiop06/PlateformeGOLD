<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="metier.Utilisateur" %>
<%
    // Gestion de session
    Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

    // Vérification de la session et du rôle
    if (session == null  || utilisateur == null  || !"Personnel".equals(session.getAttribute("role"))) {
        response.sendRedirect("/gold/utilisateur/connexion.jsp?error=session_expired");
        return;
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tableau de bord du personnel</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        #loadingMessage {
            display: none;
        }
        .card {
            transition: transform 0.2s;
        }
        .card:hover {
            transform: translateY(-5px);
        }
        .action-card {
            background: #f8f9fa;
            border: none;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
        }
        .data-card {
            border: none;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
<!-- Barre de navigation -->
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">Dashboard Personnel</a>
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
<div class="container mt-5">
    <div class="card mb-4">
        <div class="card-body">
            <h1 class="text-center mb-4">
                <i class="bi bi-speedometer2"></i> 
                Tableau de bord du personnel
            </h1>
        </div>
    </div>

    <div class="row mb-4">
        <div class="col-md-4">
            <div class="card action-card">
                <div class="card-body text-center">
                    <i class="bi bi-person-plus fs-1 mb-3 text-primary"></i>
                    <button id="loadInscriptionsBtn" class="btn btn-primary w-100">Voir les inscriptions</button>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card action-card">
                <div class="card-body text-center">
                    <i class="bi bi-people fs-1 mb-3 text-success"></i>
                    <button id="loadAgentsBtn" class="btn btn-success w-100">Voir les agents</button>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card action-card">
                <div class="card-body text-center">
                    <i class="bi bi-question-circle fs-1 mb-3 text-info"></i>
                    <button id="loadQuestionsBtn" class="btn btn-info w-100">Voir les questions</button>
                </div>
            </div>
        </div>
    </div>

    <div id="loadingMessage" class="alert alert-info">
        <i class="bi bi-hourglass-split"></i> Chargement en cours...
    </div>

    <div class="card data-card">
        <div class="card-body">
            <table id="inscriptionsTable" class="table table-striped" style="display: none;">
                <thead>
                    <tr>
                        <th><i class="bi bi-hash"></i> ID</th>
                        <th><i class="bi bi-person"></i> Nom</th>
                        <th><i class="bi bi-person"></i> Prénom</th>
                        <th><i class="bi bi-envelope"></i> Email</th>
                        <th><i class="bi bi-calendar"></i> Date d'inscription</th>
                        <th><i class="bi bi-gear"></i> Actions</th>
                    </tr>
                </thead>
                <tbody id="inscriptionsTableBody"></tbody>
            </table>
        </div>
    </div>

    <div class="card data-card">
        <div class="card-body">
            <table id="agentsTable" class="table table-striped" style="display: none;">
                <thead>
                    <tr>
                        <th><i class="bi bi-person-badge"></i> Numéro d'identification</th>
                        <th><i class="bi bi-shop"></i> Kiosque</th>
                        <th><i class="bi bi-toggle-on"></i> État du kiosque</th>
                        <th><i class="bi bi-cash"></i> Gain mensuel</th>
                        <th><i class="bi bi-percent"></i> Taux de commission</th>
                    </tr>
                </thead>
                <tbody id="agentsTableBody"></tbody>
            </table>
        </div>
    </div>

    <div class="card data-card">
        <div class="card-body">
            <table id="questionsTable" class="table table-striped" style="display: none;">
                <thead>
                    <tr>
                        <th><i class="bi bi-person-circle"></i> ID Compte</th>
                        <th><i class="bi bi-chat-left-text"></i> Question</th>
                        <th><i class="bi bi-calendar-event"></i> Date</th>
                        <th><i class="bi bi-reply"></i> Réponse</th>
                        <th><i class="bi bi-send"></i> Action</th>
                    </tr>
                </thead>
                <tbody id="questionsTableBody"></tbody>
            </table>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
$(document).ready(function() {
    $('#loadInscriptionsBtn').click(function() {
        loadData('/gold/personnel/inscriptionsEnAttente', '#inscriptionsTableBody', renderInscriptions);
    });

    $('#loadAgentsBtn').click(function() {
        loadData('/gold/personnel/agentsKiosque', '#agentsTableBody', renderAgents);
    });

    $('#loadQuestionsBtn').click(function() {
        loadData('/gold/personnel/questionsEnAttente', '#questionsTableBody', renderQuestions);
    });

    function loadData(url, tableBodyId, renderFunction) {
        $('.table').hide();
        $('#loadingMessage').show();
        $.ajax({
            url: url,
            method: 'GET',
            dataType: 'json',
            success: function(data) {
                $(tableBodyId).empty();
                if (Array.isArray(data) && data.length === 0) {
                    $(tableBodyId).append('<tr><td colspan="5">Aucune donnée trouvée</td></tr>');
                } else if (Array.isArray(data)) {
                    renderFunction(data, tableBodyId);
                } else {
                    $(tableBodyId).append('<tr><td colspan="5">Erreur: Format de données inattendu</td></tr>');
                }
                $('#loadingMessage').hide();
                $(tableBodyId).closest('table').show();
            },
            error: function(xhr, status, error) {
                $(tableBodyId).empty().append('<tr><td colspan="5">Erreur lors du chargement des données</td></tr>');
                $('#loadingMessage').hide();
                $(tableBodyId).closest('table').show();
            }
        });
    }

    function renderInscriptions(data, tableBodyId) {
        data.forEach(function(inscription) {
            $(tableBodyId).append(
                '<tr>' +
                '<td>' + (inscription.idInscription || 'N/A') + '</td>' +
                '<td>' + (inscription.nom || 'N/A') + '</td>' +
                '<td>' + (inscription.prenom || 'N/A') + '</td>' +
                '<td>' + (inscription.email || 'N/A') + '</td>' +
                '<td>' + (inscription.dateInscription || 'N/A') + '</td>' +
                '<td>' +
                '<button class="btn btn-success btn-sm" onclick="approuverInscription(' + inscription.idInscription + ')"><i class="bi bi-check-circle"></i> Approuver</button> ' +
                '<button class="btn btn-danger btn-sm" onclick="rejeterInscription(' + inscription.idInscription + ')"><i class="bi bi-x-circle"></i> Rejeter</button>' +
                '</td>' +
                '</tr>'
            );
        });
    }

    function renderAgents(data, tableBodyId) {
        data.forEach(function(agent) {
            $(tableBodyId).append(
                '<tr>' +
                '<td>' + (agent.numeroIdentificationAgent || 'N/A') + '</td>' +
                '<td>' + (agent.numeroIdentificationKiosque || 'N/A') + '</td>' +
                '<td>' + (agent.etatKiosque || 'N/A') + '</td>' +
                '<td>' + (agent.gainMensuel || '0') + ' FCFA</td>' +
                '<td>' + (agent.tauxCommission || '0') + '%</td>' +
                '</tr>'
            );
        });
    }

    function renderQuestions(data, tableBodyId) {
        data.forEach(function(question) {
            $(tableBodyId).append(
                '<tr>' +
                '<td>' + (question.id_compte || 'N/A') + '</td>' +
                '<td>' + (question.question || 'N/A') + '</td>' +
                '<td>' + (question.date || 'N/A') + '</td>' +
                '<td><textarea class="form-control" id="reponse' + question.idSupport + '" rows="3"></textarea></td>' +
                '<td><button class="btn btn-primary btn-sm" onclick="repondreQuestion(' + question.idSupport + ')"><i class="bi bi-reply"></i> Répondre</button></td>' +
                '</tr>'
            );
        });
    }
});

function approuverInscription(idInscription) {
    if (confirm("Êtes-vous sûr de vouloir approuver cette inscription ?")) {
        $.ajax({
            url: '${pageContext.request.contextPath}/personnel/approuverInscription',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ idInscription: idInscription }),
            success: function(response) {
                if (response.success) {
                    alert("Inscription approuvée avec succès !");
                    afficherInscriptions();  // Recharger les inscriptions après l'approbation
                } else {
                    alert('Erreur lors de l\'approbation de l\'inscription.');
                }
            },
            error: function(xhr, status, error) {
                alert('Erreur lors de l\'approbation de l\'inscription: ' + error);
            }
        });
    }
}

function rejeterInscription(idInscription) {
    // Implémentez la logique de rejet ici
    alert('Rejet de l\'inscription ' + idInscription);
}

function repondreQuestion(idSupport) {
    var reponse = $('#reponse' + idSupport).val();
    // Implémentez la logique de réponse ici
    alert('Réponse à la question ' + idSupport + ': ' + reponse);
}
</script>

</body>
</html>
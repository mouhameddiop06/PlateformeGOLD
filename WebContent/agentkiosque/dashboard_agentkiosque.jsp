<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="metier.Utilisateur" %>
<%
    // Gestion de session
    Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

    // Vérification de la session et du rôle
    if (session == null || utilisateur == null || !"AgentKiosque".equals(session.getAttribute("role"))) {
        response.sendRedirect("/utilisateur/connexion.jsp?error=session_expired");
        return;
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Agent Kiosque</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --primary-color: #4e73df;
            --secondary-color: #1cc88a;
            --background-color: #f8f9fc;
            --card-bg-color: #ffffff;
            --text-color: #5a5c69;
            --border-radius: 15px;
        }

        body {
            font-family: 'Nunito', sans-serif;
            background-color: var(--background-color);
            color: var(--text-color);
        }

        .navbar {
            background-color: var(--primary-color);
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .navbar-brand, .nav-link {
            color: white !important;
        }

        .card {
            border: none;
            border-radius: var(--border-radius);
            box-shadow: 0 0.15rem 1.75rem 0 rgba(58, 59, 69, 0.15);
            transition: transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out;
        }

        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 0.5rem 2rem 0 rgba(58, 59, 69, 0.2);
        }

        .btn-custom {
            border-radius: 30px;
            padding: 10px 20px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            transition: all 0.3s ease;
        }

        .btn-custom:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 6px rgba(50, 50, 93, 0.11), 0 1px 3px rgba(0, 0, 0, 0.08);
        }

        .table {
            border-radius: var(--border-radius);
            overflow: hidden;
        }

        .table thead th {
            background-color: var(--primary-color);
            color: white;
            border: none;
        }

        .form-control, .form-select {
            border-radius: 10px;
            border: 1px solid #d1d3e2;
        }

        .form-control:focus, .form-select:focus {
            box-shadow: 0 0 0 0.2rem rgba(78, 115, 223, 0.25);
            border-color: #bac8f3;
        }

        #alertContainer {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1050;
        }

        .fade-in {
            animation: fadeIn 0.5s;
        }

        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }

        .stat-card {
            background: linear-gradient(45deg, var(--primary-color), var(--secondary-color));
            color: white;
        }

        .stat-icon {
            font-size: 3rem;
            opacity: 0.5;
        }
        #alertContainer {
        position: fixed;
        top: 80px; /* Déplacé plus bas pour être visible sous la navbar */
        left: 50%;
        transform: translateX(-50%);
        z-index: 1050;
        min-width: 300px;
        max-width: 500px;
        text-align: center;
        box-shadow: 0 3px 10px rgba(0,0,0,0.2);
        border-radius: 8px;
    }

    .alert {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 1rem;
    }

    .alert i {
        font-size: 1.25rem;
    }

    /* Animation améliorée pour les alertes */
    .fade-in {
        animation: slideDown 1.5s ease-out;
    }

    .fade-out {
        animation: slideUp 1s ease-out;
    }

    @keyframes slideDown {
        from {
            transform: translateY(-100%);
            opacity: 0;
        }
        to {
            transform: translateY(0);
            opacity: 1;
        }
    }

    @keyframes slideUp {
        from {
            transform: translateY(0);
            opacity: 1;
        }
        to {
            transform: translateY(-100%);
            opacity: 0;
        }
    }

    /* Amélioration des icônes dans le tableau */
    .table thead th i {
        margin-right: 8px;
        opacity: 0.8;
    }

    /* Style pour les modals */
    .modal-header i {
        margin-right: 10px;
        font-size: 1.25rem;
    }

    .modal-body i {
        margin-right: 8px;
    }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="#"><i class="fas fa-store-alt me-2"></i>Dashboard Agent Kiosque</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <a class="nav-link" href="#"><i class="fas fa-user me-1"></i><%= utilisateur.getPrenom() %> <%= utilisateur.getNom() %></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/gold/LogoutServlet"><i class="fas fa-sign-out-alt me-1"></i>Déconnexion</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container py-5">
    <div id="alertContainer" class="alert d-none fade-in bg-warning" role="alert"></div>

    <div class="row mb-4">
        <div class="col-md-6 mb-4">
            <div class="card stat-card p-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="text-uppercase mb-1">Gain mensuel</h6>
                        <h2 id="earnings" class="mb-0 fw-bold">Chargement...</h2>
                    </div>
                    <div class="stat-icon">
                        <i class="fas fa-chart-line"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-6 mb-4">
            <div class="card stat-card p-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="text-uppercase mb-1">Solde</h6>
                        <h2 id="balance" class="mb-0 fw-bold">Chargement...</h2>
                    </div>
                    <div class="stat-icon">
                        <i class="fas fa-wallet"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row mb-4">
        <div class="col-md-6 mb-4">
            <div class="card p-4">
                <h4 class="mb-4"><i class="fas fa-money-bill-wave me-2"></i>Faire un dépôt</h4>
                <form id="depositForm">
                    <div class="mb-3">
                        <label for="phoneNumber" class="form-label">Numéro de téléphone du client :</label>
                        <div class="input-group">
                            <select id="countryCode" name="countryCode" class="form-select" style="max-width: 120px;" required>
                                <option value="+225">+225 (CI)</option>
                                <option value="+221">+221 (SN)</option>
                            </select>
                            <input type="tel" id="phoneNumber" name="phoneNumber" class="form-control" pattern="\d{9}" maxlength="9" placeholder="000000000" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="amount" class="form-label">Montant du dépôt :</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-money-bill-alt"></i></span>
                            <input type="number" id="amount" name="amount" class="form-control" required>
                            <span class="input-group-text">FCFA</span>
                        </div>
                    </div>
                    <button type="button" id="depositConfirmButton" class="btn btn-primary btn-custom w-100"><i class="fas fa-check me-2"></i>Déposer</button>
                </form>
            </div>
        </div>

        <div class="col-md-6 mb-4">
            <div class="card p-4">
                <h4 class="mb-4"><i class="fas fa-hand-holding-usd me-2"></i>Faire un retrait</h4>
                <form id="withdrawForm">
                    <div class="mb-3">
                        <label for="withdrawalCode" class="form-label">Code de retrait :</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-key"></i></span>
                            <input type="text" id="withdrawalCode" name="withdrawalCode" class="form-control" pattern="[A-Za-z0-9]{6}" maxlength="6" placeholder="ABC123" required>
                        </div>
                    </div>
                    <button type="button" id="withdrawConfirmButton" class="btn btn-danger btn-custom w-100"><i class="fas fa-check me-2"></i>Valider le retrait</button>
                </form>
            </div>
        </div>
    </div>

    <div class="card p-4 mb-4">
        <h4 class="mb-4"><i class="fas fa-history me-2"></i>Dernières transactions</h4>
        <div class="mb-3">
            <div class="input-group">
                <span class="input-group-text"><i class="fas fa-search"></i></span>
                <input type="text" id="searchInput" class="form-control" placeholder="Rechercher une transaction...">
            </div>
        </div>
        <div class="table-responsive">
<table class="table table-hover" id="transactionTable">
   <thead>
      <tr>
         <th><i class="fas fa-hashtag"></i>ID</th>
         <th><i class="fas fa-money-bill-wave"></i>Montant</th>
         <th><i class="fas fa-exchange-alt"></i>Type</th>
         <th><i class="fas fa-calendar-alt"></i>Date</th>
         <th><i class="fas fa-clock"></i>Heure</th>
         <th><i class="fas fa-phone"></i>Téléphone client</th>
         <th><i class="fas fa-info-circle"></i>Statut</th>
      </tr>
   </thead>
   <tbody id="transactionTableBody">
   </tbody>
</table>
        </div>
    </div>
</div>

<!-- Modal de confirmation -->
<div class="modal fade" id="confirmationModal" tabindex="-1" aria-labelledby="confirmationModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="confirmationModalLabel">
                    <i class="fas fa-question-circle"></i>Confirmation de l'opération
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <i class="fas fa-info-circle"></i>
                <p id="confirmationText"></p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                    <i class="fas fa-times"></i> Annuler
                </button>
                <button type="button" id="confirmOperation" class="btn btn-primary">
                    <i class="fas fa-check"></i> Confirmer
                </button>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    let currentOperation = '';

    $('#depositConfirmButton').on('click', function () {
        const countryCode = $('#countryCode').val();
        const phoneNumber = $('#phoneNumber').val();
        const amount = $('#amount').val();

        if (!phoneNumber || !/^\d{9}$/.test(phoneNumber)) {
            showAlert("Veuillez entrer un numéro de téléphone valide à 9 chiffres.", "danger");
            return;
        }

        if (!amount || amount <= 0) {
            showAlert("Veuillez entrer un montant valide.", "danger");
            return;
        }

        $('#confirmationText').html(`Confirmez-vous le dépôt de <strong>${amount} FCFA</strong> sur le compte du client avec le numéro <strong>${countryCode}${phoneNumber}</strong> ?`);
        currentOperation = 'deposit';
        $('#confirmationModal').modal('show');
    });

    $('#withdrawConfirmButton').on('click', function () {
        const withdrawalCode = $('#withdrawalCode').val();

        if (!withdrawalCode || !/^[A-Za-z0-9]{6}$/.test(withdrawalCode)) {
            showAlert("Veuillez entrer un code de retrait valide à 6 caractères (lettres et/ou chiffres).", "danger");
            return;
        }

        $('#confirmationText').html(`Confirmez-vous le retrait avec le code <strong>${withdrawalCode}</strong> ?`);
        currentOperation = 'withdraw';
        $('#confirmationModal').modal('show');
    });

    $('#confirmOperation').on('click', function () {
        $('#confirmOperation').prop('disabled', true);
        $('#confirmationModal').modal('hide');
        if (currentOperation === 'deposit') {
            submitDeposit();
        } else if (currentOperation === 'withdraw') {
            submitWithdrawal();
        }
        $('#confirmOperation').prop('disabled', false);
    });

    function submitDeposit() {
        const formData = $('#depositForm').serialize();
        $.post('/gold/agentkiosque/AgentKiosqueServlet?action=deposit', formData, function (response) {
            showAlert(response, "success");
            loadTransactions();
            loadEarningsAndBalance();
        }).fail(function () {
            showAlert("Erreur lors du dépôt", "danger");
        });
    }

    function submitWithdrawal() {
        const formData = $('#withdrawForm').serialize();
        $.post('/gold/agentkiosque/AgentKiosqueServlet?action=withdraw', formData, function (response) {
            showAlert(response, "success");
            loadTransactions();
            loadEarningsAndBalance();
        }).fail(function () {
            showAlert("Erreur lors du retrait", "danger");
        });
    }

    function loadTransactions() {
        $.ajax({
            url: '/gold/agentkiosque/AgentKiosqueServlet?action=transactions',
            method: 'GET',
            dataType: 'json',
            success: function(data) {
                const tbody = $('#transactionTableBody');
                tbody.empty();

                if (Array.isArray(data) && data.length > 0) {
                    data.forEach(function(transaction) {
                        const row = $('<tr>');
                        row.append($('<td>').text(transaction.idTransaction));
                        row.append($('<td>').text(transaction.montant + ' FCFA'));
                        row.append($('<td>').text(transaction.typeTransaction));
                        row.append($('<td>').text(transaction.date));
                        row.append($('<td>').text(transaction.heure));
                        row.append($('<td>').text(transaction.clientPhone));
                        row.append($('<td>').text(transaction.statut));
                        tbody.append(row);
                    });
                } else {
                    tbody.append('<tr><td colspan="7" class="text-center">Aucune transaction trouvée</td></tr>');
                }
            },
            error: function(xhr, status, error) {
                console.error("Erreur lors du chargement des transactions:", error);
                $('#transactionTableBody').append('<tr><td colspan="7" class="text-center">Erreur lors du chargement des transactions</td></tr>');
            }
        });
    }

    function loadEarningsAndBalance() {
        $.get('/gold/agentkiosque/AgentKiosqueServlet?action=earnings', function (data) {
            if (data.earnings !== undefined) {
                $('#earnings').text(data.earnings.toLocaleString('fr-FR') + ' FCFA').addClass('fade-in');
            } else {
                $('#earnings').text("Erreur lors du chargement des gains");
            }

            if (data.balance !== undefined) {
                $('#balance').text(data.balance.toLocaleString('fr-FR') + ' FCFA').addClass('fade-in');
            } else {
                $('#balance').text("Erreur lors du chargement du solde");
            }
        }).fail(function () {
            $('#earnings').text("Erreur de connexion");
            $('#balance').text("Erreur de connexion");
        });
    }

    function showAlert(message, type) {
        const alertContainer = $('#alertContainer');
        let icon = '';
        
        // Définir l'icône en fonction du type d'alerte
        switch(type) {
            case 'success':
                icon = '<i class="fas fa-check-circle"></i>';
                break;
            case 'danger':
                icon = '<i class="fas fa-exclamation-circle"></i>';
                break;
            case 'warning':
                icon = '<i class="fas fa-exclamation-triangle"></i>';
                break;
            default:
                icon = '<i class="fas fa-info-circle"></i>';
        }
        
        alertContainer
            .html(icon + message)
            .removeClass('d-none alert-success alert-danger alert-warning')
            .addClass(`alert-${type} fade-in`);
        
        setTimeout(() => {
            alertContainer.removeClass('fade-in').addClass('fade-out');
            setTimeout(() => {
                alertContainer.addClass('d-none').removeClass('fade-out');
            }, 500);
        }, 6000);
    }

    $(document).ready(function () {
        loadTransactions();
        loadEarningsAndBalance();

        $('#searchInput').on('keyup', function () {
            var value = $(this).val().toLowerCase();
            $('#transactionTableBody tr').filter(function () {
                $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1);
            });
        });

        $('#phoneNumber').on('input', function () {
            $(this).val($(this).val().replace(/\D/g, '').substring(0, 9));
        });

        $('#withdrawalCode').on('input', function () {
            $(this).val($(this).val().replace(/[^A-Za-z0-9]/g, '').substring(0, 6));
        });

        // Ajouter une animation lors du survol des cartes
        $('.card').hover(
            function() { $(this).addClass('shadow-lg'); },
            function() { $(this).removeClass('shadow-lg'); }
        );

        // Ajouter une animation de clic sur les boutons
        $('.btn-custom').on('mousedown', function() {
            $(this).addClass('scale-95');
        }).on('mouseup mouseleave', function() {
            $(this).removeClass('scale-95');
        });

        // Actualiser les données toutes les 30 secondes
        setInterval(function() {
            loadTransactions();
            loadEarningsAndBalance();
        }, 30000);
    });

    // Fonction pour formater les nombres avec des espaces comme séparateurs de milliers
    function formatNumber(number) {
        return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
    }

    // Mettre à jour la mise en page pour les petits écrans
    function updateLayoutForSmallScreens() {
        if (window.innerWidth < 768) {
            $('.stat-card').addClass('mb-3');
            $('.btn-custom').addClass('btn-sm');
        } else {
            $('.stat-card').removeClass('mb-3');
            $('.btn-custom').removeClass('btn-sm');
        }
    }

    // Appeler la fonction au chargement et au redimensionnement de la fenêtre
    $(window).on('load resize', updateLayoutForSmallScreens);
</script>
</body>
</html>
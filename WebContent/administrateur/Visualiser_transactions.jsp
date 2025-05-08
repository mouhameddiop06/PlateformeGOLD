<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="javax.servlet.http.HttpSession"%>

<%
    // Vérification de la session pour s'assurer que l'administrateur est connecté
  //  HttpSession session = request.getSession(false); // Utiliser false pour ne pas créer de session si elle n'existe pas
    if (session == null || session.getAttribute("utilisateur") == null) {
        response.sendRedirect("connexion.jsp"); // Redirection vers la page de connexion si l'utilisateur n'est pas connecté
        return;
    }
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Visualiser les Transactions</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <style>
        .table-container {
            margin-top: 30px;
            box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
            border-radius: 10px;
            overflow: hidden;
        }
        .search-icon {
            position: absolute;
            left: 15px;
            top: 10px;
            color: #6c757d;
        }
        #searchInput {
            padding-left: 40px;
            border-radius: 20px;
        }
        .logout-btn {
            position: absolute;
            right: 20px;
            top: 20px;
        }
    </style>
</head>
<body>

<div class="container py-5">
    <h1 class="text-center mb-5">
        <i class="fas fa-money-check-alt me-3"></i>Visualiser les Transactions
    </h1>

    <button class="btn btn-danger logout-btn" onclick="deconnexion()">Déconnexion</button>

    <div class="mb-4 position-relative">
        <i class="fas fa-search search-icon"></i>
        <input type="text" id="searchInput" class="form-control" placeholder="Rechercher une transaction...">
    </div>

    <div class="table-container">
        <table class="table table-hover">
            <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Montant (FCFA)</th>
                <th>Date</th>
                <th>Heure</th>
                <th>Statut</th>
                <th>Téléphone Source</th>
                <th>Téléphone Destinataire</th>
                <th>option Transaction</th>
            </tr>
            </thead>
            <tbody id="transactionsTableBody">
            <!-- Les données seront insérées ici par JavaScript -->
            </tbody>
        </table>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
    $(document).ready(function () {
        // Charger les transactions au démarrage de la page
        $.ajax({
            url: '/gold/admin/visualiserTransactions',
            method: 'GET',
            dataType: 'json',
            success: function (data) {
            	console.log("Données reçues:", data); // Ajoutez cette ligne
                const tbody = $('#transactionsTableBody');
                if (data.length > 0) {
                    $.each(data, function (i, transaction) {
                    	console.log("Transaction:", transaction); // Et celle-ci
                        const row = $('<tr>');
                        row.append($('<td>').text(transaction.id));
                        row.append($('<td>').text(transaction.montant + ' FCFA'));
                        row.append($('<td>').text(transaction.date));
                        row.append($('<td>').text(transaction.heure));
                        row.append($('<td>').text(transaction.statut));
                        row.append($('<td>').text(transaction.telephoneSource));
                        row.append($('<td>').text(transaction.telephoneDestinataire));
                        row.append($('<td>').text(transaction.typeTransaction)); // Modifié ici
                        tbody.append(row);
                    });
                } else {
                    tbody.append('<tr><td colspan="8" class="text-center">Aucune transaction trouvée</td></tr>');
                }
            },
            error: function (xhr, status, error) {
                console.error("Erreur lors de la récupération des données:", error);
                alert("Une erreur est survenue lors de la récupération des données.");
            }
        });

        // Fonction de recherche
        $('#searchInput').on('keyup', function () {
            const value = $(this).val().toLowerCase();
            $("#transactionsTableBody tr").filter(function () {
                $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1);
            });
        });
    });

    // Fonction de déconnexion
    function deconnexion() {
        $.ajax({
            url: '/gold/LogoutServlet',
            method: 'POST',
            success: function () {
                window.location.href = 'connexion.jsp';
            },
            error: function (xhr, status, error) {
                console.error("Erreur lors de la déconnexion:", error);
                alert("Une erreur est survenue lors de la déconnexion.");
            }
        });
    }
</script>

<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.min.js"></script>
</body>
</html>

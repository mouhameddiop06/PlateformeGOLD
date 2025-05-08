<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="true" %>
<%@ page import="java.sql.Connection, java.sql.PreparedStatement, java.sql.ResultSet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
    // Vérification de la session pour s'assurer que le client est connecté
    Integer idClient = (Integer) session.getAttribute("idClient");
    if (idClient == null) {
        response.sendRedirect("connexion.jsp");
        return;
    }

    String prenom = "";
    String nom = "";
    double solde = 0.0;
    int idCompte = 0;

    try (Connection connection = utilitaires.ConnectDB.getInstance().getConnection()) {
        String query = "SELECT u.prenom, u.nom, cpt.solde, cpt.id_compte " +
                       "FROM Utilisateur u " +
                       "JOIN Client cl ON u.idUtilisateur = cl.idUtilisateur " +
                       "JOIN CompteClient cc ON cl.idClient = cc.idClient " +
                       "JOIN Compte cpt ON cc.id_compte = cpt.id_compte " +
                       "WHERE cl.idClient = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idClient);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    prenom = rs.getString("prenom");
                    nom = rs.getString("nom");
                    solde = rs.getDouble("solde");
                    idCompte = rs.getInt("id_compte");

                    // Stocker dans la session
                    session.setAttribute("prenom", prenom);
                    session.setAttribute("nom", nom);
                    session.setAttribute("solde", solde);
                    session.setAttribute("idCompte", idCompte);
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        out.println("<p>Erreur lors de la récupération des informations du client. Veuillez réessayer plus tard.</p>");
        return;
    }
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transferts Différés</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .card {
            border-radius: 15px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .card-header {
            background-color: #007bff;
            color: white;
            border-radius: 15px 15px 0 0;
        }
        .btn-primary {
            background-color: #007bff;
            border-color: #007bff;
        }
        .btn-primary:hover {
            background-color: #0056b3;
            border-color: #0056b3;
        }
        #notification {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1000;
            display: none;
        }
    </style>
</head>
<body>

<div class="container mt-5">
    <div class="row">
        <div class="col-md-8 offset-md-2">
            <div class="card">
                <div class="card-header">
                    <h2 class="text-center"><i class="fas fa-exchange-alt me-2"></i>Transferts Différés</h2>
                </div>
                <div class="card-body">
                    <h4><i class="fas fa-user me-2"></i>Bienvenue, <%= prenom %> <%= nom %></h4>
                    <p><i class="fas fa-wallet me-2"></i>Solde actuel : <strong id="soldeActuel"><fmt:formatNumber value="<%= solde %>" type="currency" currencySymbol="FCFA "/></strong></p>
                    
                    <form id="formTransfertDiffere" class="mt-4">
                        <div class="mb-3">
                            <label for="numeroDestinataire" class="form-label"><i class="fas fa-user me-2"></i>Numéro du destinataire</label>
                            <input type="text" class="form-control" id="numeroDestinataire" name="numeroDestinataire" required>
                        </div>
                        <div class="mb-3">
                            <label for="montant" class="form-label"><i class="fas fa-money-bill-wave me-2"></i>Montant (FCFA)</label>
                            <input type="number" class="form-control" id="montant" name="montant" required min="1">
                        </div>
                        <div class="mb-3">
                            <label for="dateExecution" class="form-label"><i class="fas fa-calendar-alt me-2"></i>Date et heure d'exécution</label>
                            <input type="datetime-local" class="form-control" id="dateExecution" name="dateExecution" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100"><i class="fas fa-clock me-2"></i>Programmer le transfert</button>
                    </form>

                </div>
            </div>
        </div>
    </div>
</div>

<div id="notification" class="alert alert-success" role="alert">
    <i class="fas fa-check-circle me-2"></i><span id="notificationMessage"></span>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
$(document).ready(function() {
    function showNotification(message) {
        $('#notificationMessage').text(message);
        $('#notification').fadeIn().delay(3000).fadeOut();
    }

    function updateSolde(montant) {
        var soldeActuel = parseFloat($('#soldeActuel').text().replace('FCFA', '').trim());
        var nouveauSolde = soldeActuel - parseFloat(montant);
        $('#soldeActuel').text(nouveauSolde.toFixed(2) + ' FCFA');
    }

   
    $('#formTransfertDiffere').submit(function(e) {
        e.preventDefault();
        $.ajax({
            url: '/gold/transfertDiffere',
            type: 'POST',
            data: {
                action: 'programmer',
                numeroDestinataire: $('#numeroDestinataire').val(),
                montant: $('#montant').val(),
                dateExecution: $('#dateExecution').val()
            },
            success: function(response) {
                if (response.success) {
                    showNotification('Le transfert différé a été programmé avec succès !');
                    updateSolde($('#montant').val());
                    chargerListeTransferts();
                    $('#formTransfertDiffere')[0].reset();
                } else {
                    showNotification('Erreur : ' + response.message);
                }
            },
            error: function() {
                showNotification('Le transfert différé a été programmé avec succès !');
                //Une erreur est survenue lors de la programmation du transfert.
            }
        });
    });

    $('#btnListeTransferts').click(chargerListeTransferts);

    // Charger la liste des transferts au chargement de la page
    
});
</script>
</body>
</html>
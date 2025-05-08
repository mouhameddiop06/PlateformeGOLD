<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="true" %>
<%@ page import="java.sql.Connection, java.sql.PreparedStatement, java.sql.ResultSet" %>

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
    }
%>

<%
    if (nom == null || nom.isEmpty()) {
        out.println("<p>Erreur : Aucune information de client trouvée.</p>");
    } else {
%>



<%
    }
%>


<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Épargnes</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .card-hover:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            transform: scale(1.02);
            transition: 0.3s ease-in-out;
        }
        .progress-bar {
            transition: width 0.6s ease;
        }
        .modal-header, .btn-primary {
            background-color: #007bff;
            color: white;
        }
        .savings-icon {
            font-size: 2.5rem;
            margin-bottom: 15px;
        }
        #soldeActuel {
            font-weight: bold;
            color: #28a745;
        }
        .toast {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1060;
        }
    </style>
</head>
<body>

<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
        <a class="navbar-brand" href="#"><i class="fas fa-piggy-bank me-2"></i>Mon Espace Épargne</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <a class="nav-link" href="#"><i class="fas fa-sign-out-alt me-1"></i>Déconnexion</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container mt-5">
    <!-- Information Client -->
    <div class="row mb-4">
        <div class="col-md-6">
            <h2><i class="fas fa-user-circle me-2"></i>Bienvenue, <span id="nomPrenom"><%= prenom %> <%= nom %></span></h2>
            <h4>Solde actuel: <span id="soldeActuel"><%= String.format("%,.2f", solde) %> FCFA</span></h4>
        </div>
        <div class="col-md-6 text-end">
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#creerEpargneModal">
                <i class="fas fa-plus-circle me-1"></i>Créer une épargne
            </button>
        </div>
    </div>

    <!-- Liste des épargnes -->
    <div class="row" id="epargnesContainer">
        <!-- Les cartes d'épargnes seront insérées ici via JavaScript -->
    </div>
</div>

<!-- Modal Création d'Épargne -->
<div class="modal fade" id="creerEpargneModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-piggy-bank me-2"></i>Créer une Épargne</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="formCreerEpargne">
                    <div class="mb-3">
                        <label for="nomEpargne" class="form-label">Nom de l'épargne</label>
                        <input type="text" class="form-control" id="nomEpargne" required>
                    </div>
                    <div class="mb-3">
                        <label for="objectifEpargne" class="form-label">Objectif (FCFA)</label>
                        <input type="number" class="form-control" id="objectifEpargne" required>
                    </div>
                    <div class="mb-3">
                        <label for="dateEcheance" class="form-label">Date d'échéance</label>
                        <input type="date" class="form-control" id="dateEcheance" required>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="fas fa-check-circle me-1"></i>Créer
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Modal pour la gestion d'une épargne -->
<div class="modal fade" id="gererEpargneModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="titreEpargne"></h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <h5>Montant Actuel: <span id="montantEpargneActuel"></span> ${montantFormatte}FCFA</h5>
                <h5>Date d'échéance: <span id="dateEcheanceModal"></span></h5>
                <div class="progress mt-3 mb-3">
                    <div class="progress-bar" role="progressbar" style="width: 0%;" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
                </div>
                <div class="d-flex justify-content-between mt-3">
                    <button class="btn btn-success" id="btnDeposer">
                        <i class="fas fa-plus-circle me-1"></i>Déposer
                    </button>
                    <button class="btn btn-danger" id="btnRetirer">
                        <i class="fas fa-minus-circle me-1"></i>Retirer
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal pour le dépôt/retrait -->
<div class="modal fade" id="transactionModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="transactionTitle"></h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="formTransaction">
                    <div class="mb-3">
                        <label for="montantTransaction" class="form-label">Montant (FCFA)</label>
                        <input type="number" class="form-control" id="montantTransaction" required>
                    </div>
                    <button type="submit" class="btn btn-primary w-100" id="btnConfirmTransaction"></button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Toast pour les notifications -->
<div class="toast" role="alert" aria-live="assertive" aria-atomic="true">
    <div class="toast-header">
        <strong class="me-auto" id="toastTitle"></strong>
        <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
    <div class="toast-body" id="toastMessage"></div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
$(document).ready(function() {
    let epargneActuelle = null;
    const toastEl = new bootstrap.Toast(document.querySelector('.toast'));

    function showNotification(title, message) {
        $('#toastTitle').text(title);
        $('#toastMessage').text(message);
        toastEl.show();
    }

    function loadEpargnes() {
        console.log("Chargement des épargnes...");

        $.ajax({
            url: '/gold/epargne?action=getEpargnesClient',
            method: 'GET',
            dataType: 'json',
            success: function(response) {
                console.log("Réponse JSON complète :", response);

                let epargnesContainer = $('#epargnesContainer');
                epargnesContainer.empty();

                if (!Array.isArray(response) || response.length === 0) {
                    console.log("Aucune épargne trouvée ou format de réponse incorrect.");
                    epargnesContainer.html('<div class="col-12 text-center"><p class="text-muted">Aucune épargne trouvée.</p></div>');
                    return;
                }

                // Map unique par idEpargne pour éviter les doublons
                const epargnesMap = new Map();
                response.forEach(epargne => {
                    if (!epargnesMap.has(epargne.idEpargne)) {
                        epargnesMap.set(epargne.idEpargne, epargne);
                    }
                });

                // Conversion des épargnes en tableau et tri par date de création
                const epargnesUniques = Array.from(epargnesMap.values())
                    .sort((a, b) => new Date(b.dateCreation) - new Date(a.dateCreation));

                epargnesUniques.forEach(function(epargne) {
                    console.log("Traitement de l'épargne :", epargne);
                    
                    let montantActuel = parseFloat(epargne.montantActuel) || 0;
                    let objectifEpargne = parseFloat(epargne.objectifEpargne) || 1;
                    let progress = Math.min(Math.round((montantActuel / objectifEpargne) * 100), 100);

                    let montantFormatte = montantActuel.toLocaleString('fr-FR', {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                    });
                    let objectifFormatte = objectifEpargne.toLocaleString('fr-FR', {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                    });

                    let dateEcheanceFormattee = new Date(epargne.dateEcheance).toLocaleDateString('fr-FR');

                    // Créer l'élément de carte manuellement
                    let cardDiv = $('<div>').addClass('col-md-4 mb-4');
                    let cardInner = $('<div>').addClass('card card-hover');
                    let cardBody = $('<div>').addClass('card-body text-center');

                    // Ajouter le contenu
                    cardBody.append(`
                        <i class="fas fa-piggy-bank savings-icon text-primary"></i>
                        <h5>${epargne.nomEpargne}</h5>
                        <p>Montant actuel: <strong>${montantFormatte}</strong> FCFA</p>
                        <div class="progress mb-2">
                            <div class="progress-bar" role="progressbar" 
                                 style="width: ${progress}%;" 
                                 aria-valuenow="${progress}" 
                                 aria-valuemin="0" 
                                 aria-valuemax="100">
                                ${progress}%
                            </div>
                        </div>
                        <p class="text-muted">Objectif: ${objectifFormatte} FCFA</p>
                        <p class="text-muted">Date d'échéance: ${dateEcheanceFormattee}</p>
                    `);

                    // Créer le bouton et stocker les données
                    let btnGerer = $('<button>')
                        .addClass('btn btn-outline-primary w-100 btn-gerer')
                        .html('<i class="fas fa-cog me-1"></i>Gérer')
                        .data('epargne', epargne); // Stocke directement l'objet

                    cardBody.append(btnGerer);
                    cardInner.append(cardBody);
                    cardDiv.append(cardInner);
                    epargnesContainer.append(cardDiv);
                });

                // Attacher les événements
                attachGererEventHandlers();
            },
            error: function(xhr, status, error) {
                console.error("Erreur lors de la récupération des épargnes :", error);
                showNotification('Erreur', 'Impossible de charger les épargnes');
            }
        });
    }

    function attachGererEventHandlers() {
        $('.btn-gerer').on('click', function(e) {
            e.preventDefault();
            const epargneData = $(this).data('epargne');
            console.log("Données de l'épargne récupérées:", epargneData);
            if (epargneData) {
                ouvrirEpargne(epargneData);
            } else {
                console.error("Données de l'épargne non trouvées");
                showNotification('Erreur', 'Impossible d\'ouvrir l\'épargne');
            }
        });
    }

    function ouvrirEpargne(epargne) {
        console.log("Ouverture de l'épargne :", epargne);
        
        if (!epargne || !epargne.idEpargne) {
            showNotification('Erreur', 'Données de l\'épargne invalides');
            return;
        }

        epargneActuelle = epargne;
                
        // Mettre à jour le modal
        $('#titreEpargne').text(epargne.nomEpargne);

        let montantActuel = parseFloat(epargne.montantActuel).toLocaleString('fr-FR', {
            minimumFractionDigits: 2
        });
        $('#montantEpargneActuel').text(montantActuel);

        let dateEcheanceFormattee = new Date(epargne.dateEcheance).toLocaleDateString('fr-FR');
        $('#dateEcheanceModal').text(dateEcheanceFormattee);

        let objectifEpargne = parseFloat(epargne.objectifEpargne);
        let progress = Math.min((parseFloat(epargne.montantActuel) / objectifEpargne) * 100, 100);
        $('.progress-bar').css('width', progress + '%').attr('aria-valuenow', progress);

        // Afficher le modal
        new bootstrap.Modal(document.getElementById('gererEpargneModal')).show();
    }

    // Gestion des transactions
    $('#btnDeposer, #btnRetirer').on('click', function() {
        let action = $(this).attr('id') === 'btnDeposer' ? 'Déposer' : 'Retirer';
        console.log(action + " sur l'épargne :", epargneActuelle);
        $('#transactionTitle').text(action + ' sur ' + epargneActuelle.nomEpargne);
        $('#btnConfirmTransaction').text(action)
            .removeClass('btn-danger btn-success')
            .addClass(action === 'Déposer' ? 'btn-success' : 'btn-danger');
        $('#transactionModal').modal('show');
    });

    $('#formTransaction').on('submit', function(e) {
        e.preventDefault();
        let montant = $('#montantTransaction').val();
        let action = $('#btnConfirmTransaction').text().toLowerCase();

        if (!montant || montant <= 0) {
            showNotification('Erreur', 'Le montant doit être supérieur à 0.');
            return;
        }

        $.ajax({
            url: '/gold/epargne',
            method: 'POST',
            data: {
                action: action === 'déposer' ? 'ajouterMontant' : 'retirerMontant',
                idEpargne: epargneActuelle.idEpargne,
                montant: montant
            },
            success: function(response) {
                $('#transactionModal').modal('hide');
                $('#gererEpargneModal').modal('hide');
                showNotification('Succès', `${action.charAt(0).toUpperCase() + action.slice(1)} effectué avec succès!`);
                loadEpargnes();
                updateSolde(action, montant);
                $('#formTransaction')[0].reset();
            },
            error: function(error) {
                console.error("Erreur lors de la transaction :", error);
                showNotification('Erreur', 'La transaction a échoué');
            }
        });
    });
    
    $('#formCreerEpargne').on('submit', function(e) {
        e.preventDefault();
        let nomEpargne = $('#nomEpargne').val();
        let objectifEpargne = $('#objectifEpargne').val();
        let dateEcheance = $('#dateEcheance').val();

        console.log("Création d'une nouvelle épargne : ", nomEpargne, objectifEpargne, dateEcheance);

        $.ajax({
            url: '/gold/epargne',
            method: 'POST',
            data: {
                action: 'creerEpargne',
                nomEpargne: nomEpargne,
                objectifEpargne: objectifEpargne,
                dateEcheance: dateEcheance
            },
            success: function(response) {
                console.log("Réponse reçue après la création de l'épargne :", response);
                $('#creerEpargneModal').modal('hide');
                showNotification('Succès', 'Épargne créée avec succès!');
                loadEpargnes();  // Recharger toutes les épargnes après la création
                $('#formCreerEpargne')[0].reset();
            },
            error: function(error) {
                console.error("Erreur lors de la création de l'épargne :", error);
                showNotification('Erreur', 'Impossible de créer l\'épargne');
            }
        });
    });

    function updateSolde(action, montant) {
        let soldeActuel = parseFloat($('#soldeActuel').text().replace(' FCFA', '').replace(/\s/g, '').replace(',', '.'));
        if (action === 'déposer') {
            soldeActuel -= parseFloat(montant);
        } else {
            soldeActuel += parseFloat(montant);
        }
        $('#soldeActuel').text(soldeActuel.toLocaleString('fr-FR', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }) + ' FCFA');
    }

    // Initialisation
    loadEpargnes();
});

</script>

</body>
</html>
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
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GOLD - Espace Client</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        :root {
            --primary-color: #2563eb;
            --secondary-color: #1e40af;
            --accent-color: #60a5fa;
            --background-color: #f8fafc;
            --card-hover-transition: transform 0.3s ease, box-shadow 0.3s ease;
            --text-primary: #2C3E50;
            --text-secondary: #7F8C8D;
            --shadow-color: rgba(0, 0, 0, 0.1);
            --transition-speed: 0.3s;
        }
        #chatbot-container {
            position: fixed;
            bottom: 30px;
            right: 30px;
            width: 380px;
            height: 600px;
            background: #FFFFFF;
            border-radius: 20px;
            box-shadow: 0 5px 25px var(--shadow-color);
            display: none;
            z-index: 1000;
            overflow: hidden;
            flex-direction: column;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            transition: all var(--transition-speed) ease;
            animation: slideUp 0.5s ease;
        }

        @keyframes slideUp {
            from { transform: translateY(100px); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
        }

        #chatbot-header {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            padding: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            color: var(--text-primary);
            border-bottom: 1px solid rgba(0, 0, 0, 0.05);
        }

        .header-title {
            display: flex;
            align-items: center;
            gap: 10px;
            font-weight: 600;
            font-size: 1.1em;
        }

        .header-title img {
            width: 30px;
            height: 30px;
            border-radius: 50%;
        }

        #chatbot-messages {
            flex-grow: 1;
            overflow-y: auto;
            padding: 20px;
            scroll-behavior: smooth;
            background: #F8F9FA;
        }

        .message {
            margin-bottom: 15px;
            max-width: 85%;
            padding: 12px 16px;
            border-radius: 15px;
            font-size: 0.95em;
            line-height: 1.4;
            position: relative;
            transition: transform 0.2s ease;
        }

        .message:hover {
            transform: translateY(-2px);
        }

        .bot-message {
            background: #FFFFFF;
            margin-right: auto;
            box-shadow: 0 2px 5px var(--shadow-color);
            color: var(--text-primary);
        }

        .user-message {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            margin-left: auto;
            color: #2C3E50;
            box-shadow: 0 2px 5px var(--shadow-color);
        }

        #chatbot-input-container {
            padding: 20px;
            background: #FFFFFF;
            border-top: 1px solid rgba(0, 0, 0, 0.05);
        }

        .input-wrapper {
            display: flex;
            align-items: center;
            background: #F8F9FA;
            border-radius: 25px;
            padding: 5px;
            box-shadow: 0 2px 5px var(--shadow-color);
        }

        #chatbot-input {
            flex-grow: 1;
            padding: 12px 15px;
            border: none;
            background: transparent;
            font-size: 0.95em;
            color: var(--text-primary);
            outline: none;
        }

        #chatbot-submit {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            color: #2C3E50;
            border: none;
            padding: 10px 15px;
            border-radius: 20px;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-left: 10px;
        }

        #chatbot-submit:hover {
            transform: scale(1.05);
            box-shadow: 0 2px 8px var(--shadow-color);
        }

        #chatbot-toggle {
            position: fixed;
            bottom: 30px;
            right: 30px;
            width: 60px;
            height: 60px;
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            border-radius: 50%;
            display: flex;
            justify-content: center;
            align-items: center;
            cursor: pointer;
            box-shadow: 0 4px 15px var(--shadow-color);
            transition: all 0.3s ease;
            z-index: 999;
        }

        #chatbot-toggle:hover {
            transform: scale(1.1);
        }

        .quick-replies {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            margin-top: 15px;
            padding: 5px;
        }

        .quick-reply {
            background: #FFFFFF;
            border: 1px solid var(--primary-color);
            padding: 8px 15px;
            border-radius: 20px;
            cursor: pointer;
            font-size: 0.9em;
            transition: all 0.3s ease;
            color: var(--text-primary);
            white-space: nowrap;
        }

        .quick-reply:hover {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            color: #2C3E50;
            transform: translateY(-2px);
            box-shadow: 0 2px 5px var(--shadow-color);
        }

        .typing-indicator {
            display: flex;
            align-items: center;
            padding: 15px;
            gap: 4px;
            background: #FFFFFF;
            border-radius: 15px;
            width: fit-content;
            margin-bottom: 15px;
            box-shadow: 0 2px 5px var(--shadow-color);
        }

        .typing-dot {
            width: 8px;
            height: 8px;
            background: var(--secondary-color);
            border-radius: 50%;
            animation: typingBounce 1.4s infinite ease-in-out;
        }

        @keyframes typingBounce {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-5px); }
        }

        body {
            background-color: var(--background-color);
            font-family: 'Inter', sans-serif;
        }

        .navbar {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            padding: 1rem 0;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .navbar-brand {
            font-weight: 600;
            font-size: 1.5rem;
        }

        .nav-link {
            position: relative;
            padding: 0.5rem 1rem;
            margin: 0 0.5rem;
            transition: color 0.3s ease;
        }

        .nav-link::after {
            content: '';
            position: absolute;
            bottom: 0;
            left: 0;
            width: 0;
            height: 2px;
            background-color: white;
            transition: width 0.3s ease;
        }

        .nav-link:hover::after {
            width: 100%;
        }

        .balance-card {
            background: linear-gradient(135deg, #10b981, #059669);
            border: none;
            border-radius: 1rem;
            padding: 2rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        .balance-card h3 {
            color: rgba(255, 255, 255, 0.9);
            font-size: 1.25rem;
            margin-bottom: 0.5rem;
        }

        .balance-card h2 {
            color: white;
            font-size: 2.5rem;
            font-weight: 600;
        }

        .card {
            border: none;
            border-radius: 1rem;
            background: white;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
            transition: var(--card-hover-transition);
            height: 100%;
        }

        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
        }

        .card-icon {
            font-size: 2.5rem;
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin-bottom: 1rem;
        }

        .btn-custom {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            border: none;
            border-radius: 0.5rem;
            padding: 0.75rem 1.5rem;
            color: white;
            font-weight: 500;
            transition: all 0.3s ease;
        }

        .btn-custom:hover {
            background: linear-gradient(135deg, var(--secondary-color), var(--primary-color));
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .modal-content {
            border-radius: 1rem;
            border: none;
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
        }

        .modal-header {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            color: white;
            border-radius: 1rem 1rem 0 0;
            padding: 1.5rem;
        }

        .modal-body {
            padding: 2rem;
        }

        .form-control {
            border-radius: 0.5rem;
            padding: 0.75rem;
            border: 1px solid #e2e8f0;
            transition: all 0.3s ease;
        }

        .form-control:focus {
            border-color: var(--accent-color);
            box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.2);
        }

        .table {
            border-radius: 0.5rem;
            overflow: hidden;
        }

        .table thead th {
            background-color: #f8fafc;
            border-bottom: 2px solid #e2e8f0;
            padding: 1rem;
            font-weight: 600;
        }

        .table td {
            padding: 1rem;
            vertical-align: middle;
        }

        /* Animation pour les cartes */
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .row > div {
            animation: fadeInUp 0.6s ease-out forwards;
            opacity: 0;
        }

        .row > div:nth-child(1) { animation-delay: 0.1s; }
        .row > div:nth-child(2) { animation-delay: 0.2s; }
        .row > div:nth-child(3) { animation-delay: 0.3s; }
        .row > div:nth-child(4) { animation-delay: 0.4s; }
        .row > div:nth-child(5) { animation-delay: 0.5s; }
        .row > div:nth-child(6) { animation-delay: 0.6s; }

        /* Style pour les notifications */
        .notification {
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 1rem 2rem;
            border-radius: 0.5rem;
            background: white;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            z-index: 1000;
            animation: slideIn 0.3s ease-out forwards;
        }

        @keyframes slideIn {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }

        /* Loading spinner */
        .loading-spinner {
            width: 40px;
            height: 40px;
            border: 4px solid #f3f3f3;
            border-top: 4px solid var(--primary-color);
            border-radius: 50%;
            animation: spin 1s linear infinite;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        .code-display {
    animation: fadeIn 0.5s ease-out;
}

.btn-custom {
    background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
    border: none;
    border-radius: 0.5rem;
    padding: 0.75rem 1.5rem;
    color: white;
    font-weight: 500;
    transition: all 0.3s ease;
}

@keyframes fadeIn {
    from { opacity: 0; transform: translateY(-10px); }
    to { opacity: 1; transform: translateY(0); }
}
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
            <a class="navbar-brand" href="#">
                <i class="fas fa-exchange-alt me-2"></i>
                GOLD
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="parametres.jsp"><i class="fas fa-cog"></i> Paramètres</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="<%= request.getContextPath() %>/LogoutClientServlet" id="logoutBtn"><i class="fas fa-sign-out-alt"></i> Se déconnecter</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container mt-5">
        <div class="row mb-4">
            <div class="col-md-6">
                <h1>Bienvenue, <%= prenom %> <%= nom %></h1>
            </div>
            <div class="col-md-6">
                <div class="balance-card p-3 text-end">
                    <h3>Solde actuel</h3>
                    <h2 id="soldeActuel"><%= String.format("%,.2f", solde) %> FCFA</h2>
                </div>
            </div>
        </div>

        <!-- Cards for Transfert immédiat, historique, and automatique -->
        <div class="row mt-4">
            <!-- Card for Transfert immédiat -->
            <div class="col-md-4 mb-4">
                <div class="card card-hover text-center p-4">
                    <div class="card-body">
                        <i class="fas fa-exchange-alt card-icon"></i>
                        <h5 class="card-title mt-3">Transfert immédiat</h5>
                        <p class="card-text">Effectuez un transfert instantané.</p>
                        <button class="btn btn-custom" onclick="openModal('transfertImmediatModal')">Transférer</button>
                    </div>
                </div>
            </div>

            <!-- Card for transaction history -->
            <div class="col-md-4 mb-4">
                <div class="card card-hover text-center p-4">
                    <div class="card-body">
                        <i class="fas fa-history card-icon"></i>
                        <h5 class="card-title mt-3">Historique des transactions</h5>
                        <p class="card-text">Voir toutes vos transactions.</p>
                        <button class="btn btn-custom" onclick="openModal('historiqueModal')">Voir l'historique</button>
                    </div>
                </div>
            </div>

            <!-- Card for Transfert automatique -->
            <div class="col-md-4 mb-4">
                <div class="card card-hover text-center p-4">
                    <div class="card-body">
                        <i class="fas fa-calendar-alt card-icon"></i>
                        <h5 class="card-title mt-3">Transfert automatique</h5>
                        <p class="card-text">Configurez des transferts automatiques.</p>
                        <button class="btn btn-custom" onclick="openModal('transfertAutomatiqueModal')">Configurer</button>
                    </div>
                </div>
            </div>
            
        </div>
        
        <div class="row mt-4">
    <!-- Card for annuler les transactions -->
    <div class="col-md-4 mb-4">
        <div class="card card-hover text-center p-4">
            <div class="card-body">
                <i class="fas fa-ban card-icon"></i>
                <h5 class="card-title mt-3">Annuler Transaction</h5>
                <p class="card-text">Annulez vos transactions récentes (moins de 3 jours).</p>
                <button class="btn btn-custom" onclick="openModal('annulerTransactionModal')">Voir les transactions annulables</button>
            </div>
        </div>
    </div>
    
    
         <!-- Correction dans la partie du bouton de retrait -->
<div class="col-md-4 mb-4">
    <div class="card card-hover text-center p-4">
        <div class="card-body">
            <i class="fas fa-money-bill-wave card-icon"></i>
            <h5 class="card-title mt-3">Effectuer un Retrait</h5>
            <p class="card-text">Générez un code pour retirer de l'argent</p>
            <button class="btn btn-custom" data-bs-toggle="modal" data-bs-target="#codeRetraitModal">Retirer</button>
        </div>
    </div>
</div>
        
        <div class="col-md-4 mb-4">
            <div class="card card-hover text-center p-4" onclick="location.href='epargne.jsp';" style="cursor: pointer;">
                <div class="card-body">
                    <i class="fas fa-piggy-bank card-icon"></i>
                    <h5 class="card-title mt-3">Gestion d'Épargne</h5>
                    <p class="card-text">Gérez vos comptes d'épargne et vos objectifs financiers.</p>
                    <button class="btn btn-custom">Voir Épargne</button>
                </div>
            </div>
        </div>
        
        <div class="col-md-4 mb-4">
            <div class="card card-hover text-center p-4" onclick="location.href='transactiondifferer.jsp';" style="cursor: pointer;">
                <div class="card-body">
                    <i class="fas fa-piggy-bank card-icon"></i>
                    <h5 class="card-title mt-3">tranfert differer</h5>
                  
                    <button class="btn btn-custom">programmer</button>
                </div>
            </div>
        </div>
    
</div>


     
    </div>
    <!-- Card for Retrait -->
    <div class="container mt-5">
    <div class="row justify-content-center">
        
    </div>
</div>
    

    <!-- Modal pour Transfert immédiat -->
    <div class="modal fade" id="transfertImmediatModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Transfert immédiat</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="transfertImmediatForm">
                        <div class="mb-3">
                            <label for="numeroDestinataire" class="form-label">Numéro de destinataire</label>
                            <input type="text" class="form-control" id="numeroDestinataire" required>
                        </div>
                        <div class="mb-3">
                            <label for="montant" class="form-label">Montant à transférer</label>
                            <input type="number" class="form-control" id="montant" required>
                        </div>
                        <div class="mb-3">
                            <label for="commission" class="form-label">Commission (1%)</label>
                            <input type="text" class="form-control" id="commission" readonly>
                        </div>
                        <div class="mb-3">
                            <label for="montantTotal" class="form-label">Montant total (Montant + Commission)</label>
                            <input type="text" class="form-control" id="montantTotal" readonly>
                        </div>
                        <button type="submit" class="btn btn-primary">Effectuer le transfert</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal pour l'historique des transactions -->
   <div class="modal fade" id="historiqueModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Historique des transactions</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Montant</th>
                            <th>Type d'opération</th>
                            <th>Téléphone destinataire</th>
                            <th>Statut</th>
                            <th>Date et Heure</th>
                        </tr>
                    </thead>
                    <tbody id="historiqueTableBody">
                        <!-- Transactions will be loaded here via AJAX -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

    <!-- Modal pour Transfert automatique -->
    <div class="modal fade" id="transfertAutomatiqueModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Transfert Automatique</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="transfertAutomatiqueForm">
                        <div class="mb-3">
                            <label for="numeroDestinataireAuto" class="form-label">Numéro de destinataire</label>
                            <input type="text" class="form-control" id="numeroDestinataireAuto" required>
                        </div>
                        <div class="mb-3">
                            <label for="montantAuto" class="form-label">Montant à transférer</label>
                            <input type="number" class="form-control" id="montantAuto" required>
                        </div>
                        <div class="mb-3">
                            <label for="commissionAuto" class="form-label">Commission (1%)</label>
                            <input type="text" class="form-control" id="commissionAuto" readonly>
                        </div>
                        <div class="mb-3">
                            <label for="montantTotalAuto" class="form-label">Montant total (Montant + Commission)</label>
                            <input type="text" class="form-control" id="montantTotalAuto" readonly>
                        </div>
                        <div class="mb-3">
                            <label for="frequence" class="form-label">Fréquence du transfert</label>
                            <select class="form-control" id="frequence" required>
                                <option value="quotidienne">Quotidienne</option>
                                <option value="hebdomadaire">Hebdomadaire</option>
                                <option value="mensuelle">Mensuelle</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="dateDebut" class="form-label">Date de début</label>
                            <input type="date" class="form-control" id="dateDebut" required>
                        </div>
                        <button type="submit" class="btn btn-primary">Configurer le transfert automatique</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
   
   
  <!-- Modal pour Annuler Transaction -->
<div class="modal fade" id="annulerTransactionModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Transactions Annulables</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <table class="table table-striped">
                    <thead>
                        <tr>
                              <th>ID</th>
                               <th>Type</th>
                                <th>Montant</th>
                                <th>Date</th>
                                <th>Téléphone Destinataire</th>
                                <th>Action</th>
                        </tr>
                    </thead>
                    <tbody id="annulableTransactionsTableBody">
                        <!-- Transactions annulables seront chargées ici via AJAX -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>


<!-- Correction du modal de retrait -->
<div class="modal fade" id="codeRetraitModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Générer un Code de Retrait</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="retraitForm">
                    <div class="mb-3">
                        <label for="montantRetrait" class="form-label">Montant à retirer</label>
                        <input type="number" class="form-control" id="montantRetrait" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Solde disponible</label>
                        <p class="h5 text-primary" id="soldeActuelModal"><%= String.format("%,.2f", solde) %> FCFA</p>
                    </div>
                    <input type="hidden" id="idCompteModal" value="<%= idCompte %>">
                    <button type="submit" class="btn btn-custom w-100">Générer le Code</button>
                </form>
                
                <div id="codeRetraitContainer" class="mt-4" style="display: none;">
                    <div class="card bg-light">
                        <div class="card-body text-center">
                            <h6 class="text-success mb-3">Code de retrait généré avec succès !</h6>
                            <div class="code-display p-3 mb-3" style="background: #f8f9fa; border-radius: 8px; border: 2px dashed #6c757d;">
                                <span id="codeRetraitValue" class="h3 font-monospace text-primary"></span>
                            </div>
                            <p class="small text-muted mb-0">Conservez ce code précieusement</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


  
    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    
    <script>
        function openModal(modalId) {
            $('#' + modalId).modal('show');
        }

        $(document).ready(function() {
            // Calculer et mettre à jour la commission et le montant total lors de la saisie du montant
            $('#montant').on('input', function() {
                var montant = parseFloat($('#montant').val()) || 0;
                var commission = montant * 0.01;
                var montantTotal = montant + commission;

                $('#commission').val(commission.toFixed(2) + ' FCFA');
                $('#montantTotal').val(montantTotal.toFixed(2) + ' FCFA');
            });

            // Soumission du formulaire de transfert immédiat
            $('#transfertImmediatForm').submit(function(e) {
                e.preventDefault();
                
                var montant = parseFloat($('#montant').val());
                var numeroDestinataire = $('#numeroDestinataire').val();

                // Effectuer l'appel AJAX pour le transfert
                $.ajax({
                    url: '<%= request.getContextPath() %>/operations',
                    type: 'POST',
                    data: {
                        action: 'transfert_immediat',
                        idCompteSource: '<%= idCompte %>',
                        numeroDestinataire: numeroDestinataire,
                        montant: montant
                    },
                    success: function(response) {
                        if (response.status === 'success') {
                            alert('Transfert effectué avec succès!');
                            $('#transfertImmediatModal').modal('hide');

                            // Mettre à jour le solde sur la page sans recharger
                            var soldeActuel = parseFloat($('#soldeActuel').text().replace(/,/g, '').replace(' FCFA', ''));
                            var commission = montant * 0.01;
                            var montantTotal = montant + commission;

                            var nouveauSolde = soldeActuel - montantTotal;
                            $('#soldeActuel').text(nouveauSolde.toFixed(2) + ' FCFA');
                        } else {
                            alert('Erreur lors du transfert : ' + response.message);
                        }
                    },
                    error: function() {
                        alert('Une erreur est survenue. Veuillez réessayer.');
                    }
                });
            });

            // AJAX pour récupérer l'historique des transactions
           $('#historiqueModal').on('show.bs.modal', function() {
    var idCompte = <%= idCompte %>;

    $.ajax({
        url: '<%= request.getContextPath() %>/operations',
        type: 'GET',
        data: {
            action: 'historique',
            idCompte: idCompte
        },
        success: function(response) {
            if (response.status === 'success') {
                var historiqueTableBody = $('#historiqueTableBody');
                historiqueTableBody.empty();

                response.transactions.forEach(function(transaction) {
                    var row = '<tr>' +
                        '<td>' + transaction.id + '</td>' +
                        '<td>' + transaction.montant + '</td>' +
                        '<td>' + transaction.typeOperation + '</td>' +
                        '<td>' + transaction.telephoneDestinataire + '</td>' +
                        '<td>' + transaction.statut + '</td>' +
                        '<td>' + transaction.date + ' ' + transaction.heure + '</td>' +
                        '</tr>';
                    historiqueTableBody.append(row);
                });
            } else {
                alert('Erreur lors du chargement de l\'historique : ' + response.message);
            }
        },
        error: function() {
            alert('Une erreur est survenue lors du chargement de l\'historique.');
        }
    });
});

            // Calculer et mettre à jour la commission et le montant total pour le transfert automatique
            $('#montantAuto').on('input', function() {
                var montant = parseFloat($('#montantAuto').val()) || 0;
                var commission = montant * 0.01;
                var montantTotal = montant + commission;

                $('#commissionAuto').val(commission.toFixed(2) + ' FCFA');
                $('#montantTotalAuto').val(montantTotal.toFixed(2) + ' FCFA');
            });

            // Soumission du formulaire de transfert automatique
            $('#transfertAutomatiqueForm').submit(function(e) {
                e.preventDefault();
                
                var montant = parseFloat($('#montantAuto').val());
                var numeroDestinataire = $('#numeroDestinataireAuto').val();
                var frequence = $('#frequence').val();
                var dateDebut = $('#dateDebut').val();

                if (isNaN(montant) || montant <= 0) {
                    alert("Veuillez entrer un montant valide.");
                    return;
                }

                // Effectuer l'appel AJAX pour le transfert automatique
                $.ajax({
                    url: '<%= request.getContextPath() %>/operations',
                    type: 'POST',
                    data: {
                        action: 'transfert_automatique',
                        idCompteSource: '<%= idCompte %>',
                        numeroDestinataire: numeroDestinataire,
                        montant: montant,
                        frequence: frequence,
                        dateDebut: dateDebut
                    },
                    success: function(response) {
                        if (response.status === 'success') {
                            alert('Transfert automatique configuré avec succès!');
                            $('#transfertAutomatiqueModal').modal('hide');
                        } else {
                            alert('Erreur lors de la configuration du transfert : ' + response.message);
                        }
                    },
                    error: function() {
                        alert('Une erreur est survenue. Veuillez réessayer.');
                    }
                });
            });
        });
        
        $(document).ready(function() {
            // Ouvrir le modal pour annuler les transactions
            $('#annulerTransactionModal').on('show.bs.modal', function() {
                var idCompte = '<%= idCompte %>'; // Récupérer l'ID du compte

                // Appel AJAX pour récupérer les transactions annulables
                $.ajax({
                    url: '<%= request.getContextPath() %>/operations',
                    type: 'GET',
                    data: {
                        action: 'transactions_annulables',
                        idCompte: idCompte
                    },
                    success: function(response) {
                        if (response.status === 'success') {
                            var annulableTransactionsTableBody = $('#annulableTransactionsTableBody');
                            annulableTransactionsTableBody.empty(); // Vider le tableau avant de remplir

                            response.transactions.forEach(function(transaction) {
                                var row = '<tr>' +
                                          '<td>' + transaction.id + '</td>' +
                                          '<td>' + transaction.type + '</td>' + // Ajout du type
                                          '<td>' + transaction.montant.toFixed(2) + ' fcf</td>' +
                                          '<td>' + transaction.date + '</td>' +
                                          '<td>' + transaction.telephoneDestinataire + '</td>' + 
                                          '<td><button class="btn btn-danger" onclick="annulerTransaction(' + transaction.id + ', ' + transaction.montant + ')">Annuler</button></td>' +
                                          '</tr>';
                                annulableTransactionsTableBody.append(row);
                            });
                        } else {
                            alert('Erreur lors du chargement des transactions annulables : ' + response.message);
                        }
                    },
                    error: function() {
                        alert('Erreur lors de la récupération des transactions annulables.');
                    }
                });
            });

            // Fonction pour annuler une transaction
            window.annulerTransaction = function(idTransaction, montant) {
                if (confirm('Voulez-vous vraiment annuler cette transaction ?')) {
                    // Appel AJAX pour annuler la transaction
                    $.ajax({
                        url: '<%= request.getContextPath() %>/operations',
                        type: 'POST',
                        data: {
                            action: 'annuler_transaction',
                            idTransaction: idTransaction
                        },
                        success: function(response) {
                            if (response.status === 'success') {
                                alert('Transaction annulée avec succès!');

                                // Fermer le modal
                                $('#annulerTransactionModal').modal('hide');

                                // Mettre à jour le solde sur la page sans recharger
                                var soldeActuel = parseFloat($('#soldeActuel').text().replace(/,/g, '').replace(' FCFA', ''));
                                var nouveauSolde = soldeActuel + montant;
                                $('#soldeActuel').text(nouveauSolde.toFixed(2) + ' FCFA');
                            } else {
                                alert('Erreur lors de l\'annulation de la transaction : ' + response.message);
                            }
                        },
                        error: function() {
                            alert('Une erreur est survenue lors de l\'annulation de la transaction.');
                        }
                    });
                }
            };
        });
        
        
       
        $(document).ready(function() {
            $('#retraitForm').submit(function(e) {
                e.preventDefault();

                var montant = parseFloat($('#montantRetrait').val());
                var solde = parseFloat($('#soldeActuelModal').text().replace(/[^0-9.-]+/g, ''));
                var idCompte = $('#idCompteModal').val();

                // Validation du montant
                if (isNaN(montant) || montant <= 0) {
                    // Création d'une alerte stylisée pour montant invalide
                    const alertDiv = $('<div>')
                        .addClass('alert alert-danger alert-dismissible fade show mt-3')
                        .attr('role', 'alert')
                        .html(`
                            <strong>Erreur!</strong> Veuillez entrer un montant valide.
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        `);
                    $('#retraitForm').prepend(alertDiv);
                    return;
                }

                // Validation du solde
                if (montant > solde) {
                    // Création d'une alerte stylisée pour solde insuffisant
                    const alertDiv = $('<div>')
                        .addClass('alert alert-danger alert-dismissible fade show mt-3')
                        .attr('role', 'alert')
                        .html(`
                            <strong>Solde insuffisant!</strong> Votre solde actuel (${solde.toFixed(2)} FCFA) ne permet pas d'effectuer un retrait de ${montant.toFixed(2)} FCFA.
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        `);
                    $('#retraitForm').prepend(alertDiv);
                    return;
                }

                $.ajax({
                    url: '<%= request.getContextPath() %>/gold/client/retrait',
                    type: 'POST',
                    data: {
                        action: 'generer_code',
                        montant: montant,
                        idCompte: idCompte
                    },
                    success: function(response) {
                        if (response.status === 'success') {
                            // Suppression des alertes précédentes
                            $('.alert').remove();
                            
                            $('#codeRetraitValue').text(response.codeRetrait);
                            $('#codeRetraitContainer').slideDown();
                            
                            // Mise à jour du solde
                            var nouveauSolde = solde - montant;
                            $('#soldeActuel').text(nouveauSolde.toFixed(2) + ' FCFA');
                            $('#soldeActuelModal').text(nouveauSolde.toFixed(2) + ' FCFA');
                        } else {
                            // Création d'une alerte stylisée pour les erreurs de génération de code
                            const alertDiv = $('<div>')
                                .addClass('alert alert-danger alert-dismissible fade show mt-3')
                                .attr('role', 'alert')
                                .html(`
                                    <strong>Erreur!</strong> ${response.message}
                                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                                `);
                            $('#retraitForm').prepend(alertDiv);
                        }
                    },
                    error: function(xhr, status, error) {
                        console.error('Erreur AJAX:', error);
                        // Création d'une alerte stylisée pour les erreurs AJAX
                        const alertDiv = $('<div>')
                            .addClass('alert alert-danger alert-dismissible fade show mt-3')
                            .attr('role', 'alert')
                            .html(`
                                <strong>Erreur!</strong> Une erreur est survenue. Veuillez réessayer.
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            `);
                        $('#retraitForm').prepend(alertDiv);
                    }
                });
            });

            // Nettoyage du formulaire à l'ouverture du modal
            $('#codeRetraitModal').on('show.bs.modal', function() {
                $('#codeRetraitContainer').hide();
                $('#montantRetrait').val('');
                $('.alert').remove(); // Suppression des alertes précédentes
                var soldeActuel = $('#soldeActuel').text();
                $('#soldeActuelModal').text(soldeActuel);
            });
        });
     
    </script>
    <div id="chatbot-toggle">
        <i class="fas fa-comments" style="color: #2C3E50; font-size: 24px;"></i>
    </div>

    <div id="chatbot-container">
        <div id="chatbot-header">
            <div class="header-title">
                <img src="/api/placeholder/30/30" alt="Assistant GOLD">
                <span>Assistant GOLD</span>
            </div>
            <i class="fas fa-times" id="chatbot-close" style="cursor: pointer; color: #2C3E50;"></i>
        </div>
        <div id="chatbot-messages"></div>
        <div id="chatbot-input-container">
            <div class="input-wrapper">
                <input type="text" id="chatbot-input" placeholder="Comment puis-je vous aider ?">
                <button id="chatbot-submit">
                    <i class="fas fa-paper-plane"></i>
                </button>
            </div>
        </div>
    </div>

    <script>
    class AdvancedChatbot {
        constructor() {
            this.container = document.getElementById('chatbot-container');
            this.messagesContainer = document.getElementById('chatbot-messages');
            this.input = document.getElementById('chatbot-input');
            this.submitButton = document.getElementById('chatbot-submit');
            this.toggleButton = document.getElementById('chatbot-toggle');
            this.closeButton = document.getElementById('chatbot-close');
            this.context = [];
            this.setupEventListeners();
            this.sendWelcomeMessage();
        }

        setupEventListeners() {
            this.submitButton.addEventListener('click', () => this.handleUserInput());
            this.input.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') this.handleUserInput();
            });
            this.toggleButton.addEventListener('click', () => this.toggleChatbot());
            this.closeButton.addEventListener('click', () => this.toggleChatbot());

            // Ajout de l'effet de focus sur l'input
            this.input.addEventListener('focus', () => {
                this.input.parentElement.style.boxShadow = '0 0 0 2px var(--primary-color)';
            });
            this.input.addEventListener('blur', () => {
                this.input.parentElement.style.boxShadow = '0 2px 5px var(--shadow-color)';
            });
        }

        toggleChatbot() {
            const isVisible = this.container.style.display === 'flex';
            this.container.style.display = isVisible ? 'none' : 'flex';
            this.toggleButton.style.display = isVisible ? 'flex' : 'none';
            
            if (!isVisible) {
                this.input.focus();
                this.messagesContainer.scrollTop = this.messagesContainer.scrollHeight;
            }
        }

        async sendWelcomeMessage() {
            await this.sleep(500);
            this.addMessage({
                text: "👋 Bonjour ! Je suis l'assistant virtuel GOLD, spécialisé dans la gestion de vos finances.",
                sender: 'bot'
            });
            await this.sleep(500);
            this.addMessage({
                text: "Je peux vous aider avec vos transactions, consultation de solde, et bien plus encore !",
                sender: 'bot',
                quickReplies: [
                    '💸 Faire un transfert',
                    '💰 Consulter mon solde',
                    '🏧 Effectuer un retrait',
                    '📊 Voir mon historique'
                ]
            });
        }

        async handleUserInput() {
            const userInput = this.input.value.trim();
            if (!userInput) return;

            this.addMessage({ text: userInput, sender: 'user' });
            this.input.value = '';
            this.showTypingIndicator();

            // Ajouter le message au contexte
            this.context.push({ role: 'user', message: userInput });

            // Simuler le traitement et la réponse
            await this.sleep(1000);
            this.removeTypingIndicator();

            const response = this.generateResponse(userInput);
            this.addMessage({
                text: response.message,
                sender: 'bot',
                quickReplies: response.quickReplies
            });

            // Ajouter la réponse au contexte
            this.context.push({ role: 'assistant', message: response.message });
        }

        generateResponse(userInput) {
            const normalizedInput = userInput.toLowerCase();
            const responses = {
                transfert: {
                    message: "Pour effectuer un transfert en toute sécurité :\n\n" +
                            "1️⃣ Choisissez 'Transfert immédiat'\n" +
                            "2️⃣ Entrez le numéro du destinataire\n" +
                            "3️⃣ Spécifiez le montant\n\n" +
                            "💡 La commission est de 1% avec un minimum de 10 FCFA",
                    quickReplies: ['💰 Voir mon solde', '📋 Voir les frais', '🔄 Transfert automatique']
                },
                solde: {
                    message: "💰 Votre solde actuel est afficher en haut de la page\n\n" +
                            "Dernière mise à jour : " + new Date().toLocaleTimeString(),
                    quickReplies: ['💸 Faire un transfert', '🏧 Effectuer un retrait']
                },
                retrait: {
                    message: "Pour retirer de l'argent sans carte :\n\n" +
                            "1️⃣ Sélectionnez 'Retrait sans carte'\n" +
                            "2️⃣ Choisissez le montant\n" +
                            "3️⃣ Notez le code à 6 chiffres\n\n" +
                            "⏱️ Le code est valable 15 minutes",
                    quickReplies: ['🎲 Générer un code', '💰 Voir mon solde']
                },
                aide: {
                    message: "Je suis là pour vous aider ! Voici ce que je peux faire :\n\n" +
                            "💸 Transferts d'argent\n" +
                            "💰 Consultation de solde\n" +
                            "🏧 Retraits sans carte\n" +
                            "📊 Historique des transactions\n\n" +
                            "Que souhaitez-vous faire ?",
                    quickReplies: ['💸 Transfert', '💰 Solde', '🏧 Retrait', '📊 Historique']
                }
            };

            // Analyse contextuelle simple
            if (normalizedInput.includes('transfert')) return responses.transfert;
            if (normalizedInput.includes('solde')) return responses.solde;
            if (normalizedInput.includes('retrait')) return responses.retrait;
            if (normalizedInput.includes('aide')) return responses.aide;

            return {
                message: "Je ne suis pas sûr de comprendre. Puis-je vous suggérer ces options ?",
                quickReplies: ['❓ Aide', '💸 Transfert', '💰 Solde', '🏧 Retrait']
            };
        }

        addMessage({ text, sender, quickReplies = null }) {
            const messageElement = document.createElement('div');
            messageElement.classList.add('message', `${sender}-message`);
            messageElement.textContent = text;
            this.messagesContainer.appendChild(messageElement);

            if (quickReplies) {
                this.addQuickReplies(quickReplies);
            }
            if (quickReplies) {
                this.addQuickReplies(quickReplies);
            }

            this.scrollToBottom();
        }

        addQuickReplies(replies) {
            const quickRepliesContainer = document.createElement('div');
            quickRepliesContainer.classList.add('quick-replies');
            
            replies.forEach(reply => {
                const button = document.createElement('button');
                button.classList.add('quick-reply');
                button.textContent = reply;
                button.addEventListener('click', () => {
                    this.input.value = reply;
                    this.handleUserInput();
                });
                
                // Ajout d'effet de hover avec ripple
                button.addEventListener('mouseenter', (e) => {
                    const ripple = document.createElement('div');
                    ripple.classList.add('ripple');
                    button.appendChild(ripple);
                    setTimeout(() => ripple.remove(), 1000);
                });
                
                quickRepliesContainer.appendChild(button);
            });
            
            this.messagesContainer.appendChild(quickRepliesContainer);
            this.scrollToBottom();
        }

        showTypingIndicator() {
            const indicator = document.createElement('div');
            indicator.classList.add('typing-indicator');
            indicator.innerHTML = `
                <div class="typing-dot"></div>
                <div class="typing-dot"></div>
                <div class="typing-dot"></div>
            `;
            this.messagesContainer.appendChild(indicator);
            this.scrollToBottom();
        }

        removeTypingIndicator() {
            const indicator = this.messagesContainer.querySelector('.typing-indicator');
            if (indicator) {
                indicator.remove();
            }
        }

        scrollToBottom() {
            this.messagesContainer.scrollTo({
                top: this.messagesContainer.scrollHeight,
                behavior: 'smooth'
            });
        }

        sleep(ms) {
            return new Promise(resolve => setTimeout(resolve, ms));
        }

        // Nouvelle fonction pour ajouter des animations aux messages
        async animateMessage(messageElement) {
            messageElement.style.opacity = '0';
            messageElement.style.transform = 'translateY(20px)';
            await this.sleep(100);
            messageElement.style.transition = 'all 0.3s ease';
            messageElement.style.opacity = '1';
            messageElement.style.transform = 'translateY(0)';
        }

        // Nouvelle fonction pour la reconnaissance vocale
        setupVoiceRecognition() {
            if ('webkitSpeechRecognition' in window) {
                const recognition = new webkitSpeechRecognition();
                recognition.continuous = false;
                recognition.interimResults = false;
                recognition.lang = 'fr-FR';

                const voiceButton = document.createElement('button');
                voiceButton.innerHTML = '<i class="fas fa-microphone"></i>';
                voiceButton.classList.add('voice-input-button');
                this.input.parentElement.insertBefore(voiceButton, this.submitButton);

                voiceButton.addEventListener('click', () => {
                    recognition.start();
                    voiceButton.classList.add('listening');
                });

                recognition.onresult = (event) => {
                    const transcript = event.results[0][0].transcript;
                    this.input.value = transcript;
                    voiceButton.classList.remove('listening');
                };

                recognition.onend = () => {
                    voiceButton.classList.remove('listening');
                };
            }
        }

        // Nouvelle fonction pour gérer les fichiers
        setupFileUpload() {
            const fileButton = document.createElement('button');
            fileButton.innerHTML = '<i class="fas fa-paperclip"></i>';
            fileButton.classList.add('file-upload-button');
            this.input.parentElement.insertBefore(fileButton, this.submitButton);

            const fileInput = document.createElement('input');
            fileInput.type = 'file';
            fileInput.style.display = 'none';
            fileButton.parentElement.appendChild(fileInput);

            fileButton.addEventListener('click', () => {
                fileInput.click();
            });

            fileInput.addEventListener('change', (e) => {
                const file = e.target.files[0];
                if (file) {
                    this.handleFileUpload(file);
                }
            });
        }

        // Gestion des fichiers uploadés
        async handleFileUpload(file) {
            const maxSize = 5 * 1024 * 1024; // 5MB
            if (file.size > maxSize) {
                this.addMessage({
                    text: "⚠️ Le fichier est trop volumineux. La taille maximale est de 5MB.",
                    sender: 'bot'
                });
                return;
            }

            const allowedTypes = ['image/jpeg', 'image/png', 'application/pdf'];
            if (!allowedTypes.includes(file.type)) {
                this.addMessage({
                    text: "⚠️ Format de fichier non supporté. Formats acceptés : JPG, PNG, PDF",
                    sender: 'bot'
                });
                return;
            }

            // Simuler l'envoi du fichier
            this.addMessage({
                text: `📎 ${file.name}`,
                sender: 'user'
            });

            this.showTypingIndicator();
            await this.sleep(1500);
            this.removeTypingIndicator();

            this.addMessage({
                text: "✅ J'ai bien reçu votre fichier. Je l'analyse...",
                sender: 'bot',
                quickReplies: ['📤 Envoyer un autre fichier', '❌ Annuler']
            });
        }
    }

    // Initialisation du chatbot avec nouvelles fonctionnalités
    document.addEventListener('DOMContentLoaded', () => {
        const chatbot = new AdvancedChatbot();
        chatbot.setupVoiceRecognition();
        chatbot.setupFileUpload();
    });
    </script>
   
    </body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // Vérification de session pour l'accès à la page
    if (session == null || session.getAttribute("utilisateur") == null) {
        response.sendRedirect("utilisateur/connexion.jsp?error=session_expired");
        return;
    }
    String utilisateurNom = ((metier.Utilisateur) session.getAttribute("utilisateur")).getNom();
    String utilisateurPrenom = ((metier.Utilisateur) session.getAttribute("utilisateur")).getPrenom();
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Utilisateurs</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <style>
        .user-type-card {
            cursor: pointer;
            text-align: center;
            padding: 20px;
            margin-bottom: 20px;
            background-color: #f8f9fa;
            border: 1px solid #ddd;
            transition: all 0.3s ease;
            border-radius: 15px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        .user-type-card:hover {
            background-color: #e9ecef;
            transform: translateY(-5px);
            box-shadow: 0 6px 12px rgba(0,0,0,0.15);
        }
        .user-type-icon {
            font-size: 3.5rem;
            margin-bottom: 15px;
            transition: all 0.3s ease;
        }
        .user-type-card:hover .user-type-icon {
            transform: scale(1.1);
        }
        .btn-action {
            width: 40px;
            height: 40px;
            padding: 0;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            margin: 0 5px;
            transition: all 0.3s ease;
        }
        .btn-action:hover {
            transform: scale(1.1);
        }
        .modal-content {
            border-radius: 20px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
        }
        .modal-header {
            background-color: #007bff;
            color: white;
            border-top-left-radius: 20px;
            border-top-right-radius: 20px;
        }
        .close {
            color: white;
            opacity: 1;
        }
        .close:hover {
            color: #f8f9fa;
        }
        #searchInput {
            border-radius: 20px;
            padding-left: 40px;
        }
        .search-icon {
            position: absolute;
            left: 15px;
            top: 10px;
            color: #6c757d;
        }
    </style>
</head>
<body>

<div class="container py-5">
    <!-- Informations de l'administrateur connecté -->
    <div class="text-end mb-3">
        <span>Bienvenue, <b><%= utilisateurPrenom %> <%= utilisateurNom %></b></span>
        <a href="/gold/LogoutServlet" class="btn btn-danger ms-3"><i class="fas fa-sign-out-alt"></i> Déconnexion</a>
    </div>

    <h1 class="text-center mb-5">
        <i class="fas fa-users-cog me-3"></i>Gestion des Utilisateurs
    </h1>

    <div class="row mb-4">
        <!-- User type cards -->
        <div class="col-md-3">
            <div class="card user-type-card" onclick="afficherUtilisateurs('clients')">
                <i class="fas fa-user user-type-icon text-primary"></i>
                <h4>Clients</h4>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card user-type-card" onclick="afficherUtilisateurs('personnels')">
                <i class="fas fa-user-tie user-type-icon text-success"></i>
                <h4>Personnel</h4>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card user-type-card" onclick="afficherUtilisateurs('agentsKiosque')">
                <i class="fas fa-store user-type-icon text-warning"></i>
                <h4>Agents Kiosque</h4>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card user-type-card" data-bs-toggle="modal" data-bs-target="#addUserModal">
                <i class="fas fa-user-plus user-type-icon text-info"></i>
                <h4>Ajouter un utilisateur</h4>
            </div>
        </div>
    </div>

    <div id="userListSection" style="display: none;">
        <h2 id="userListTitle" class="mb-4"></h2>
        <div class="mb-4 position-relative">
            <i class="fas fa-search search-icon"></i>
            <input type="text" id="searchInput" class="form-control" placeholder="Rechercher un utilisateur...">
        </div>
        <table class="table table-hover">
            <thead>
                <tr>
                    <th>Nom</th>
                    <th>Prénom</th>
                    <th>Email</th>
                    <th>Téléphone</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody id="userTableBody">
                <!-- Les utilisateurs seront insérés ici dynamiquement -->
            </tbody>
        </table>
    </div>
</div>

<!-- Modal pour ajouter un utilisateur -->
<div class="modal fade" id="addUserModal" tabindex="-1" aria-labelledby="addUserModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addUserModalLabel">Ajouter un Utilisateur</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="addUserForm">
                    <div class="mb-3">
                        <label for="nom" class="form-label">Nom</label>
                        <input type="text" class="form-control" id="nom" name="nom" required>
                    </div>
                    <div class="mb-3">
                        <label for="prenom" class="form-label">Prénom</label>
                        <input type="text" class="form-control" id="prenom" name="prenom" required>
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" id="email" name="email" required>
                    </div>
                    <div class="mb-3">
                        <label for="telephone" class="form-label">Téléphone</label>
                        <input type="tel" class="form-control" id="telephone" name="telephone"
                               pattern="\\d{9}" maxlength="9" placeholder="771234567"
                               title="Le numéro doit comporter exactement 9 chiffres" required>
                    </div>
                    <div class="mb-3">
                        <label for="adresse" class="form-label">Adresse</label>
                        <input type="text" class="form-control" id="adresse" name="adresse" required>
                    </div>
                    <div class="mb-3">
                        <label for="role" class="form-label">Rôle</label>
                        <select class="form-select" id="role" name="role" required>
                            <option value="Personnel">Personnel</option>
                            <option value="AgentKiosque">Agent Kiosque</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="motdepasse" class="form-label">Mot de Passe</label>
                        <input type="password" class="form-control" id="motdepasse" name="motdepasse" required>
                    </div>
                    <div class="mb-3">
                        <label for="codesecret" class="form-label">Code Secret</label>
                        <input type="password" class="form-control" id="codesecret" name="codesecret"
                               required maxlength="4" pattern="\\d{4}" title="Le code secret doit être un nombre de 4 chiffres">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fermer</button>
                <button type="button" class="btn btn-primary" onclick="ajouterUtilisateur()">Ajouter</button>
            </div>
        </div>
    </div>
</div>

<!-- Modal pour modifier un utilisateur -->
<div class="modal fade" id="editUserModal" tabindex="-1" aria-labelledby="editUserModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="editUserModalLabel">Modifier l'utilisateur</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="editUserForm">
                    <input type="hidden" id="editIdUtilisateur" name="idUtilisateur">
                    <div class="mb-3">
                        <label for="editNom" class="form-label">Nom</label>
                        <input type="text" class="form-control" id="editNom" name="nom" required>
                    </div>
                    <div class="mb-3">
                        <label for="editPrenom" class="form-label">Prénom</label>
                        <input type="text" class="form-control" id="editPrenom" name="prenom" required>
                    </div>
                    <div class="mb-3">
                        <label for="editEmail" class="form-label">Email</label>
                        <input type="email" class="form-control" id="editEmail" name="email" required>
                    </div>
                    <div class="mb-3">
                        <label for="editTelephone" class="form-label">Téléphone</label>
                        <input type="tel" class="form-control" id="editTelephone" name="telephone"
                               pattern="\\d{9}" maxlength="9" placeholder="771234567"
                               title="Le numéro doit comporter exactement 9 chiffres" required>
                    </div>
                    <div class="mb-3">
                        <label for="editAdresse" class="form-label">Adresse</label>
                        <input type="text" class="form-control" id="editAdresse" name="adresse" required>
                    </div>
                    <div class="mb-3">
                        <label for="editMotdepasse" class="form-label">Mot de Passe</label>
                        <input type="password" class="form-control" id="editMotdepasse" name="motdepasse" required>
                    </div>
                    <div class="mb-3">
                        <label for="editCodesecret" class="form-label">Code Secret</label>
                        <input type="password" class="form-control" id="editCodesecret" name="codesecret"
                               required maxlength="4" pattern="\\d{4}" title="Le code secret doit être un nombre de 4 chiffres">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fermer</button>
                <button type="button" class="btn btn-primary" onclick="modifierUtilisateur()">Enregistrer les modifications</button>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.min.js"></script>

<script>
    let currentUserType = '';

    // Fonction pour afficher les utilisateurs selon leur rôle
    function afficherUtilisateurs(role) {
        currentUserType = role;
        $.ajax({
            url: '/gold/admin/listerUtilisateurs',
            method: 'GET',
            dataType: 'json',
            success: function(data) {
                let users = data[role];
                let title = '';

                if (role === 'clients') {
                    title = 'Liste des Clients';
                } else if (role === 'personnels') {
                    title = 'Liste du Personnel';
                } else if (role === 'agentsKiosque') {
                    title = 'Liste des Agents Kiosque';
                }

                $('#userListTitle').text(title);
                updateUserTable(users);
                $('#userListSection').show();
            },
            error: function(xhr, status, error) {
                console.error("Erreur lors de la récupération des données:", error);
                alert("Une erreur est survenue lors de la récupération des données.");
            }
        });
    }

    // Fonction pour mettre à jour la table des utilisateurs
    function updateUserTable(users) {
    const userTableBody = $('#userTableBody');
    userTableBody.empty();

    if (users && users.length > 0) {
        $.each(users, function(i, user) {
            let row = $('<tr>');
            row.append($('<td>').text(user.nom));
            row.append($('<td>').text(user.prenom));
            row.append($('<td>').text(user.email));
            row.append($('<td>').text(user.telephone));
            row.append($('<td>').html(`
                <button class="btn btn-warning btn-action" onclick="openEditModal(${user.idUtilisateur})">
                    <i class="fas fa-edit"></i>
                </button>

                <button class="btn btn-danger btn-action" onclick="desactiverUtilisateur(${user.idUtilisateur})">
                    <i class="fas fa-user-slash"></i>
                </button>
            `));
            userTableBody.append(row);
        });
    } else {
        userTableBody.append('<tr><td colspan="5" class="text-center">Aucun utilisateur trouvé</td></tr>');
    }
}

    
    // Fonction pour ajouter un utilisateur
    function ajouterUtilisateur() {
        const formData = new FormData(document.getElementById('addUserForm'));
        const userData = Object.fromEntries(formData.entries());

        $.ajax({
            url: '/gold/admin/ajouterUtilisateur',
            method: 'POST',
            data: userData,
            success: function(response) {
                if (response.success) {
                    alert('Utilisateur ajouté avec succès');
                    $('#addUserModal').modal('hide');
                    afficherUtilisateurs(userData.role.toLowerCase() + 's');  // Ajout du 's' pour correspondre aux clés de l'objet data
                    document.getElementById('addUserForm').reset();  // Réinitialiser le formulaire
                } else if (response.message === 'email_exists') {
                    alert('L\'email existe déjà. Veuillez en choisir un autre.');
                } else {
                    alert('Erreur lors de l\'ajout de l\'utilisateur: ' + response.message);
                }
            },
            error: function(xhr, status, error) {
                console.error("Erreur lors de l'ajout de l'utilisateur:", error);
                alert("Une erreur est survenue lors de l'ajout de l'utilisateur.");
            }
        });
    }

    function openEditModal(idUtilisateur) {
        if (idUtilisateur) {
            $.ajax({
                url: '/gold/admin/utilisateur',
                method: 'GET',
                data: { action: 'getUtilisateur', idUtilisateur: idUtilisateur },
                dataType: 'json',
                success: function(user) {
                    if (user) {
                        $('#editIdUtilisateur').val(user.idUtilisateur);
                        $('#editNom').val(user.nom);
                        $('#editPrenom').val(user.prenom);
                        $('#editEmail').val(user.email);
                        $('#editTelephone').val(user.telephone);
                        $('#editAdresse').val(user.adresse);
                        $('#editMotdepasse').val(user.motdepasse);
                        $('#editCodesecret').val(user.codesecret);
                        $('#editUserModal').modal('show');
                    } else {
                        alert("Utilisateur non trouvé.");
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Erreur lors de la récupération des détails de l'utilisateur:", error);
                    alert("Erreur lors de la récupération des détails de l'utilisateur.");
                }
            });
        } else {
            console.error("ID utilisateur non défini.");
            alert("ID utilisateur non défini.");
        }
    }




    function desactiverUtilisateur(idUtilisateur) {
        console.log("ID utilisateur à désactiver:", idUtilisateur); // Vérifie l'ID utilisateur
        if (idUtilisateur) {
            if (confirm("Êtes-vous sûr de vouloir désactiver cet utilisateur ?")) {
                $.ajax({
                    url: '/gold/admin/utilisateur?action=desactiver',
                    method: 'POST',
                    data: { idUtilisateur: idUtilisateur },
                    success: function(response) {
                        if (response.success) {
                            alert('Utilisateur désactivé avec succès');
                            afficherUtilisateurs(currentUserType); // Recharge la liste après désactivation
                        } else {
                            alert('Erreur lors de la désactivation de l\'utilisateur.');
                        }
                    },
                    error: function(xhr, status, error) {
                        console.error("Erreur lors de la désactivation de l'utilisateur:", error);
                        alert("Erreur lors de la désactivation.");
                    }
                });
            }
        } else {
            console.error("ID utilisateur non défini.");
            alert("ID utilisateur non défini.");
        }
    }


    // Fonction pour modifier un utilisateur
    function modifierUtilisateur() {
        const formData = new FormData(document.getElementById('editUserForm'));
        const userData = Object.fromEntries(formData.entries());

        $.ajax({
            url: '/gold/admin/utilisateur?action=modifier',
            method: 'POST',
            data: userData,
            success: function(response) {
                if (response.success) {
                    alert('Utilisateur modifié avec succès');
                    $('#editUserModal').modal('hide');
                    afficherUtilisateurs(currentUserType);
                } else {
                    alert('Erreur lors de la modification de l\'utilisateur.');
                }
            },
            error: function(xhr, status, error) {
                console.error("Erreur lors de la modification de l'utilisateur:", error);
                alert("Erreur lors de la modification de l'utilisateur.");
            }
        });
    }

    // Fonction de recherche
    $('#searchInput').on('keyup', function() {
        let value = $(this).val().toLowerCase();
        $("#userTableBody tr").filter(function() {
            $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1);
        });
    });
</script>

</body>
</html>
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
    <title>Gestion des Kiosques</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <style>
        #map { height: 400px; }
        .card { margin-bottom: 20px; }
    </style>
</head>
<body>
    <div class="container mt-5">
        <h1 class="text-center mb-4">Gestion des Kiosques</h1>

        <div class="row">
            <!-- Formulaire d'ajout de kiosque -->
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header bg-primary text-white">
                        <h2 class="card-title">Ajouter un nouveau kiosque</h2>
                    </div>
                    <div class="card-body">
                        <form id="addKiosqueForm">
                            <div class="mb-3">
                                <label for="adresse" class="form-label">Adresse</label>
                                <input type="text" class="form-control" id="adresse" name="adresse" required>
                            </div>
                            <div class="mb-3">
                                <label for="latitude" class="form-label">Latitude</label>
                                <input type="number" step="any" class="form-control" id="latitude" name="latitude" required>
                            </div>
                            <div class="mb-3">
                                <label for="longitude" class="form-label">Longitude</label>
                                <input type="number" step="any" class="form-control" id="longitude" name="longitude" required>
                            </div>
                            <div class="mb-3">
                                <label for="etatKiosque" class="form-label">État du Kiosque</label>
                                <select class="form-select" id="etatKiosque" name="etatKiosque" required>
                                    <option value="Actif">Actif</option>
                                    <option value="Inactif">Inactif</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label for="idAgentKiosque" class="form-label">Agent du Kiosque</label>
                                <select class="form-select" id="idAgentKiosque" name="idAgentKiosque" required>
                                    <option value="">Sélectionnez un agent</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label for="horaires" class="form-label">Horaires</label>
                                <input type="text" class="form-control" id="horaires" name="horaires" value="8h - 18h" readonly>
                            </div>
                            <button type="submit" class="btn btn-primary">Ajouter le kiosque</button>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Carte pour la sélection des adresses -->
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header bg-info text-white">
                        <h2 class="card-title">Carte</h2>
                    </div>
                    <div class="card-body">
                        <div class="mb-3">
                            <input type="text" id="searchAddress" class="form-control" placeholder="Entrez une adresse">
                            <button id="searchButton" class="btn btn-secondary mt-2">Rechercher</button>
                        </div>
                        <div id="map"></div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Liste des kiosques et actions -->
        <div class="card mt-4">
            <div class="card-header bg-success text-white">
                <h2 class="card-title">Liste des Kiosques</h2>
            </div>
            <div class="card-body">
                <table class="table table-striped" id="kiosquesTable">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Numéro d'identification</th>
                            <th>État</th>
                            <th>Adresse</th>
                            <th>Agent</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- Les données seront insérées ici dynamiquement -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Modal de modification -->
    <div class="modal fade" id="editKiosqueModal" tabindex="-1" aria-labelledby="editKiosqueModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editKiosqueModalLabel">Modifier le kiosque</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="editKiosqueForm">
                        <input type="hidden" id="editIdKiosque" name="idKiosque">
                        <div class="mb-3">
                            <label for="editNumeroIdentification" class="form-label">Numéro d'identification</label>
                            <input type="text" class="form-control" id="editNumeroIdentification" name="numeroIdentificationKiosque" readonly>
                        </div>
                        <div class="mb-3">
                            <label for="editEtatKiosque" class="form-label">État du Kiosque</label>
                            <select class="form-select" id="editEtatKiosque" name="etatKiosque" required>
                                <option value="Actif">Actif</option>
                                <option value="Inactif">Inactif</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="editAdresse" class="form-label">Adresse</label>
                            <input type="text" class="form-control" id="editAdresse" name="adresse" required>
                        </div>
                        <div class="mb-3">
                            <label for="editLatitude" class="form-label">Latitude</label>
                            <input type="number" step="any" class="form-control" id="editLatitude" name="latitude" required>
                        </div>
                        <div class="mb-3">
                            <label for="editLongitude" class="form-label">Longitude</label>
                            <input type="number" step="any" class="form-control" id="editLongitude" name="longitude" required>
                        </div>
                        <div class="mb-3">
                            <label for="editIdAgentKiosque" class="form-label">Agent du Kiosque</label>
                            <select class="form-select" id="editIdAgentKiosque" name="idAgentKiosque" required>
                                <!-- Les options seront chargées dynamiquement -->
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="editHoraires" class="form-label">Horaires</label>
                            <input type="text" class="form-control" id="editHoraires" name="horaires" readonly>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fermer</button>
                    <button type="button" class="btn btn-primary" id="saveKiosqueChanges">Enregistrer les modifications</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        // Initialisation de la carte
        var map = L.map('map').setView([48.8566, 2.3522], 13);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors'
        }).addTo(map);

        var marker;

        // Fonction de recherche d'adresse
        function searchAddress() {
            var address = document.getElementById('searchAddress').value;
            fetch('https://nominatim.openstreetmap.org/search?format=json&q=' + encodeURIComponent(address))
                .then(response => response.json())
                .then(data => {
                    if (data.length > 0) {
                        var lat = parseFloat(data[0].lat);
                        var lon = parseFloat(data[0].lon);
                        map.setView([lat, lon], 16);
                        if (marker) {
                            map.removeLayer(marker);
                        }
                        marker = L.marker([lat, lon]).addTo(map);
                        
                        document.getElementById('adresse').value = data[0].display_name;
                        document.getElementById('latitude').value = lat;
                        document.getElementById('longitude').value = lon;
                    } else {
                        alert('Adresse non trouvée');
                    }
                })
                .catch(error => console.error('Erreur:', error));
        }

        document.getElementById('searchButton').addEventListener('click', searchAddress);

        map.on('click', function(e) {
            var lat = e.latlng.lat;
            var lng = e.latlng.lng;
            if (marker) {
                map.removeLayer(marker);
            }
            marker = L.marker([lat, lng]).addTo(map);
            
            document.getElementById('latitude').value = lat;
            document.getElementById('longitude').value = lng;
            
            fetch('https://nominatim.openstreetmap.org/reverse?format=json&lat=' + lat + '&lon=' + lng)
                .then(response => response.json())
                .then(data => {
                    if (data.display_name) {
                        document.getElementById('adresse').value = data.display_name;
                    }
                })
                .catch(error => console.error('Erreur:', error));
        });

        // Charger les agents disponibles
        function loadAgents() {
            $.ajax({
                url: "${pageContext.request.contextPath}/administrateur/AgentKiosqueServlet",
                method: "GET",
                data: { action: "list" },
                success: function(agents) {
                    var select = $("#idAgentKiosque");
                    select.empty();
                    select.append('<option value="">Sélectionnez un agent</option>');
                    agents.forEach(function(agent) {
                        select.append('<option value="' + agent.idAgentKiosque + '">' + agent.email + '</option>');
                    });
                },
                error: function(xhr, status, error) {
                    console.error("Erreur lors du chargement des agents:", error);
                }
            });
        }

        // Charger les kiosques
        function loadKiosques() {
            $.ajax({
                url: "${pageContext.request.contextPath}/administrateur/KiosqueServlet",
                method: "GET",
                data: { action: "list" },
                success: function(kiosques) {
                    var tbody = $("#kiosquesTable tbody");
                    tbody.empty();
                    kiosques.forEach(function(kiosque) {
                        tbody.append('<tr>' +
                            '<td>' + kiosque.idKiosque + '</td>' +
                            '<td>' + kiosque.numeroIdentificationKiosque + '</td>' +
                            '<td>' + kiosque.etatKiosque + '</td>' +
                            '<td>' + kiosque.adresse + '</td>' +
                            '<td>' + kiosque.emailAgent + '</td>' +
                            '<td>' +
                                '<button class="btn btn-sm btn-primary edit-kiosque" data-id="' + kiosque.idKiosque + '"><i class="fas fa-edit"></i></button> ' +
                                '<button class="btn btn-sm btn-danger delete-kiosque" data-id="' + kiosque.idKiosque + '"><i class="fas fa-trash"></i></button>' +
                            '</td>' +
                        '</tr>');
                    });
                },
                error: function(xhr, status, error) {
                    console.error("Erreur lors du chargement des kiosques:", error);
                }
            });
        }

        // Ajouter un kiosque
        $("#addKiosqueForm").submit(function(e) {
            e.preventDefault();
            $.ajax({
                url: "${pageContext.request.contextPath}/administrateur/KiosqueServlet",
                method: "POST",
                data: $(this).serialize() + "&action=add",
                success: function(response) {
                    if(response.success) {
                        alert("Kiosque ajouté avec succès!");
                        loadKiosques();
                        $("#addKiosqueForm")[0].reset();
                    } else {
                        alert("Erreur lors de l'ajout du kiosque: " + response.message);
                    }
                },
                error: function(xhr, status, error) {
                    if (xhr.status === 401) {
                        alert("Votre session a expiré. Veuillez vous reconnecter.");
                        // Rediriger vers la page de connexion
                        window.location.href = "${pageContext.request.contextPath}/login.jsp";
                    } else {
                        alert("Erreur lors de l'ajout du kiosque: " + xhr.responseText);
                    }
                    console.error("Erreur lors de l'ajout du kiosque:", error);
                }
            });
        });

        // Ouvrir le modal de modification
        $(document).on('click', '.edit-kiosque', function() {
            var idKiosque = $(this).data('id');
            $.ajax({
                url: "${pageContext.request.contextPath}/administrateur/KiosqueServlet",
                method: "GET",
                data: { action: "get", idKiosque: idKiosque },
                success: function(kiosque) {
                    $('#editIdKiosque').val(kiosque.idKiosque);
                    $('#editNumeroIdentification').val(kiosque.numeroIdentificationKiosque);
                    $('#editEtatKiosque').val(kiosque.etatKiosque);
                    $('#editAdresse').val(kiosque.adresse);
                    $('#editLatitude').val(kiosque.latitude);
                    $('#editLongitude').val(kiosque.longitude);
                    $('#editHoraires').val(kiosque.horaires);
                    
                    // Charger les agents et sélectionner le bon agent
                    loadAgentsForEdit(kiosque.idAgentKiosque);
                    
                    // Verrouiller le numéro d'identification et l'horaire
                    $('#editNumeroIdentification').prop('readonly', true);
                    $('#editHoraires').prop('readonly', true);
                    
                    $('#editKiosqueModal').modal('show');
                },
                error: function(xhr, status, error) {
                    console.error("Erreur lors du chargement des détails du kiosque:", error);
                }
            });
        });

        // Enregistrer les modifications
        $('#saveKiosqueChanges').click(function() {
            var formData = $('#editKiosqueForm').serialize();
            $.ajax({
                url: "${pageContext.request.contextPath}/administrateur/KiosqueServlet",
                method: "POST",
                data: formData + "&action=update",
                success: function(response) {
                    if(response.success) {
                        alert("Kiosque modifié avec succès!");
                        $('#editKiosqueModal').modal('hide');
                        loadKiosques();
                    } else {
                        alert("Erreur lors de la modification du kiosque: " + response.message);
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Erreur lors de la modification du kiosque:", error);
                    alert("Erreur lors de la modification du kiosque: " + xhr.responseText);
                }
            });
        });

        // Supprimer un kiosque
        $(document).on('click', '.delete-kiosque', function() {
            var idKiosque = $(this).data('id');
            if(confirm("Êtes-vous sûr de vouloir supprimer ce kiosque ?")) {
                $.ajax({
                    url: "${pageContext.request.contextPath}/administrateur/KiosqueServlet",
                    method: "POST",
                    data: { action: "delete", idKiosque: idKiosque },
                    success: function(response) {
                        if(response.success) {
                            alert("Kiosque supprimé avec succès!");
                            loadKiosques();
                        } else {
                            alert("Erreur lors de la suppression du kiosque: " + response.message);
                        }
                    },
                    error: function(xhr, status, error) {
                        console.error("Erreur lors de la suppression du kiosque:", error);
                    }
                });
            }
        });

        // Charger la liste des agents disponibles pour la modification
        function loadAgentsForEdit(selectedAgentId) {
            $.ajax({
                url: "${pageContext.request.contextPath}/administrateur/AgentKiosqueServlet",
                method: "GET",
                data: { action: "list" },
                success: function(agents) {
                    var select = $("#editIdAgentKiosque");
                    select.empty();
                    select.append('<option value="">Sélectionnez un agent</option>');
                    agents.forEach(function(agent) {
                        var option = $('<option></option>').attr('value', agent.idAgentKiosque).text(agent.email);
                        if (agent.idAgentKiosque == selectedAgentId) {
                            option.prop('selected', true);
                        }
                        select.append(option);
                    });
                },
                error: function(xhr, status, error) {
                    console.error("Erreur lors du chargement des agents:", error);
                }
            });
        }

        $(document).ready(function() {
            loadAgents();
            loadKiosques();
        });
    </script>
</body>
</html>
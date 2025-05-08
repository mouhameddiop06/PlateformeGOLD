<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Paramètres - GOLD</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <!-- SweetAlert2 -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    
    <style>
        body {
            background-color: #f0f9ff;
            min-height: 100vh;
            padding: 2rem 0;
        }

        .settings-card {
            background: white;
            border-radius: 15px;
            padding: 2rem;
            margin-bottom: 1.5rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
        }

        .settings-header {
            display: flex;
            align-items: center;
            margin-bottom: 1.5rem;
            color: #1f2937;
        }

        .settings-header i {
            font-size: 1.5rem;
            margin-right: 0.75rem;
            color: #2563eb;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .btn-primary {
            background-color: #2563eb;
            border: none;
        }

        .btn-primary:hover {
            background-color: #1d4ed8;
        }

        .contact-list {
            max-height: 200px;
            overflow-y: auto;
        }

        .contact-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0.5rem;
            background-color: #f3f4f6;
            border-radius: 8px;
            margin-bottom: 0.5rem;
        }

        .contact-item button {
            padding: 0.25rem 0.5rem;
            font-size: 0.875rem;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <!-- Informations personnelles -->
                <div class="settings-card">
                    <div class="settings-header">
                        <i class="fas fa-user-circle"></i>
                        <h4>Informations personnelles</h4>
                    </div>
                    <form id="profileForm">
                        <div class="row">
                            <div class="col-md-6 form-group">
                                <label class="form-label">Nom</label>
                                <input type="text" class="form-control" id="nom" name="nom" required>
                            </div>
                            <div class="col-md-6 form-group">
                                <label class="form-label">Prénom</label>
                                <input type="text" class="form-control" id="prenom" name="prenom" required>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Email</label>
                            <input type="email" class="form-control" id="email" name="email" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Adresse</label>
                            <input type="text" class="form-control" id="adresse" name="adresse">
                        </div>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save me-2"></i>Enregistrer les modifications
                        </button>
                    </form>
                </div>

                <!-- Sécurité -->
                <div class="settings-card">
                    <div class="settings-header">
                        <i class="fas fa-lock"></i>
                        <h4>Sécurité</h4>
                    </div>
                    <form id="securityForm">
                        <div class="form-group">
                            <label class="form-label">Ancien code secret</label>
                            <input type="password" class="form-control" id="oldCode" required maxlength="4" pattern="\d{4}">
                        </div>
                        <div class="form-group">```jsp
                        <div class="form-group">
                            <label class="form-label">Nouveau code secret</label>
                            <input type="password" class="form-control" id="newCode" required maxlength="4" pattern="\d{4}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Confirmer le nouveau code</label>
                            <input type="password" class="form-control" id="confirmCode" required maxlength="4" pattern="\d{4}">
                        </div>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-key me-2"></i>Changer le code secret
                        </button>
                    </form>
                </div>

                <!-- Contacts de confiance -->
                <div class="settings-card">
                    <div class="settings-header">
                        <i class="fas fa-address-book"></i>
                        <h4>Contacts de confiance</h4>
                    </div>
                    <div class="mb-4">
                        <p class="text-muted">Ajoutez des contacts de confiance pour vos transferts rapides.</p>
                        <form id="contactForm" class="mb-3">
                            <div class="input-group">
                                <input type="tel" 
                                       class="form-control" 
                                       id="newContact" 
                                       placeholder="Numéro de téléphone"
                                       pattern="[0-9]{9}" 
                                       maxlength="9" 
                                       required>
                                <button class="btn btn-primary" type="submit">
                                    <i class="fas fa-plus me-2"></i>Ajouter
                                </button>
                            </div>
                        </form>
                        <div class="contact-list" id="contactList">
                            <!-- Les contacts seront ajoutés ici dynamiquement -->
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
</body>
</html>
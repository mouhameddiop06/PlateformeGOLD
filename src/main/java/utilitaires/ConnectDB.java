package utilitaires;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConnectDB {

    // Instance unique (singleton) de la classe ConnectDB
    private static ConnectDB instance;
    private Connection connection;

    // URL, utilisateur et mot de passe de la base de données
    private final String url = "jdbc:postgresql://localhost:5432/dbgold";
    private final String username = "postgres";
    private final String password = "mlkj";

    // Constructeur privé pour empêcher l'instanciation externe
    private ConnectDB() throws SQLException {
        try {
            // Charger le driver PostgreSQL
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Driver PostgreSQL introuvable", ex);
        }
    }

    // Méthode statique pour obtenir l'instance unique
    public static synchronized ConnectDB getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new ConnectDB();
        }
        return instance;
    }

    // Méthode pour obtenir la connexion à la base de données
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            // Réouvrir la connexion si elle a été fermée
            instance = new ConnectDB();
        }
        return connection;
    }

    // Méthode pour créer un PreparedStatement
    public static PreparedStatement creerStatement(String req) throws SQLException {
        return getInstance().getConnection().prepareStatement(req);
    }

    // Méthode pour fermer manuellement la connexion
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                instance = null; // Libérer l'instance pour un nouvel accès à la prochaine requête
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

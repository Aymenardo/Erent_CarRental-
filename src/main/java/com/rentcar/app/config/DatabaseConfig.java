package com.rentcar.app.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Configuration de la connexion à la base de données (Singleton)
 */
public class DatabaseConfig {
    // Instance unique
    private static DatabaseConfig instance;

    // Configuration de la base de données - MODIFIER CES VALEURS SELON VOTRE CONFIGURATION
    private static final String DB_HOST = "localhost"; // Adresse du serveur MySQL
    private static final String DB_PORT = "3306";      // Port du serveur MySQL (3306 par défaut)
    private static final String DB_NAME = "gestion_locations"; // Nom de la base de données

    // Identifiants de connexion - MODIFIER CES VALEURS SELON VOTRE CONFIGURATION
    private static final String DB_USER = "root";      // Nom d'utilisateur MySQL
    private static final String DB_PASSWORD = "@Admin2003";      // Mot de passe MySQL

    // URL de connexion à la base de données (construite à partir des paramètres ci-dessus)
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?serverTimezone=UTC";

    // Option permettant d'ajouter automatiquement les éléments à la base de données
    private static final boolean AUTO_RECONNECT = true;

    // Constructeur privé pour le pattern Singleton
    private DatabaseConfig() {
        // Initialisation si nécessaire
    }

    /**
     * Obtient l'instance unique de la configuration de base de données
     * @return L'instance de DatabaseConfig
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    /**
     * Obtient une connexion à la base de données
     * @return Une connexion à la base de données
     * @throws SQLException si la connexion échoue
     */
    public Connection getConnection() throws SQLException {
        try {
            // Charger le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Afficher les informations de connexion pour le débogage
            System.out.println("Tentative de connexion à la base de données avec les paramètres suivants:");
            System.out.println("URL: " + DB_URL);
            System.out.println("Utilisateur: " + DB_USER);
            System.out.println("Mot de passe défini: " + (DB_PASSWORD != null && !DB_PASSWORD.isEmpty()));

            // Établir la connexion avec un timeout plus long (30 secondes)
            return DriverManager.getConnection(
                    DB_URL + (AUTO_RECONNECT ? "&autoReconnect=true" : "") + "&connectTimeout=30000",
                    DB_USER,
                    DB_PASSWORD
            );
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur: Le driver MySQL n'a pas été trouvé");
            throw new SQLException("Le driver MySQL n'a pas été trouvé", e);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
            System.err.println("Vérifiez que le serveur MySQL est démarré et que la base de données 'gestion_locations' existe.");
            throw e;
        }
    }
}
package com.rentcar.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principale de l'application
 */
public class Main extends Application {
    
    private static Scene scene;
    
    /**
     * Méthode principale pour le lancement de l'application
     * @param args Arguments de ligne de commande
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Initialise et démarre l'interface utilisateur JavaFX
     * @param stage Stage principal
     * @throws IOException En cas d'erreur lors du chargement des ressources
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Chargement de la vue de connexion
        scene = new Scene(loadFXML("login"), 800, 600);
        
        // Configuration de la fenêtre principale
        stage.setTitle("Gestion des Locations de Voitures");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        
        // Chargement de l'icône de l'application
        try {
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/com/rentcar/app/images/car_icon.png")));
        } catch (Exception e) {
            System.err.println("Impossible de charger l'icône de l'application: " + e.getMessage());
            // L'absence d'icône n'est pas critique, l'application continuera à fonctionner
        }
        
        // Affichage de la fenêtre
        stage.show();
    }
    
    /**
     * Change la vue racine de la scène
     * @param fxml Nom du fichier FXML à charger
     * @throws IOException En cas d'erreur lors du chargement du fichier FXML
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    
    /**
     * Charge un fichier FXML
     * @param fxml Nom du fichier FXML à charger
     * @return Le nœud racine du FXML chargé
     * @throws IOException En cas d'erreur lors du chargement du fichier FXML
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/rentcar/app/views/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }
}
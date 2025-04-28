package com.rentcar.app.controllers;

import com.rentcar.app.Main;
import com.rentcar.app.models.User;
import com.rentcar.app.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Contrôleur pour la vue principale
 */
public class MainController {
    
    @FXML
    private BorderPane mainPane;
    
    @FXML
    private VBox menuPane;
    
    @FXML
    private Label userLabel;
    
    @FXML
    private Button clientsButton;
    
    @FXML
    private Button voituresButton;
    
    @FXML
    private Button contratsButton;
    
    @FXML
    private Button paiementsButton;
    
    @FXML
    private Button logoutButton;
    
    private final SessionManager sessionManager = SessionManager.getInstance();

    /**
     * Initialise le contrôleur
     */
    @FXML
    private void initialize() {
        // Vérifier si l'utilisateur est connecté
        if (!sessionManager.isLoggedIn()) {
            try {
                Main.setRoot("login");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // Afficher les informations de l'utilisateur connecté
        User currentUser = sessionManager.getCurrentUser();
        userLabel.setText("Utilisateur: " + currentUser.getUsername() + "\nRôle: " + currentUser.getRole().getValue());
        
        // Charger la vue des clients par défaut
        loadView("client");
    }

    /**
     * Charge la vue des clients
     * @param event Événement de clic
     */
    @FXML
    private void handleClientsAction(ActionEvent event) {
        loadView("client");
    }

    /**
     * Charge la vue des voitures
     * @param event Événement de clic
     */
    @FXML
    private void handleVoituresAction(ActionEvent event) {
        loadView("voiture");
    }

    /**
     * Charge la vue des contrats
     * @param event Événement de clic
     */
    @FXML
    private void handleContratsAction(ActionEvent event) {
        loadView("contrat");
    }

    /**
     * Charge la vue des paiements
     * @param event Événement de clic
     */
    @FXML
    private void handlePaiementsAction(ActionEvent event) {
        loadView("paiement");
    }

    /**
     * Déconnecte l'utilisateur
     * @param event Événement de clic
     */
    @FXML
    private void handleLogoutAction(ActionEvent event) {
        sessionManager.logout();
        try {
            Main.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Charge une vue dans le panneau principal
     * @param viewName Nom de la vue à charger
     */
    private void loadView(String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rentcar/app/views/" + viewName + ".fxml"));
            Parent view = loader.load();
            mainPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.rentcar.app.controllers;

import com.rentcar.app.Main;
import com.rentcar.app.dao.UserDAO;
import com.rentcar.app.models.User;
import com.rentcar.app.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Contrôleur pour la vue de connexion
 */
public class LoginController {
    
    @FXML
    private AnchorPane rootPane;
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    private final UserDAO userDAO = new UserDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();

    /**
     * Initialise le contrôleur
     */
    @FXML
    private void initialize() {
        // Si l'utilisateur est déjà connecté, rediriger vers la page principale
        if (sessionManager.isLoggedIn()) {
            try {
                Main.setRoot("main");
            } catch (IOException e) {
                showError("Erreur lors du chargement de la page principale: " + e.getMessage());
            }
        }
    }

    /**
     * Gère la tentative de connexion
     * @param event Événement de clic
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez entrer un nom d'utilisateur et un mot de passe.");
            return;
        }
        
        User user = userDAO.authenticate(username, password);
        
        if (user != null) {
            sessionManager.setCurrentUser(user);
            try {
                Main.setRoot("main");
            } catch (IOException e) {
                showError("Erreur lors du chargement de la page principale: " + e.getMessage());
            }
        } else {
            showError("Nom d'utilisateur ou mot de passe incorrect.");
        }
    }

    /**
     * Affiche une boîte de dialogue d'erreur
     * @param message Message d'erreur
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

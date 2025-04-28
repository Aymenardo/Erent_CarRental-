package com.rentcar.app.controllers;

import com.rentcar.app.dao.ClientDAO;
import com.rentcar.app.models.Client;
import com.rentcar.app.utils.SessionManager;
import com.rentcar.app.utils.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la vue des clients
 */
public class ClientController {
    
    @FXML
    private VBox clientPane;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private TableView<Client> clientTable;
    
    @FXML
    private TableColumn<Client, Integer> idColumn;
    
    @FXML
    private TableColumn<Client, String> nomColumn;
    
    @FXML
    private TableColumn<Client, String> prenomColumn;
    
    @FXML
    private TableColumn<Client, String> emailColumn;
    
    @FXML
    private TableColumn<Client, String> telephoneColumn;
    
    @FXML
    private TextField nomField;
    
    @FXML
    private TextField prenomField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField telephoneField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button addButton;
    
    @FXML
    private Button updateButton;
    
    @FXML
    private Button deleteButton;
    
    @FXML
    private Button clearButton;
    
    private final ClientDAO clientDAO = new ClientDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final ObservableList<Client> clientList = FXCollections.observableArrayList();
    private Client selectedClient;

    /**
     * Initialise le contrôleur
     */
    @FXML
    private void initialize() {
        // Configurer les colonnes de la table
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        
        // Charger les données
        loadClients();
        
        // Configurer la sélection dans la table
        clientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedClient = newSelection;
                nomField.setText(selectedClient.getNom());
                prenomField.setText(selectedClient.getPrenom());
                emailField.setText(selectedClient.getEmail());
                telephoneField.setText(selectedClient.getTelephone());
                updateButton.setDisable(false);
                deleteButton.setDisable(!sessionManager.isAdmin());
            } else {
                selectedClient = null;
                clearFields();
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
        
        // Configurer l'accès en fonction du rôle
        boolean isAdmin = sessionManager.isAdmin();
        addButton.setDisable(!isAdmin);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        
        // Masquer le message d'erreur
        errorLabel.setVisible(false);
    }

    /**
     * Charge la liste des clients
     */
    private void loadClients() {
        List<Client> clients = clientDAO.findAll();
        clientList.setAll(clients);
        clientTable.setItems(clientList);
    }

    /**
     * Recherche des clients par nom ou prénom
     * @param event Événement de clic
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        String recherche = searchField.getText().trim();
        
        if (recherche.isEmpty()) {
            loadClients();
        } else {
            List<Client> clients = clientDAO.findByName(recherche);
            clientList.setAll(clients);
            clientTable.setItems(clientList);
        }
    }

    /**
     * Ajoute un nouveau client
     * @param event Événement de clic
     */
    @FXML
    private void handleAdd(ActionEvent event) {
        if (!validateForm()) {
            return;
        }
        
        Client newClient = new Client(
            nomField.getText().trim(),
            prenomField.getText().trim(),
            emailField.getText().trim(),
            telephoneField.getText().trim()
        );
        
        if (clientDAO.create(newClient)) {
            showSuccess("Client ajouté avec succès.");
            loadClients();
            clearFields();
        } else {
            showError("Erreur lors de l'ajout du client.");
        }
    }

    /**
     * Met à jour un client
     * @param event Événement de clic
     */
    @FXML
    private void handleUpdate(ActionEvent event) {
        if (selectedClient == null) {
            showError("Aucun client sélectionné.");
            return;
        }
        
        if (!validateForm()) {
            return;
        }
        
        selectedClient.setNom(nomField.getText().trim());
        selectedClient.setPrenom(prenomField.getText().trim());
        selectedClient.setEmail(emailField.getText().trim());
        selectedClient.setTelephone(telephoneField.getText().trim());
        
        if (clientDAO.update(selectedClient)) {
            showSuccess("Client mis à jour avec succès.");
            loadClients();
            clearFields();
        } else {
            showError("Erreur lors de la mise à jour du client.");
        }
    }

    /**
     * Supprime un client
     * @param event Événement de clic
     */
    @FXML
    private void handleDelete(ActionEvent event) {
        if (selectedClient == null) {
            showError("Aucun client sélectionné.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce client ? Cette action est irréversible.");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (clientDAO.delete(selectedClient.getId())) {
                showSuccess("Client supprimé avec succès.");
                loadClients();
                clearFields();
            } else {
                showError("Erreur lors de la suppression du client.");
            }
        }
    }

    /**
     * Efface les champs du formulaire
     * @param event Événement de clic
     */
    @FXML
    private void handleClear(ActionEvent event) {
        clearFields();
        clientTable.getSelectionModel().clearSelection();
    }

    /**
     * Efface les champs du formulaire
     */
    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
        selectedClient = null;
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        errorLabel.setVisible(false);
    }

    /**
     * Vérifie que les champs du formulaire sont valides
     * @return true si le formulaire est valide, false sinon
     */
    private boolean validateForm() {
        errorLabel.setVisible(false);
        
        if (ValidationUtil.isNullOrEmpty(nomField.getText())) {
            showError("Le nom est obligatoire.");
            return false;
        }
        
        if (ValidationUtil.isNullOrEmpty(prenomField.getText())) {
            showError("Le prénom est obligatoire.");
            return false;
        }
        
        if (!ValidationUtil.isNullOrEmpty(emailField.getText()) && !ValidationUtil.isValidEmail(emailField.getText())) {
            showError("L'email est invalide.");
            return false;
        }
        
        if (ValidationUtil.isNullOrEmpty(telephoneField.getText())) {
            showError("Le téléphone est obligatoire.");
            return false;
        }
        
        if (!ValidationUtil.isValidPhone(telephoneField.getText())) {
            showError("Le téléphone doit contenir uniquement des chiffres.");
            return false;
        }
        
        return true;
    }

    /**
     * Affiche un message d'erreur
     * @param message Message d'erreur
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    /**
     * Affiche un message de succès
     * @param message Message de succès
     */
    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setStyle("-fx-text-fill: green;");
    }
}

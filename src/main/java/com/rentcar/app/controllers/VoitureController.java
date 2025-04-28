package com.rentcar.app.controllers;

import com.rentcar.app.dao.VoitureDAO;
import com.rentcar.app.models.Voiture;
import com.rentcar.app.utils.SessionManager;
import com.rentcar.app.utils.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.Optional;

/**
 * Contrôleur pour la vue des voitures
 */
public class VoitureController {
    
    @FXML
    private VBox voiturePane;
    
    @FXML
    private TableView<Voiture> voitureTable;
    
    @FXML
    private TableColumn<Voiture, Integer> idColumn;
    
    @FXML
    private TableColumn<Voiture, String> marqueColumn;
    
    @FXML
    private TableColumn<Voiture, String> modeleColumn;
    
    @FXML
    private TableColumn<Voiture, String> immatriculationColumn;
    
    @FXML
    private TableColumn<Voiture, Voiture.Statut> statutColumn;
    
    @FXML
    private TextField marqueField;
    
    @FXML
    private TextField modeleField;
    
    @FXML
    private TextField immatriculationField;
    
    @FXML
    private ComboBox<String> statutCombo;
    
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
    
    @FXML
    private Button filterDisponibleButton;
    
    @FXML
    private Button filterAllButton;
    
    private final VoitureDAO voitureDAO = new VoitureDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final ObservableList<Voiture> voitureList = FXCollections.observableArrayList();
    private Voiture selectedVoiture;

    /**
     * Initialise le contrôleur
     */
    @FXML
    private void initialize() {
        // Configurer les colonnes de la table
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        marqueColumn.setCellValueFactory(new PropertyValueFactory<>("marque"));
        modeleColumn.setCellValueFactory(new PropertyValueFactory<>("modele"));
        immatriculationColumn.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        
        // Configurer l'affichage de la colonne statut
        statutColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Voiture.Statut statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(statut.getValue());
                    if (statut == Voiture.Statut.DISPONIBLE) {
                        setStyle("-fx-text-fill: green;");
                    } else {
                        setStyle("-fx-text-fill: red;");
                    }
                }
            }
        });
        
        // Initialiser la combobox des statuts
        statutCombo.getItems().addAll("Disponible", "Loué");
        statutCombo.setValue("Disponible");
        
        // Charger les données
        loadVoitures();
        
        // Configurer la sélection dans la table
        voitureTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedVoiture = newSelection;
                marqueField.setText(selectedVoiture.getMarque());
                modeleField.setText(selectedVoiture.getModele());
                immatriculationField.setText(selectedVoiture.getImmatriculation());
                statutCombo.setValue(selectedVoiture.getStatut().getValue());
                updateButton.setDisable(false);
                deleteButton.setDisable(!sessionManager.isAdmin());
            } else {
                selectedVoiture = null;
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
     * Charge la liste des voitures
     */
    private void loadVoitures() {
        voitureList.setAll(voitureDAO.findAll());
        voitureTable.setItems(voitureList);
    }

    /**
     * Filtre les voitures disponibles
     * @param event Événement de clic
     */
    @FXML
    private void handleFilterDisponible(ActionEvent event) {
        voitureList.setAll(voitureDAO.findAvailable());
        voitureTable.setItems(voitureList);
    }

    /**
     * Affiche toutes les voitures
     * @param event Événement de clic
     */
    @FXML
    private void handleFilterAll(ActionEvent event) {
        loadVoitures();
    }

    /**
     * Ajoute une nouvelle voiture
     * @param event Événement de clic
     */
    @FXML
    private void handleAdd(ActionEvent event) {
        if (!validateForm()) {
            return;
        }
        
        if (voitureDAO.immatriculationExists(immatriculationField.getText().trim(), 0)) {
            showError("Cette immatriculation existe déjà.");
            return;
        }
        
        Voiture.Statut statut = statutCombo.getValue().equals("Disponible") ? 
                              Voiture.Statut.DISPONIBLE : Voiture.Statut.LOUE;
        
        Voiture newVoiture = new Voiture(
            marqueField.getText().trim(),
            modeleField.getText().trim(),
            immatriculationField.getText().trim(),
            statut
        );
        
        if (voitureDAO.create(newVoiture)) {
            showSuccess("Voiture ajoutée avec succès.");
            loadVoitures();
            clearFields();
        } else {
            showError("Erreur lors de l'ajout de la voiture.");
        }
    }

    /**
     * Met à jour une voiture
     * @param event Événement de clic
     */
    @FXML
    private void handleUpdate(ActionEvent event) {
        if (selectedVoiture == null) {
            showError("Aucune voiture sélectionnée.");
            return;
        }
        
        if (!validateForm()) {
            return;
        }
        
        if (voitureDAO.immatriculationExists(immatriculationField.getText().trim(), selectedVoiture.getId())) {
            showError("Cette immatriculation existe déjà.");
            return;
        }
        
        Voiture.Statut statut = statutCombo.getValue().equals("Disponible") ? 
                              Voiture.Statut.DISPONIBLE : Voiture.Statut.LOUE;
        
        selectedVoiture.setMarque(marqueField.getText().trim());
        selectedVoiture.setModele(modeleField.getText().trim());
        selectedVoiture.setImmatriculation(immatriculationField.getText().trim());
        selectedVoiture.setStatut(statut);
        
        if (voitureDAO.update(selectedVoiture)) {
            showSuccess("Voiture mise à jour avec succès.");
            loadVoitures();
            clearFields();
        } else {
            showError("Erreur lors de la mise à jour de la voiture.");
        }
    }

    /**
     * Supprime une voiture
     * @param event Événement de clic
     */
    @FXML
    private void handleDelete(ActionEvent event) {
        if (selectedVoiture == null) {
            showError("Aucune voiture sélectionnée.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette voiture ? Cette action est irréversible.");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (voitureDAO.delete(selectedVoiture.getId())) {
                showSuccess("Voiture supprimée avec succès.");
                loadVoitures();
                clearFields();
            } else {
                showError("Erreur lors de la suppression de la voiture.");
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
        voitureTable.getSelectionModel().clearSelection();
    }

    /**
     * Efface les champs du formulaire
     */
    private void clearFields() {
        marqueField.clear();
        modeleField.clear();
        immatriculationField.clear();
        statutCombo.setValue("Disponible");
        selectedVoiture = null;
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
        
        if (ValidationUtil.isNullOrEmpty(marqueField.getText())) {
            showError("La marque est obligatoire.");
            return false;
        }
        
        if (ValidationUtil.isNullOrEmpty(modeleField.getText())) {
            showError("Le modèle est obligatoire.");
            return false;
        }
        
        if (ValidationUtil.isNullOrEmpty(immatriculationField.getText())) {
            showError("L'immatriculation est obligatoire.");
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

package com.rentcar.app.controllers;

import com.rentcar.app.dao.ClientDAO;
import com.rentcar.app.dao.ContratDAO;
import com.rentcar.app.dao.VoitureDAO;
import com.rentcar.app.models.Client;
import com.rentcar.app.models.Contrat;
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
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la vue des contrats
 */
public class ContratController {
    
    @FXML
    private VBox contratPane;
    
    @FXML
    private TableView<Contrat> contratTable;
    
    @FXML
    private TableColumn<Contrat, Integer> idColumn;
    
    @FXML
    private TableColumn<Contrat, String> clientColumn;
    
    @FXML
    private TableColumn<Contrat, String> voitureColumn;
    
    @FXML
    private TableColumn<Contrat, LocalDate> dateDebutColumn;
    
    @FXML
    private TableColumn<Contrat, LocalDate> dateFinColumn;
    
    @FXML
    private TableColumn<Contrat, Double> montantColumn;
    
    @FXML
    private ComboBox<Client> clientCombo;
    
    @FXML
    private ComboBox<Voiture> voitureCombo;
    
    @FXML
    private DatePicker dateDebutPicker;
    
    @FXML
    private DatePicker dateFinPicker;
    
    @FXML
    private TextField montantField;
    
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
    private Button calculerButton;
    
    private final ContratDAO contratDAO = new ContratDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final VoitureDAO voitureDAO = new VoitureDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final ObservableList<Contrat> contratList = FXCollections.observableArrayList();
    private Contrat selectedContrat;

    /**
     * Initialise le contrôleur
     */
    @FXML
    private void initialize() {
        // Configurer les colonnes de la table
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getClient() != null) {
                return javafx.beans.binding.Bindings.createStringBinding(
                    () -> cellData.getValue().getClient().getNomComplet());
            }
            return javafx.beans.binding.Bindings.createStringBinding(() -> "");
        });
        voitureColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getVoiture() != null) {
                return javafx.beans.binding.Bindings.createStringBinding(
                    () -> cellData.getValue().getVoiture().toString());
            }
            return javafx.beans.binding.Bindings.createStringBinding(() -> "");
        });
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));
        
        // Formater les dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateDebutColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(formatter.format(date));
                }
            }
        });
        dateFinColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(formatter.format(date));
                }
            }
        });
        
        // Formater les montants
        montantColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double montant, boolean empty) {
                super.updateItem(montant, empty);
                if (empty || montant == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f €", montant));
                }
            }
        });
        
        // Initialiser les ComboBox
        configurerClientCombo();
        configurerVoitureCombo();
        
        // Configurer les DatePicker
        dateDebutPicker.setValue(LocalDate.now());
        dateFinPicker.setValue(LocalDate.now().plusDays(1));
        
        // Restriction des dates: pas de dates dans le passé
        dateDebutPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        
        // Restriction des dates: pas de dates avant la date de début
        dateFinPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (dateDebutPicker.getValue() != null) {
                    setDisable(empty || date.isBefore(dateDebutPicker.getValue()));
                }
            }
        });
        
        // Mise à jour de la date de fin lorsque la date de début change
        dateDebutPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && (dateFinPicker.getValue() == null || dateFinPicker.getValue().isBefore(newVal))) {
                dateFinPicker.setValue(newVal.plusDays(1));
            }
        });
        
        // Charger les données
        loadContrats();
        
        // Configurer la sélection dans la table
        contratTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedContrat = newSelection;
                clientCombo.setValue(selectedContrat.getClient());
                voitureCombo.setValue(selectedContrat.getVoiture());
                dateDebutPicker.setValue(selectedContrat.getDateDebut());
                dateFinPicker.setValue(selectedContrat.getDateFin());
                montantField.setText(String.format("%.2f", selectedContrat.getMontant()));
                updateButton.setDisable(false);
                deleteButton.setDisable(!sessionManager.isAdmin());
            } else {
                selectedContrat = null;
                clearFields();
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
        
        // Configurer l'accès en fonction du rôle
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        
        // Masquer le message d'erreur
        errorLabel.setVisible(false);
    }

    /**
     * Configure la ComboBox des clients
     */
    private void configurerClientCombo() {
        List<Client> clients = clientDAO.findAll();
        clientCombo.setItems(FXCollections.observableArrayList(clients));
        clientCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Client client) {
                return client != null ? client.getNomComplet() : "";
            }
            
            @Override
            public Client fromString(String string) {
                return null; // Non utilisé
            }
        });
    }

    /**
     * Configure la ComboBox des voitures
     */
    private void configurerVoitureCombo() {
        List<Voiture> voitures = voitureDAO.findAvailable();
        voitureCombo.setItems(FXCollections.observableArrayList(voitures));
        voitureCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Voiture voiture) {
                return voiture != null ? voiture.toString() : "";
            }
            
            @Override
            public Voiture fromString(String string) {
                return null; // Non utilisé
            }
        });
    }

    /**
     * Charge la liste des contrats
     */
    private void loadContrats() {
        List<Contrat> contrats = contratDAO.findAll();
        contratList.setAll(contrats);
        contratTable.setItems(contratList);
    }

    /**
     * Calcule le montant du contrat en fonction des dates
     * @param event Événement de clic
     */
    @FXML
    private void handleCalculer(ActionEvent event) {
        if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null) {
            showError("Veuillez sélectionner les dates de début et de fin.");
            return;
        }
        
        if (voitureCombo.getValue() == null) {
            showError("Veuillez sélectionner une voiture.");
            return;
        }
        
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();
        
        if (dateFin.isBefore(dateDebut)) {
            showError("La date de fin doit être postérieure à la date de début.");
            return;
        }
        
        // Calcul simple du montant: 50€ par jour
        long nbJours = ChronoUnit.DAYS.between(dateDebut, dateFin);
        double montant = nbJours * 50.0;
        
        montantField.setText(String.format("%.2f", montant));
    }

    /**
     * Ajoute un nouveau contrat
     * @param event Événement de clic
     */
    @FXML
    private void handleAdd(ActionEvent event) {
        if (!validateForm()) {
            return;
        }
        
        Client client = clientCombo.getValue();
        Voiture voiture = voitureCombo.getValue();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();
        double montant = Double.parseDouble(montantField.getText().replace(',', '.'));
        
        // Vérifier si la voiture est disponible pour cette période
        if (!contratDAO.isVoitureAvailable(voiture.getId(), dateDebut, dateFin, 0)) {
            showError("Cette voiture n'est pas disponible pour la période sélectionnée.");
            return;
        }
        
        Contrat newContrat = new Contrat(
            client.getId(),
            voiture.getId(),
            dateDebut,
            dateFin,
            montant
        );
        
        if (contratDAO.create(newContrat)) {
            showSuccess("Contrat créé avec succès.");
            loadContrats();
            clearFields();
            // Actualiser la liste des voitures disponibles
            configurerVoitureCombo();
        } else {
            showError("Erreur lors de la création du contrat.");
        }
    }

    /**
     * Met à jour un contrat
     * @param event Événement de clic
     */
    @FXML
    private void handleUpdate(ActionEvent event) {
        if (selectedContrat == null) {
            showError("Aucun contrat sélectionné.");
            return;
        }
        
        if (!validateForm()) {
            return;
        }
        
        Client client = clientCombo.getValue();
        Voiture voiture = voitureCombo.getValue();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();
        double montant = Double.parseDouble(montantField.getText().replace(',', '.'));
        
        // Vérifier si la voiture est disponible pour cette période (en excluant le contrat actuel)
        if (!contratDAO.isVoitureAvailable(voiture.getId(), dateDebut, dateFin, selectedContrat.getId())) {
            showError("Cette voiture n'est pas disponible pour la période sélectionnée.");
            return;
        }
        
        selectedContrat.setClientId(client.getId());
        selectedContrat.setVoitureId(voiture.getId());
        selectedContrat.setDateDebut(dateDebut);
        selectedContrat.setDateFin(dateFin);
        selectedContrat.setMontant(montant);
        
        if (contratDAO.update(selectedContrat)) {
            showSuccess("Contrat mis à jour avec succès.");
            loadContrats();
            clearFields();
        } else {
            showError("Erreur lors de la mise à jour du contrat.");
        }
    }

    /**
     * Supprime un contrat
     * @param event Événement de clic
     */
    @FXML
    private void handleDelete(ActionEvent event) {
        if (selectedContrat == null) {
            showError("Aucun contrat sélectionné.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce contrat ? Cette action est irréversible.");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (contratDAO.delete(selectedContrat.getId())) {
                showSuccess("Contrat supprimé avec succès.");
                loadContrats();
                clearFields();
                // Actualiser la liste des voitures disponibles
                configurerVoitureCombo();
            } else {
                showError("Erreur lors de la suppression du contrat.");
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
        contratTable.getSelectionModel().clearSelection();
    }

    /**
     * Efface les champs du formulaire
     */
    private void clearFields() {
        clientCombo.setValue(null);
        voitureCombo.setValue(null);
        dateDebutPicker.setValue(LocalDate.now());
        dateFinPicker.setValue(LocalDate.now().plusDays(1));
        montantField.clear();
        selectedContrat = null;
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
        
        if (clientCombo.getValue() == null) {
            showError("Veuillez sélectionner un client.");
            return false;
        }
        
        if (voitureCombo.getValue() == null) {
            showError("Veuillez sélectionner une voiture.");
            return false;
        }
        
        if (dateDebutPicker.getValue() == null) {
            showError("Veuillez sélectionner une date de début.");
            return false;
        }
        
        if (dateFinPicker.getValue() == null) {
            showError("Veuillez sélectionner une date de fin.");
            return false;
        }
        
        if (dateFinPicker.getValue().isBefore(dateDebutPicker.getValue())) {
            showError("La date de fin doit être postérieure à la date de début.");
            return false;
        }
        
        if (ValidationUtil.isNullOrEmpty(montantField.getText())) {
            showError("Veuillez calculer ou saisir un montant.");
            return false;
        }
        
        if (!ValidationUtil.isNumeric(montantField.getText().replace(',', '.'))) {
            showError("Le montant doit être un nombre valide.");
            return false;
        }
        
        double montant = Double.parseDouble(montantField.getText().replace(',', '.'));
        if (!ValidationUtil.isPositive(montant)) {
            showError("Le montant doit être positif.");
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

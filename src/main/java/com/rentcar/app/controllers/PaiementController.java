package com.rentcar.app.controllers;

import com.rentcar.app.dao.ContratDAO;
import com.rentcar.app.dao.PaiementDAO;
import com.rentcar.app.models.Contrat;
import com.rentcar.app.models.Paiement;
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
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la vue des paiements
 */
public class PaiementController {
    
    @FXML
    private VBox paiementPane;
    
    @FXML
    private TableView<Paiement> paiementTable;
    
    @FXML
    private TableColumn<Paiement, Integer> idColumn;
    
    @FXML
    private TableColumn<Paiement, String> contratColumn;
    
    @FXML
    private TableColumn<Paiement, Double> montantColumn;
    
    @FXML
    private TableColumn<Paiement, LocalDate> datePaiementColumn;
    
    @FXML
    private TableColumn<Paiement, Paiement.Methode> methodeColumn;
    
    @FXML
    private ComboBox<Contrat> contratCombo;
    
    @FXML
    private TextField montantField;
    
    @FXML
    private DatePicker datePaiementPicker;
    
    @FXML
    private ComboBox<String> methodeCombo;
    
    @FXML
    private Label infoContratLabel;
    
    @FXML
    private Label resteLabel;
    
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
    
    private final PaiementDAO paiementDAO = new PaiementDAO();
    private final ContratDAO contratDAO = new ContratDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final ObservableList<Paiement> paiementList = FXCollections.observableArrayList();
    private Paiement selectedPaiement;

    /**
     * Initialise le contrôleur
     */
    @FXML
    private void initialize() {
        // Configurer les colonnes de la table
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        contratColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getContrat() != null) {
                return javafx.beans.binding.Bindings.createStringBinding(
                    () -> "Contrat #" + cellData.getValue().getContrat().getId());
            }
            return javafx.beans.binding.Bindings.createStringBinding(() -> "");
        });
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));
        datePaiementColumn.setCellValueFactory(new PropertyValueFactory<>("datePaiement"));
        methodeColumn.setCellValueFactory(new PropertyValueFactory<>("methode"));
        
        // Formater les dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        datePaiementColumn.setCellFactory(column -> new TableCell<>() {
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
        
        // Formater la méthode de paiement
        methodeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Paiement.Methode methode, boolean empty) {
                super.updateItem(methode, empty);
                if (empty || methode == null) {
                    setText(null);
                } else {
                    setText(methode.getValue());
                }
            }
        });
        
        // Initialiser les ComboBox
        configurerContratCombo();
        methodeCombo.getItems().addAll("Espèces", "Carte", "Virement");
        methodeCombo.setValue("Espèces");
        
        // Configurer le DatePicker
        datePaiementPicker.setValue(LocalDate.now());
        datePaiementPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });
        
        // Charger les données
        loadPaiements();
        
        // Configurer la sélection dans la table
        paiementTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedPaiement = newSelection;
                contratCombo.setValue(selectedPaiement.getContrat());
                montantField.setText(String.format("%.2f", selectedPaiement.getMontant()));
                datePaiementPicker.setValue(selectedPaiement.getDatePaiement());
                methodeCombo.setValue(selectedPaiement.getMethode().getValue());
                updateButton.setDisable(false);
                deleteButton.setDisable(!sessionManager.isAdmin());
                updateInfoContrat(selectedPaiement.getContrat());
            } else {
                selectedPaiement = null;
                clearFields();
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
        
        // Configurer l'accès en fonction du rôle
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        
        // Écouter les changements sur la combobox des contrats
        contratCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateInfoContrat(newVal);
            } else {
                infoContratLabel.setText("Sélectionner un contrat");
                resteLabel.setText("");
            }
        });
        
        // Masquer le message d'erreur
        errorLabel.setVisible(false);
    }

    /**
     * Configure la ComboBox des contrats
     */
    private void configurerContratCombo() {
        List<Contrat> contrats = contratDAO.findAll();
        contratCombo.setItems(FXCollections.observableArrayList(contrats));
        contratCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Contrat contrat) {
                if (contrat != null) {
                    return "Contrat #" + contrat.getId() + " - " + 
                           (contrat.getClient() != null ? contrat.getClient().getNomComplet() : "");
                }
                return "";
            }
            
            @Override
            public Contrat fromString(String string) {
                return null; // Non utilisé
            }
        });
    }

    /**
     * Met à jour les informations du contrat sélectionné
     * @param contrat Contrat sélectionné
     */
    private void updateInfoContrat(Contrat contrat) {
        if (contrat != null) {
            String clientInfo = contrat.getClient() != null ? contrat.getClient().getNomComplet() : "";
            String voitureInfo = contrat.getVoiture() != null ? contrat.getVoiture().toString() : "";
            
            infoContratLabel.setText(
                "Client: " + clientInfo + "\n" +
                "Voiture: " + voitureInfo + "\n" +
                "Période: " + contrat.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                " au " + contrat.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                "Montant total: " + String.format("%.2f €", contrat.getMontant())
            );
            
            // Calculer le reste à payer
            double totalPaye = paiementDAO.getTotalPaiementsByContrat(contrat.getId());
            double reste = contrat.getMontant() - totalPaye;
            
            resteLabel.setText("Total payé: " + String.format("%.2f €", totalPaye) + 
                              "\nReste à payer: " + String.format("%.2f €", reste));
            
            // Proposer le montant restant
            if (reste > 0 && (selectedPaiement == null || 
                (selectedPaiement != null && selectedPaiement.getContratId() != contrat.getId()))) {
                montantField.setText(String.format("%.2f", reste));
            }
        }
    }

    /**
     * Charge la liste des paiements
     */
    private void loadPaiements() {
        List<Paiement> paiements = paiementDAO.findAll();
        paiementList.setAll(paiements);
        paiementTable.setItems(paiementList);
    }

    /**
     * Ajoute un nouveau paiement
     * @param event Événement de clic
     */
    @FXML
    private void handleAdd(ActionEvent event) {
        if (!validateForm()) {
            return;
        }
        
        Contrat contrat = contratCombo.getValue();
        double montant = Double.parseDouble(montantField.getText().replace(',', '.'));
        LocalDate datePaiement = datePaiementPicker.getValue();
        Paiement.Methode methode = Paiement.Methode.fromString(methodeCombo.getValue());
        
        Paiement newPaiement = new Paiement(
            contrat.getId(),
            montant,
            datePaiement,
            methode
        );
        
        if (paiementDAO.create(newPaiement)) {
            showSuccess("Paiement enregistré avec succès.");
            loadPaiements();
            clearFields();
        } else {
            showError("Erreur lors de l'enregistrement du paiement.");
        }
    }

    /**
     * Met à jour un paiement
     * @param event Événement de clic
     */
    @FXML
    private void handleUpdate(ActionEvent event) {
        if (selectedPaiement == null) {
            showError("Aucun paiement sélectionné.");
            return;
        }
        
        if (!validateForm()) {
            return;
        }
        
        Contrat contrat = contratCombo.getValue();
        double montant = Double.parseDouble(montantField.getText().replace(',', '.'));
        LocalDate datePaiement = datePaiementPicker.getValue();
        Paiement.Methode methode = Paiement.Methode.fromString(methodeCombo.getValue());
        
        selectedPaiement.setContratId(contrat.getId());
        selectedPaiement.setMontant(montant);
        selectedPaiement.setDatePaiement(datePaiement);
        selectedPaiement.setMethode(methode);
        
        if (paiementDAO.update(selectedPaiement)) {
            showSuccess("Paiement mis à jour avec succès.");
            loadPaiements();
            clearFields();
        } else {
            showError("Erreur lors de la mise à jour du paiement.");
        }
    }

    /**
     * Supprime un paiement
     * @param event Événement de clic
     */
    @FXML
    private void handleDelete(ActionEvent event) {
        if (selectedPaiement == null) {
            showError("Aucun paiement sélectionné.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce paiement ? Cette action est irréversible.");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (paiementDAO.delete(selectedPaiement.getId())) {
                showSuccess("Paiement supprimé avec succès.");
                loadPaiements();
                clearFields();
            } else {
                showError("Erreur lors de la suppression du paiement.");
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
        paiementTable.getSelectionModel().clearSelection();
    }

    /**
     * Efface les champs du formulaire
     */
    private void clearFields() {
        contratCombo.setValue(null);
        montantField.clear();
        datePaiementPicker.setValue(LocalDate.now());
        methodeCombo.setValue("Espèces");
        selectedPaiement = null;
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        errorLabel.setVisible(false);
        infoContratLabel.setText("Sélectionner un contrat");
        resteLabel.setText("");
    }

    /**
     * Vérifie que les champs du formulaire sont valides
     * @return true si le formulaire est valide, false sinon
     */
    private boolean validateForm() {
        errorLabel.setVisible(false);
        
        if (contratCombo.getValue() == null) {
            showError("Veuillez sélectionner un contrat.");
            return false;
        }
        
        if (ValidationUtil.isNullOrEmpty(montantField.getText())) {
            showError("Veuillez saisir un montant.");
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
        
        if (datePaiementPicker.getValue() == null) {
            showError("Veuillez sélectionner une date de paiement.");
            return false;
        }
        
        if (methodeCombo.getValue() == null) {
            showError("Veuillez sélectionner une méthode de paiement.");
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

package com.rentcar.app.models;

import java.time.LocalDate;

/**
 * Classe représentant un paiement
 */
public class Paiement {
    private int id;
    private int contratId;
    private double montant;
    private LocalDate datePaiement;
    private Methode methode;

    // Variable temporaire pour la relation
    private Contrat contrat;

    /**
     * Enumération des méthodes de paiement
     */
    public enum Methode {
        ESPECES("Espèces"),
        CARTE("Carte"),
        VIREMENT("Virement");

        private final String value;

        Methode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Methode fromString(String methode) {
            switch (methode) {
                case "Espèces":
                    return ESPECES;
                case "Carte":
                    return CARTE;
                case "Virement":
                    return VIREMENT;
                default:
                    return ESPECES;
            }
        }
    }

    // Constructeur par défaut
    public Paiement() {
    }

    // Constructeur avec paramètres
    public Paiement(int id, int contratId, double montant, LocalDate datePaiement, Methode methode) {
        this.id = id;
        this.contratId = contratId;
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.methode = methode;
    }

    // Constructeur sans ID (pour la création de nouveaux paiements)
    public Paiement(int contratId, double montant, LocalDate datePaiement, Methode methode) {
        this.contratId = contratId;
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.methode = methode;
    }

    // Constructeur avec objet Contrat
    public Paiement(int id, Contrat contrat, double montant, LocalDate datePaiement, Methode methode) {
        this.id = id;
        this.contrat = contrat;
        this.contratId = contrat.getId();
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.methode = methode;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContratId() {
        return contratId;
    }

    public void setContratId(int contratId) {
        this.contratId = contratId;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public Methode getMethode() {
        return methode;
    }

    public void setMethode(Methode methode) {
        this.methode = methode;
    }

    public Contrat getContrat() {
        return contrat;
    }

    public void setContrat(Contrat contrat) {
        this.contrat = contrat;
        if (contrat != null) {
            this.contratId = contrat.getId();
        }
    }

    @Override
    public String toString() {
        return "Paiement #" + id + " - " + montant + "€";
    }
}

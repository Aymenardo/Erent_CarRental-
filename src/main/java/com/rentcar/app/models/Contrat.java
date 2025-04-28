package com.rentcar.app.models;

import java.time.LocalDate;

/**
 * Classe représentant un contrat de location
 */
public class Contrat {
    private int id;
    private int clientId;
    private int voitureId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private double montant;

    // Variables temporaires pour les relations
    private Client client;
    private Voiture voiture;

    // Constructeur par défaut
    public Contrat() {
    }

    // Constructeur avec paramètres
    public Contrat(int id, int clientId, int voitureId, LocalDate dateDebut, LocalDate dateFin, double montant) {
        this.id = id;
        this.clientId = clientId;
        this.voitureId = voitureId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.montant = montant;
    }

    // Constructeur sans ID (pour la création de nouveaux contrats)
    public Contrat(int clientId, int voitureId, LocalDate dateDebut, LocalDate dateFin, double montant) {
        this.clientId = clientId;
        this.voitureId = voitureId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.montant = montant;
    }

    // Constructeur avec objets Client et Voiture
    public Contrat(int id, Client client, Voiture voiture, LocalDate dateDebut, LocalDate dateFin, double montant) {
        this.id = id;
        this.client = client;
        this.clientId = client.getId();
        this.voiture = voiture;
        this.voitureId = voiture.getId();
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.montant = montant;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getVoitureId() {
        return voitureId;
    }

    public void setVoitureId(int voitureId) {
        this.voitureId = voitureId;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
        if (client != null) {
            this.clientId = client.getId();
        }
    }

    public Voiture getVoiture() {
        return voiture;
    }

    public void setVoiture(Voiture voiture) {
        this.voiture = voiture;
        if (voiture != null) {
            this.voitureId = voiture.getId();
        }
    }

    @Override
    public String toString() {
        return "Contrat #" + id;
    }
}

package com.rentcar.app.models;

/**
 * Classe représentant un client
 */
public class Client {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    // Constructeur par défaut
    public Client() {
    }

    // Constructeur avec paramètres
    public Client(int id, String nom, String prenom, String email, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
    }

    // Constructeur sans ID (pour la création de nouveaux clients)
    public Client(String nom, String prenom, String email, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return prenom + " " + nom;
    }

    // Méthode pour obtenir le nom complet
    public String getNomComplet() {
        return prenom + " " + nom;
    }
}

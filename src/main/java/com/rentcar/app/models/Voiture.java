package com.rentcar.app.models;

/**
 * Classe représentant une voiture
 */
public class Voiture {
    private int id;
    private String marque;
    private String modele;
    private String immatriculation;
    private Statut statut;

    /**
     * Enumération des statuts de voiture
     */
    public enum Statut {
        DISPONIBLE("Disponible"),
        LOUE("Loué");

        private final String value;

        Statut(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Statut fromString(String statut) {
            if (statut.equals("Loué")) {
                return LOUE;
            } else {
                return DISPONIBLE;
            }
        }
    }

    // Constructeur par défaut
    public Voiture() {
    }

    // Constructeur avec paramètres
    public Voiture(int id, String marque, String modele, String immatriculation, Statut statut) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.statut = statut;
    }

    // Constructeur sans ID (pour la création de nouvelles voitures)
    public Voiture(String marque, String modele, String immatriculation, Statut statut) {
        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.statut = statut;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return marque + " " + modele + " (" + immatriculation + ")";
    }
}

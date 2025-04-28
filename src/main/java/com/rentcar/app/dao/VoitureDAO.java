package com.rentcar.app.dao;

import com.rentcar.app.config.DatabaseConfig;
import com.rentcar.app.models.Voiture;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des voitures
 */
public class VoitureDAO {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    /**
     * Crée une nouvelle voiture
     * @param voiture Voiture à créer
     * @return true si l'opération a réussi, false sinon
     */
    public boolean create(Voiture voiture) {
        String sql = "INSERT INTO voitures (marque, modele, immatriculation, statut) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, voiture.getMarque());
            stmt.setString(2, voiture.getModele());
            stmt.setString(3, voiture.getImmatriculation());
            stmt.setString(4, voiture.getStatut().getValue());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        voiture.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la voiture: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Récupère toutes les voitures
     * @return Liste des voitures
     */
    public List<Voiture> findAll() {
        List<Voiture> voitures = new ArrayList<>();
        String sql = "SELECT * FROM voitures ORDER BY marque, modele";
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                voitures.add(new Voiture(
                    rs.getInt("id"),
                    rs.getString("marque"),
                    rs.getString("modele"),
                    rs.getString("immatriculation"),
                    Voiture.Statut.fromString(rs.getString("statut"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des voitures: " + e.getMessage());
        }
        
        return voitures;
    }

    /**
     * Récupère toutes les voitures disponibles
     * @return Liste des voitures disponibles
     */
    public List<Voiture> findAvailable() {
        List<Voiture> voitures = new ArrayList<>();
        String sql = "SELECT * FROM voitures WHERE statut = 'Disponible' ORDER BY marque, modele";
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                voitures.add(new Voiture(
                    rs.getInt("id"),
                    rs.getString("marque"),
                    rs.getString("modele"),
                    rs.getString("immatriculation"),
                    Voiture.Statut.fromString(rs.getString("statut"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des voitures disponibles: " + e.getMessage());
        }
        
        return voitures;
    }

    /**
     * Récupère une voiture par son ID
     * @param id ID de la voiture
     * @return La voiture si elle existe, null sinon
     */
    public Voiture findById(int id) {
        String sql = "SELECT * FROM voitures WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Voiture(
                        rs.getInt("id"),
                        rs.getString("marque"),
                        rs.getString("modele"),
                        rs.getString("immatriculation"),
                        Voiture.Statut.fromString(rs.getString("statut"))
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la voiture: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Met à jour une voiture
     * @param voiture Voiture à mettre à jour
     * @return true si l'opération a réussi, false sinon
     */
    public boolean update(Voiture voiture) {
        String sql = "UPDATE voitures SET marque = ?, modele = ?, immatriculation = ?, statut = ? WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, voiture.getMarque());
            stmt.setString(2, voiture.getModele());
            stmt.setString(3, voiture.getImmatriculation());
            stmt.setString(4, voiture.getStatut().getValue());
            stmt.setInt(5, voiture.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la voiture: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Supprime une voiture
     * @param id ID de la voiture à supprimer
     * @return true si l'opération a réussi, false sinon
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM voitures WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la voiture: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Vérifie si une immatriculation existe déjà
     * @param immatriculation Immatriculation à vérifier
     * @param idToExclude ID à exclure (pour les mises à jour)
     * @return true si l'immatriculation existe déjà, false sinon
     */
    public boolean immatriculationExists(String immatriculation, int idToExclude) {
        String sql = "SELECT COUNT(*) FROM voitures WHERE immatriculation = ? AND id != ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, immatriculation);
            stmt.setInt(2, idToExclude);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'immatriculation: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Met à jour le statut d'une voiture
     * @param id ID de la voiture
     * @param statut Nouveau statut
     * @return true si l'opération a réussi, false sinon
     */
    public boolean updateStatus(int id, Voiture.Statut statut) {
        String sql = "UPDATE voitures SET statut = ? WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, statut.getValue());
            stmt.setInt(2, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut de la voiture: " + e.getMessage());
        }
        
        return false;
    }
}

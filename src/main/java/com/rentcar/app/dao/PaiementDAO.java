package com.rentcar.app.dao;

import com.rentcar.app.config.DatabaseConfig;
import com.rentcar.app.models.Contrat;
import com.rentcar.app.models.Paiement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des paiements
 */
public class PaiementDAO {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();
    private final ContratDAO contratDAO = new ContratDAO();

    /**
     * Crée un nouveau paiement
     * @param paiement Paiement à créer
     * @return true si l'opération a réussi, false sinon
     */
    public boolean create(Paiement paiement) {
        String sql = "INSERT INTO paiements (contrat_id, montant, date_paiement, methode) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, paiement.getContratId());
            stmt.setDouble(2, paiement.getMontant());
            stmt.setDate(3, Date.valueOf(paiement.getDatePaiement()));
            stmt.setString(4, paiement.getMethode().getValue());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        paiement.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du paiement: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Récupère tous les paiements
     * @return Liste des paiements
     */
    public List<Paiement> findAll() {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiements ORDER BY date_paiement DESC";
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Paiement paiement = new Paiement(
                    rs.getInt("id"),
                    rs.getInt("contrat_id"),
                    rs.getDouble("montant"),
                    rs.getDate("date_paiement").toLocalDate(),
                    Paiement.Methode.fromString(rs.getString("methode"))
                );
                
                // Récupérer le contrat associé
                Contrat contrat = contratDAO.findById(rs.getInt("contrat_id"));
                paiement.setContrat(contrat);
                
                paiements.add(paiement);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des paiements: " + e.getMessage());
        }
        
        return paiements;
    }

    /**
     * Récupère les paiements pour un contrat donné
     * @param contratId ID du contrat
     * @return Liste des paiements du contrat
     */
    public List<Paiement> findByContratId(int contratId) {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiements WHERE contrat_id = ? ORDER BY date_paiement DESC";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, contratId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Paiement paiement = new Paiement(
                        rs.getInt("id"),
                        rs.getInt("contrat_id"),
                        rs.getDouble("montant"),
                        rs.getDate("date_paiement").toLocalDate(),
                        Paiement.Methode.fromString(rs.getString("methode"))
                    );
                    
                    // Récupérer le contrat associé
                    Contrat contrat = contratDAO.findById(contratId);
                    paiement.setContrat(contrat);
                    
                    paiements.add(paiement);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des paiements du contrat: " + e.getMessage());
        }
        
        return paiements;
    }

    /**
     * Récupère un paiement par son ID
     * @param id ID du paiement
     * @return Le paiement s'il existe, null sinon
     */
    public Paiement findById(int id) {
        String sql = "SELECT * FROM paiements WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Paiement paiement = new Paiement(
                        rs.getInt("id"),
                        rs.getInt("contrat_id"),
                        rs.getDouble("montant"),
                        rs.getDate("date_paiement").toLocalDate(),
                        Paiement.Methode.fromString(rs.getString("methode"))
                    );
                    
                    // Récupérer le contrat associé
                    Contrat contrat = contratDAO.findById(rs.getInt("contrat_id"));
                    paiement.setContrat(contrat);
                    
                    return paiement;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du paiement: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Met à jour un paiement
     * @param paiement Paiement à mettre à jour
     * @return true si l'opération a réussi, false sinon
     */
    public boolean update(Paiement paiement) {
        String sql = "UPDATE paiements SET contrat_id = ?, montant = ?, date_paiement = ?, methode = ? WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, paiement.getContratId());
            stmt.setDouble(2, paiement.getMontant());
            stmt.setDate(3, Date.valueOf(paiement.getDatePaiement()));
            stmt.setString(4, paiement.getMethode().getValue());
            stmt.setInt(5, paiement.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du paiement: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Supprime un paiement
     * @param id ID du paiement à supprimer
     * @return true si l'opération a réussi, false sinon
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM paiements WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du paiement: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Calcule le total des paiements pour un contrat
     * @param contratId ID du contrat
     * @return Montant total des paiements
     */
    public double getTotalPaiementsByContrat(int contratId) {
        String sql = "SELECT SUM(montant) as total FROM paiements WHERE contrat_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, contratId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du total des paiements: " + e.getMessage());
        }
        
        return 0.0;
    }
}

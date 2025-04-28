package com.rentcar.app.dao;

import com.rentcar.app.config.DatabaseConfig;
import com.rentcar.app.models.Client;
import com.rentcar.app.models.Contrat;
import com.rentcar.app.models.Voiture;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des contrats
 */
public class ContratDAO {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();
    private final ClientDAO clientDAO = new ClientDAO();
    private final VoitureDAO voitureDAO = new VoitureDAO();

    /**
     * Crée un nouveau contrat
     * @param contrat Contrat à créer
     * @return true si l'opération a réussi, false sinon
     */
    public boolean create(Contrat contrat) {
        String sql = "INSERT INTO contrats (client_id, voiture_id, date_debut, date_fin, montant) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, contrat.getClientId());
            stmt.setInt(2, contrat.getVoitureId());
            stmt.setDate(3, Date.valueOf(contrat.getDateDebut()));
            stmt.setDate(4, Date.valueOf(contrat.getDateFin()));
            stmt.setDouble(5, contrat.getMontant());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        contrat.setId(generatedKeys.getInt(1));
                        
                        // Mettre à jour le statut de la voiture
                        voitureDAO.updateStatus(contrat.getVoitureId(), Voiture.Statut.LOUE);
                        
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du contrat: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Récupère tous les contrats avec les informations client et voiture
     * @return Liste des contrats
     */
    public List<Contrat> findAll() {
        List<Contrat> contrats = new ArrayList<>();
        String sql = "SELECT c.*, cl.nom, cl.prenom, cl.email, cl.telephone, " +
                     "v.marque, v.modele, v.immatriculation, v.statut " +
                     "FROM contrats c " +
                     "JOIN clients cl ON c.client_id = cl.id " +
                     "JOIN voitures v ON c.voiture_id = v.id " +
                     "ORDER BY c.date_debut DESC";
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Client client = new Client(
                    rs.getInt("client_id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("telephone")
                );
                
                Voiture voiture = new Voiture(
                    rs.getInt("voiture_id"),
                    rs.getString("marque"),
                    rs.getString("modele"),
                    rs.getString("immatriculation"),
                    Voiture.Statut.fromString(rs.getString("statut"))
                );
                
                Contrat contrat = new Contrat(
                    rs.getInt("id"),
                    rs.getInt("client_id"),
                    rs.getInt("voiture_id"),
                    rs.getDate("date_debut").toLocalDate(),
                    rs.getDate("date_fin").toLocalDate(),
                    rs.getDouble("montant")
                );
                
                contrat.setClient(client);
                contrat.setVoiture(voiture);
                
                contrats.add(contrat);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des contrats: " + e.getMessage());
        }
        
        return contrats;
    }

    /**
     * Récupère un contrat par son ID avec les informations client et voiture
     * @param id ID du contrat
     * @return Le contrat s'il existe, null sinon
     */
    public Contrat findById(int id) {
        String sql = "SELECT c.*, cl.nom, cl.prenom, cl.email, cl.telephone, " +
                     "v.marque, v.modele, v.immatriculation, v.statut " +
                     "FROM contrats c " +
                     "JOIN clients cl ON c.client_id = cl.id " +
                     "JOIN voitures v ON c.voiture_id = v.id " +
                     "WHERE c.id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Client client = new Client(
                        rs.getInt("client_id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone")
                    );
                    
                    Voiture voiture = new Voiture(
                        rs.getInt("voiture_id"),
                        rs.getString("marque"),
                        rs.getString("modele"),
                        rs.getString("immatriculation"),
                        Voiture.Statut.fromString(rs.getString("statut"))
                    );
                    
                    Contrat contrat = new Contrat(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        rs.getInt("voiture_id"),
                        rs.getDate("date_debut").toLocalDate(),
                        rs.getDate("date_fin").toLocalDate(),
                        rs.getDouble("montant")
                    );
                    
                    contrat.setClient(client);
                    contrat.setVoiture(voiture);
                    
                    return contrat;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du contrat: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Recherche les contrats pour un client donné
     * @param clientId ID du client
     * @return Liste des contrats du client
     */
    public List<Contrat> findByClientId(int clientId) {
        List<Contrat> contrats = new ArrayList<>();
        String sql = "SELECT c.*, v.marque, v.modele, v.immatriculation, v.statut " +
                     "FROM contrats c " +
                     "JOIN voitures v ON c.voiture_id = v.id " +
                     "WHERE c.client_id = ? " +
                     "ORDER BY c.date_debut DESC";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Client client = clientDAO.findById(clientId);
                    
                    Voiture voiture = new Voiture(
                        rs.getInt("voiture_id"),
                        rs.getString("marque"),
                        rs.getString("modele"),
                        rs.getString("immatriculation"),
                        Voiture.Statut.fromString(rs.getString("statut"))
                    );
                    
                    Contrat contrat = new Contrat(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        rs.getInt("voiture_id"),
                        rs.getDate("date_debut").toLocalDate(),
                        rs.getDate("date_fin").toLocalDate(),
                        rs.getDouble("montant")
                    );
                    
                    contrat.setClient(client);
                    contrat.setVoiture(voiture);
                    
                    contrats.add(contrat);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des contrats du client: " + e.getMessage());
        }
        
        return contrats;
    }

    /**
     * Met à jour un contrat
     * @param contrat Contrat à mettre à jour
     * @return true si l'opération a réussi, false sinon
     */
    public boolean update(Contrat contrat) {
        String sql = "UPDATE contrats SET client_id = ?, voiture_id = ?, date_debut = ?, date_fin = ?, montant = ? WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, contrat.getClientId());
            stmt.setInt(2, contrat.getVoitureId());
            stmt.setDate(3, Date.valueOf(contrat.getDateDebut()));
            stmt.setDate(4, Date.valueOf(contrat.getDateFin()));
            stmt.setDouble(5, contrat.getMontant());
            stmt.setInt(6, contrat.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du contrat: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Supprime un contrat
     * @param id ID du contrat à supprimer
     * @return true si l'opération a réussi, false sinon
     */
    public boolean delete(int id) {
        // D'abord récupérer le contrat pour connaître la voiture associée
        Contrat contrat = findById(id);
        if (contrat == null) {
            return false;
        }
        
        String sql = "DELETE FROM contrats WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                // Mettre à jour le statut de la voiture
                voitureDAO.updateStatus(contrat.getVoitureId(), Voiture.Statut.DISPONIBLE);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du contrat: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Vérifie si une voiture est disponible pour une période donnée
     * @param voitureId ID de la voiture
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @param contratIdToExclude ID du contrat à exclure (pour les mises à jour)
     * @return true si la voiture est disponible, false sinon
     */
    public boolean isVoitureAvailable(int voitureId, LocalDate dateDebut, LocalDate dateFin, int contratIdToExclude) {
        String sql = "SELECT COUNT(*) FROM contrats " +
                     "WHERE voiture_id = ? AND id != ? AND " +
                     "((date_debut BETWEEN ? AND ?) OR " +
                     "(date_fin BETWEEN ? AND ?) OR " +
                     "(date_debut <= ? AND date_fin >= ?))";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, voitureId);
            stmt.setInt(2, contratIdToExclude);
            stmt.setDate(3, Date.valueOf(dateDebut));
            stmt.setDate(4, Date.valueOf(dateFin));
            stmt.setDate(5, Date.valueOf(dateDebut));
            stmt.setDate(6, Date.valueOf(dateFin));
            stmt.setDate(7, Date.valueOf(dateDebut));
            stmt.setDate(8, Date.valueOf(dateFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de disponibilité: " + e.getMessage());
        }
        
        return false;
    }
}

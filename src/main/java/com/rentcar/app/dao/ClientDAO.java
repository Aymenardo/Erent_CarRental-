package com.rentcar.app.dao;

import com.rentcar.app.config.DatabaseConfig;
import com.rentcar.app.models.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des clients
 */
public class ClientDAO {
    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    /**
     * Crée un nouveau client
     * @param client Client à créer
     * @return true si l'opération a réussi, false sinon
     */
    public boolean create(Client client) {
        String sql = "INSERT INTO clients (nom, prenom, email, telephone) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getTelephone());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        client.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du client: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Récupère tous les clients
     * @return Liste des clients
     */
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients ORDER BY nom, prenom";
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                clients.add(new Client(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("telephone")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des clients: " + e.getMessage());
        }
        
        return clients;
    }

    /**
     * Recherche des clients par nom ou prénom
     * @param recherche Chaîne de recherche
     * @return Liste des clients correspondants
     */
    public List<Client> findByName(String recherche) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom, prenom";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchTerm = "%" + recherche + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clients.add(new Client(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des clients: " + e.getMessage());
        }
        
        return clients;
    }

    /**
     * Récupère un client par son ID
     * @param id ID du client
     * @return Le client s'il existe, null sinon
     */
    public Client findById(int id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Client(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du client: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Met à jour un client
     * @param client Client à mettre à jour
     * @return true si l'opération a réussi, false sinon
     */
    public boolean update(Client client) {
        String sql = "UPDATE clients SET nom = ?, prenom = ?, email = ?, telephone = ? WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getTelephone());
            stmt.setInt(5, client.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du client: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Supprime un client
     * @param id ID du client à supprimer
     * @return true si l'opération a réussi, false sinon
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du client: " + e.getMessage());
        }
        
        return false;
    }
}

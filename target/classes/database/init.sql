-- Script d'initialisation de la base de données pour l'application de Gestion des Locations

-- Suppression de la base de données si elle existe déjà
DROP DATABASE IF EXISTS gestion_locations;

-- Création de la base de données
CREATE DATABASE gestion_locations;
USE gestion_locations;

-- Création de la table des utilisateurs
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Standard') NOT NULL DEFAULT 'Standard'
);

-- Création de la table des clients
CREATE TABLE clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) CHECK (email LIKE '%@%.%'),
    telephone VARCHAR(20) CHECK (telephone REGEXP '^[0-9]+$')
);

-- Création de la table des voitures
CREATE TABLE voitures (
    id INT AUTO_INCREMENT PRIMARY KEY,
    marque VARCHAR(50) NOT NULL,
    modele VARCHAR(50) NOT NULL,
    immatriculation VARCHAR(20) UNIQUE NOT NULL,
    statut ENUM('Disponible', 'Loué') NOT NULL DEFAULT 'Disponible'
);

-- Création de la table des contrats
CREATE TABLE contrats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    voiture_id INT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    montant DOUBLE NOT NULL CHECK (montant > 0),
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
    FOREIGN KEY (voiture_id) REFERENCES voitures(id) ON DELETE CASCADE,
    CHECK (date_fin > date_debut)
);

-- Création de la table des paiements
CREATE TABLE paiements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    contrat_id INT NOT NULL,
    montant DOUBLE NOT NULL CHECK (montant > 0),
    date_paiement DATE NOT NULL,
    methode ENUM('Espèces', 'Carte', 'Virement') NOT NULL,
    FOREIGN KEY (contrat_id) REFERENCES contrats(id) ON DELETE CASCADE
);

-- Insertion d'un administrateur par défaut
-- Mot de passe: admin (haché avec SHA-256)
INSERT INTO users (username, password, role) VALUES 
('admin', 'jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=', 'Admin');

-- Insertion d'un utilisateur standard
-- Mot de passe: user (haché avec SHA-256)
INSERT INTO users (username, password, role) VALUES 
('user', 'n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg=', 'Standard');

-- Insertion de données de démonstration pour les clients
INSERT INTO clients (nom, prenom, email, telephone) VALUES
('Martin', 'Jean', 'jean.martin@email.com', '0612345678'),
('Dubois', 'Marie', 'marie.dubois@email.com', '0698765432'),
('Leroy', 'Pierre', 'pierre.leroy@email.com', '0712345678'),
('Moreau', 'Sophie', 'sophie.moreau@email.com', '0723456789');

-- Insertion de données de démonstration pour les voitures
INSERT INTO voitures (marque, modele, immatriculation, statut) VALUES
('Renault', 'Clio', 'AA-123-BB', 'Disponible'),
('Peugeot', '308', 'CC-456-DD', 'Disponible'),
('Citroen', 'C3', 'EE-789-FF', 'Disponible'),
('Dacia', 'Sandero', 'GG-012-HH', 'Disponible'),
('Toyota', 'Yaris', 'II-345-JJ', 'Disponible');

-- Message de confirmation
SELECT 'Base de données gestion_locations initialisée avec succès!' AS 'Info';

package com.rentcar.app.models;

/**
 * Classe représentant un utilisateur du système
 */
public class User {
    private int id;
    private String username;
    private String password;
    private Role role;

    /**
     * Enumération des rôles utilisateur
     */
    public enum Role {
        ADMIN("Admin"),
        STANDARD("Standard");

        private final String value;

        Role(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Role fromString(String role) {
            if (role.equals("Admin")) {
                return ADMIN;
            } else {
                return STANDARD;
            }
        }
    }

    // Constructeur par défaut
    public User() {
    }

    // Constructeur avec paramètres
    public User(int id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Constructeur sans ID (pour la création de nouveaux utilisateurs)
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}

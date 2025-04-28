package com.rentcar.app.utils;

import com.rentcar.app.models.User;

/**
 * Gestionnaire de session utilisateur (Singleton)
 */
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {
    }

    /**
     * Obtient l'instance unique
     * @return L'instance du SessionManager
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Définit l'utilisateur courant
     * @param user Utilisateur connecté
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Obtient l'utilisateur courant
     * @return L'utilisateur actuellement connecté
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Vérifie si l'utilisateur courant est un administrateur
     * @return true si l'utilisateur est admin, false sinon
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.Role.ADMIN;
    }

    /**
     * Déconnecte l'utilisateur courant
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Vérifie si un utilisateur est connecté
     * @return true si un utilisateur est connecté, false sinon
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}

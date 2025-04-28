package com.rentcar.app.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utilitaire pour la gestion des mots de passe
 */
public class PasswordUtil {

    /**
     * Hache un mot de passe avec SHA-256
     * @param passwordToHash Mot de passe en clair
     * @return Le hash du mot de passe
     */
    public static String hashPassword(String passwordToHash) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hashage du mot de passe", e);
        }
    }

    /**
     * Vérifie si un mot de passe en clair correspond à un hash
     * @param passwordToCheck Mot de passe en clair
     * @param storedHash Hash stocké
     * @return true si le mot de passe correspond, false sinon
     */
    public static boolean verifyPassword(String passwordToCheck, String storedHash) {
        String hashedPassword = hashPassword(passwordToCheck);
        return hashedPassword.equals(storedHash);
    }
}

package com.rentcar.app.utils;

import java.util.regex.Pattern;

/**
 * Utilitaire de validation des données
 */
public class ValidationUtil {
    // Regex pour valider un email
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    // Regex pour valider un numéro de téléphone (chiffres uniquement)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]+$");

    /**
     * Vérifie si une chaîne est vide ou null
     * @param str Chaîne à vérifier
     * @return true si la chaîne est vide ou null, false sinon
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Vérifie si un email est valide
     * @param email Email à vérifier
     * @return true si l'email est valide, false sinon
     */
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Vérifie si un numéro de téléphone est valide (chiffres uniquement)
     * @param phone Numéro de téléphone à vérifier
     * @return true si le téléphone est valide, false sinon
     */
    public static boolean isValidPhone(String phone) {
        if (isNullOrEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Vérifie si une valeur numérique est positive
     * @param value Valeur à vérifier
     * @return true si la valeur est positive, false sinon
     */
    public static boolean isPositive(double value) {
        return value > 0;
    }

    /**
     * Vérifie si une chaîne peut être convertie en nombre
     * @param str Chaîne à vérifier
     * @return true si la chaîne est un nombre valide, false sinon
     */
    public static boolean isNumeric(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

package com.mycompany.licoreria.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar contraseña", e);
        }
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }

    // MÉTODO PARA DEBUG - Verificar el hash generado
    public static void debugPassword(String password) {
        String hash = hashPassword(password);
        System.out.println("Contraseña: " + password);
        System.out.println("SHA-256: " + hash);
        System.out.println("Longitud: " + hash.length());
    }
}
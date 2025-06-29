package com.example.hospedagens.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Classe utilitária que faz o hash das senhas
public class PasswordUtils {

    // Hash com MessageDigest (achei mais simples que com o BCrypt)
    public static String hashPassword(String password) {
        // Trata exceção para o caso de o algoritmo SHA-256 não estar implementado no ambiente Java
        try {
            // Gera valor de hash de 256 bits
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            // Converte os bytes obtidos para sua versão hexadecimal de 2 dígitos (32 bytes)
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            // Retorna para o armazenamento no Banco
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Se encontrar a exceção, vai exibir na saída o caminho da pilha que teve problema (essa aqui), para possível depuração.
            e.printStackTrace();
            return null;
        }
    }
}
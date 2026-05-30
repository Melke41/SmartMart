package smartmart.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    public static String hashPassword(String plainText) {
        if (plainText == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(plainText.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("NoSuchAlgorithmException: " + e.getMessage());
            return null;
        }
    }

    public static boolean checkPassword(String plainText, String hashedPassword) {
        if (plainText == null || hashedPassword == null) {
            return false;
        }
        String hashOfInput = hashPassword(plainText);
        return hashedPassword.equals(hashOfInput);
    }
}

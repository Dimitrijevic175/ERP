package com.dimitrijevic175.user_service.configuration;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Hešovanje lozinke
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Provera da li se uneta lozinka poklapa sa hešovanom
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}

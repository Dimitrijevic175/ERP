package com.dimitrijevic175.user_service;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        // Generiše 256-bitni HMAC ključ za HS256 algoritam
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        // Konvertuje ključ u Base64 string
        String encodedKey = Encoders.BASE64.encode(key.getEncoded());

        // Ispisuje Base64 ključ u konzolu
        System.out.println("Your new secure JWT secret:");
        System.out.println(encodedKey);
    }
}

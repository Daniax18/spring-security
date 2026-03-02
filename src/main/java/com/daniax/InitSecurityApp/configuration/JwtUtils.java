package com.daniax.InitSecurityApp.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitaire de gestion des tokens JWT (JSON Web Token).
 *
 * <p>
 * Cette classe est responsable de :
 * <ul>
 *     <li>La génération des tokens JWT</li>
 *     <li>La signature des tokens avec une clé secrète</li>
 *     <li>La validation des tokens (utilisateur + expiration)</li>
 *     <li>L'extraction des informations contenues dans le token</li>
 * </ul>
 *
 * <p>
 * Les tokens générés sont utilisés pour l'authentification stateless
 * dans Spring Security (sans session serveur).
 */
@Component
public class JwtUtils {
    @Value("${app.secret-key}")
    private String secretKey;

    @Value("${app.expiration-time}")
    private long expirationTime;

    /**
     * Génère un token JWT pour un utilisateur donné.
     *
     * <p>
     * Le nom d'utilisateur est stocké dans le champ {@code subject} du token.
     *
     * @param username nom d'utilisateur à inclure dans le token
     * @return token JWT signé et valide
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Crée un token JWT avec les claims fournis et un sujet donné.
     *
     * <p>
     * Le token contient :
     * <ul>
     *     <li>Les claims personnalisés (s'il y en a)</li>
     *     <li>Le sujet (username)</li>
     *     <li>La date de création</li>
     *     <li>La date d'expiration</li>
     * </ul>
     *
     * @param claims  données personnalisées à inclure dans le token
     * @param subject sujet du token (généralement le username)
     * @return token JWT signé
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Génère la clé de signature utilisée pour signer et vérifier le token JWT.
     *
     * <p>
     * La clé est construite à partir de la clé secrète définie
     * dans les propriétés de l'application.
     *
     * @return clé cryptographique de signature
     */
    private Key getSignKey() {
        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * Valide un token JWT par rapport à un utilisateur donné.
     *
     * <p>
     * Un token est considéré comme valide si :
     * <ul>
     *     <li>Le username du token correspond à celui de l'utilisateur</li>
     *     <li>Le token n'est pas expiré</li>
     * </ul>
     *
     * @param token       token JWT à valider
     * @param userDetails informations de l'utilisateur authentifié
     * @return {@code true} si le token est valide, {@code false} sinon
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Vérifie si un token JWT est expiré.
     *
     * @param token token JWT
     * @return {@code true} si le token est expiré, {@code false} sinon
     */
    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    /**
     * Extrait le nom d'utilisateur (subject) contenu dans le token JWT.
     *
     * @param token token JWT
     * @return nom d'utilisateur extrait du token
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extrait la date d'expiration contenue dans le token JWT.
     *
     * @param token token JWT
     * @return date d'expiration du token
     */
    private Date extractExpirationDate(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * Extrait l'ensemble des claims contenus dans un token JWT.
     *
     * <p>
     * Cette méthode vérifie automatiquement :
     * <ul>
     *     <li>La signature du token</li>
     *     <li>L'intégrité des données</li>
     * </ul>
     *
     * @param token token JWT
     * @return claims extraits du token
     * @throws io.jsonwebtoken.JwtException si le token est invalide ou corrompu
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey()) // même clé que pour signer
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

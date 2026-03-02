package com.daniax.InitSecurityApp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration principale de la sécurité Spring Security.
 *
 * <p>
 * Cette classe définit :
 * <ul>
 *     <li>La chaîne de filtres de sécurité HTTP</li>
 *     <li>Les règles d'autorisation des endpoints</li>
 *     <li>La gestion de l'authentification via JWT</li>
 *     <li>Les beans liés à l'authentification et au chiffrement des mots de passe</li>
 * </ul>
 *
 * <p>
 * L'application utilise une approche <b>stateless</b> :
 * <ul>
 *     <li>Aucune session serveur</li>
 *     <li>L'authentification repose uniquement sur le token JWT</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Utilitaire de gestion des tokens JWT.
     */
    private final JwtUtils jwtUtils;

    /**
     * Service Spring Security permettant de charger les utilisateurs
     * depuis la base de données.
     */
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Définition de la chaîne de filtres Spring Security.
     *
     * <p>
     * Cette configuration :
     * <ul>
     *     <li>Désactive la protection CSRF (API REST stateless) => Pas bon en prod</li>
     *     <li>Autorise l'accès libre aux endpoints d'authentification</li>
     *     <li>Protège tous les autres endpoints par authentification JWT</li>
     *     <li>Ajoute un filtre JWT personnalisé avant le filtre natif Spring</li>
     * </ul>
     *
     * @param http configuration HTTP de Spring Security
     * @return chaîne de filtres de sécurité configurée
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/v1/auth/register", "/api/v1/auth/login").permitAll()
                                .anyRequest().authenticated())
                .addFilterBefore(new JwtConfig(userDetailsService, jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Bean permettant le chiffrement des mots de passe utilisateurs.
     *
     * <p>
     * Utilise l'algorithme BCrypt, recommandé par Spring Security
     * pour le stockage sécurisé des mots de passe.
     *
     * @return encodeur de mots de passe BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuration de l'AuthenticationManager.
     *
     * <p>
     * Ce composant est utilisé lors du processus de login pour :
     * <ul>
     *     <li>Vérifier les identifiants utilisateur</li>
     *     <li>Comparer les mots de passe chiffrés</li>
     * </ul>
     *
     * @param http configuration HTTP de Spring Security
     * @param passwordEncoder encodeur de mots de passe
     * @return gestionnaire d'authentification configuré
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }
}

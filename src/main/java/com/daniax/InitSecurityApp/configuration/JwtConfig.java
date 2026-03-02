package com.daniax.InitSecurityApp.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtConfig extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    public JwtConfig(UserDetailsService userDetailsService, JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Filtre JWT exécuté à chaque requête HTTP entrante.
     *
     * <p>
     * Ce filtre intercepte les requêtes afin de :
     * <ul>
     *     <li>Extraire le token JWT depuis l'en-tête HTTP <b>Authorization</b></li>
     *     <li>Valider le token (signature, expiration, cohérence utilisateur)</li>
     *     <li>Authentifier l'utilisateur dans le contexte de sécurité Spring</li>
     * </ul>
     *
     * <p>
     * Si le token est valide, l'utilisateur est automatiquement authentifié
     * et ses rôles (authorities) sont injectés dans le {@link SecurityContextHolder}.
     * Cela permet ensuite à Spring Security de gérer l'autorisation
     * (ex: {@code @PreAuthorize}, {@code hasRole()}, etc.).
     *
     * <p>
     * Ce filtre est positionné avant {@link UsernamePasswordAuthenticationFilter}
     * dans la chaîne de sécurité.
     *
     * @param request  requête HTTP entrante
     * @param response réponse HTTP sortante
     * @param filterChain chaîne de filtres Spring Security
     * @throws ServletException en cas d'erreur liée au traitement du filtre
     * @throws IOException en cas d'erreur d'entrée/sortie
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            username = jwtUtils.extractUsername(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtils.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}

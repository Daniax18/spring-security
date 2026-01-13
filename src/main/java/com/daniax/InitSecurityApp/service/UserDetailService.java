package com.daniax.InitSecurityApp.service;

import com.daniax.InitSecurityApp.entity.User;
import com.daniax.InitSecurityApp.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Implémentation personnalisée du service {@link UserDetailsService}.
 *
 * <p>
 * Cette classe permet à Spring Security de charger les informations
 * d'un utilisateur depuis la base de données lors du processus
 * d'authentification et de validation du token JWT.
 *
 * <p>
 * Elle transforme l'entité métier {@link User} en un objet {@link org.springframework.security.core.userdetails.User}
 * compréhensible par Spring Security.
 */
@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;


    public UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Charge un utilisateur à partir de son nom d'utilisateur.
     *
     * <p>
     * Cette méthode est appelée automatiquement par Spring Security :
     * <ul>
     *     <li>Lors de l'authentification (login)</li>
     *     <li>Lors de la validation d'un token JWT</li>
     * </ul>
     *
     * <p>
     * Le rôle de l'utilisateur est converti en autorité Spring Security en respectant la convention {@code ROLE_}.
     *
     * @param username nom d'utilisateur fourni lors de l'authentification
     * @return {@link UserDetails} contenant les informations de sécurité de l'utilisateur
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        String role = "ROLE_" + user.getRole().name();

        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getMdp(),
                Collections.singletonList(new SimpleGrantedAuthority(role)));
    }
}

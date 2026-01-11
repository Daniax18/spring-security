package com.daniax.InitSecurityApp.service;

import com.daniax.InitSecurityApp.entity.User;
import com.daniax.InitSecurityApp.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;


    public UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


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

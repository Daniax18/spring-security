package com.daniax.InitSecurityApp.service;

import com.daniax.InitSecurityApp.entity.User;
import com.daniax.InitSecurityApp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User create(User user) throws Exception{
        if(userRepository.findByUserName(user.getUserName()) != null) throw new Exception("User already exists");

        user.setMdp(passwordEncoder.encode(user.getMdp()));
        return userRepository.save(user);
    }
}

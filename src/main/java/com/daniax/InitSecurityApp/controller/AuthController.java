package com.daniax.InitSecurityApp.controller;

import com.daniax.InitSecurityApp.configuration.JwtUtils;
import com.daniax.InitSecurityApp.dto.LoginUserDTO;
import com.daniax.InitSecurityApp.dto.UserDTO;
import com.daniax.InitSecurityApp.entity.User;
import com.daniax.InitSecurityApp.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDTO loginUserDTO){
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUserDTO.getUserName(), loginUserDTO.getMdp()));
            if(authentication.isAuthenticated()){
                Map<String, Object> authData = new HashMap<>();
                authData.put("token", jwtUtils.generateToken(loginUserDTO.getUserName()));
                authData.put("type", "Bearer");
                return new ResponseEntity<>(authData, HttpStatus.OK);
            }
            return new ResponseEntity<>("Invalid mdp or user", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid mdp or user", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDto){
        try {
            User user = new User(userDto.getUserName(), userDto.getEmail(), userDto.getMdp(), userDto.getRole());
            return new ResponseEntity<>(authService.create(user), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}

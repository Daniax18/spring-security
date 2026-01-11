package com.daniax.InitSecurityApp.controller;

import com.daniax.InitSecurityApp.dto.UserDTO;
import com.daniax.InitSecurityApp.entity.User;
import com.daniax.InitSecurityApp.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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

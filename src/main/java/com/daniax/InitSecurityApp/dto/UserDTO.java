package com.daniax.InitSecurityApp.dto;

public class UserDTO {
    private String userName;
    private String email;
    private String mdp;
    private int role;

    public UserDTO() {
    }

    public UserDTO(String userName, String email, String mdp, int role) {
        this.userName = userName;
        this.email = email;
        this.mdp = mdp;
        this.role = role;
    }

    // Getters and setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}

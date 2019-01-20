package com.example.yuktmitash.testingaws;

public class User {
    private boolean isAdmin;
    private String username;
    private String email;
    private String password;
    private String fireid;


    public String getFireid() {
        return fireid;
    }

    public void setFireid(String fireid) {
        this.fireid = fireid;
    }

    public User(boolean isAdmin, String username, String email, String password, String fireid) {
        this.isAdmin = isAdmin;
        this.username = username;
        this.email = email;
        this.password = password;
        this.fireid = fireid;

    }

    public User() {}


    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

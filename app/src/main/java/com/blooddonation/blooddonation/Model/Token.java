package com.blooddonation.blooddonation.Model;

public class Token {

    private String token;
    private boolean isServer;
    private boolean ServerToken;
    private boolean admin;

    public Token() {
    }

    public Token(String token, boolean isServer, boolean serverToken, boolean admin) {
        this.token = token;
        this.isServer = isServer;
        ServerToken = serverToken;
        this.admin = admin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isServer() {
        return isServer;
    }

    public void setServer(boolean server) {
        isServer = server;
    }

    public boolean isServerToken() {
        return ServerToken;
    }

    public void setServerToken(boolean serverToken) {
        ServerToken = serverToken;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}

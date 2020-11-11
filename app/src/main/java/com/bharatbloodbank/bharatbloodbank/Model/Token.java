package com.bharatbloodbank.bharatbloodbank.Model;

public class Token {

    private String token;
    private boolean isServer;
    private boolean ServerToken;

    public Token() {
    }

    public Token(String token, boolean isServer, boolean serverToken) {
        this.token = token;
        this.isServer = isServer;
        ServerToken = serverToken;
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


}

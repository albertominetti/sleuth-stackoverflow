package com.example.demo;

public class ServerUnavailableException extends RuntimeException {
    public ServerUnavailableException(String message) {
        super(message);
    }
}

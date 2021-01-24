package com.example.demo;

import feign.Client;

public class SimpleClient extends Client.Default {
    public SimpleClient() {
        super(null, null);
    }
}

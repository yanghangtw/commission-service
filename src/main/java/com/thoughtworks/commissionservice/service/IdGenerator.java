package com.thoughtworks.commissionservice.service;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdGenerator {
    public String next() {
        return UUID.randomUUID().toString();
    }
}

package com.objectcomputing.checkins.security.authentication.srp6;

import lombok.Data;

@Data
public class Srp6Secrets {
    private String salt;
    private String verifier;

    public Srp6Secrets() {
    }

    public Srp6Secrets(String salt, String verifier) {
        this.salt = salt;
        this.verifier = verifier;
    }
}

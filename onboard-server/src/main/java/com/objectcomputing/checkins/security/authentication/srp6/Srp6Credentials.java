package com.objectcomputing.checkins.security.authentication.srp6;

import lombok.Data;

@Data
public class Srp6Credentials {
    private String identity;
    private String salt;
    private String verifier;

    public Srp6Credentials() {
    }

    public Srp6Credentials(String identity, String salt, String verifier) {
        this.identity = identity;
        this.salt = salt;
        this.verifier = verifier;
    }
}

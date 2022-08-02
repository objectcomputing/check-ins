package com.objectcomputing.checkins.security.authentication.srp6;

import lombok.Data;

@Data
public class Srp6Challenge {
    private String salt;
    private String b;

    public Srp6Challenge() {
    }

    public Srp6Challenge(String salt, String b) {
        this.salt = salt;
        this.b = b;
    }
}

package com.objectcomputing.checkins.auth.commons;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String identity;
    private String secret;
}

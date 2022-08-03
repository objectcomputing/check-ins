package com.objectcomputing.checkins.auth.commons;

import lombok.Data;

@Data
public class SecretsRequest {
    private String identity;
    private String secret;
}

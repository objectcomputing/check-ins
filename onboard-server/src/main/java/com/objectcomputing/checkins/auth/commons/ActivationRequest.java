package com.objectcomputing.checkins.auth.commons;

import lombok.Data;

@Data
public class ActivationRequest {
    private String identity;
    private String secret;
}

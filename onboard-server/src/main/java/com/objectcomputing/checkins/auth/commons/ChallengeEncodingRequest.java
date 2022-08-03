package com.objectcomputing.checkins.auth.commons;

import lombok.Data;

@Data
public class ChallengeEncodingRequest {
    private String identity;
    private String secret;
    private String salt;
    private String b;
}

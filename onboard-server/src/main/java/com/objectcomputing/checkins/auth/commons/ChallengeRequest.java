package com.objectcomputing.checkins.auth.commons;

import lombok.Data;

@Data
public class ChallengeRequest {
    private String identity;
    private String scope;
}

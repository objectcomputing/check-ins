package com.objectcomputing.geoai.platform.account.endpoint;

import lombok.Data;

@Data
public class UserAccountConfig {
    private String emailAddress;
    private String handle;

    private String firstName;
    private String lastName;

    private String salt;
    private String primaryVerifier;
    private String secondaryVerifier;
}

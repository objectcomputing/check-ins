package com.objectcomputing.checkins.services.system.endpoint;

import lombok.Data;

@Data
public class SystemAccountConfig {
    private String identity;
    private String salt;
    private String verifier;
    private Boolean administrator;
    private String requester;

}

package com.objectcomputing.checkins.services.system.endpoint;

import lombok.Data;

@Data
public class InitializationConfig {
    private String requester;
    private SystemAccountConfig systemOwner;
}

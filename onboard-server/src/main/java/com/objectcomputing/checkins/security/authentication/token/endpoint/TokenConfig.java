package com.objectcomputing.checkins.security.authentication.token.endpoint;

import com.objectcomputing.geoai.platform.token.model.TokenType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TokenConfig {
    private String roleName;
    private String displayName;
    private List<String> policies;
    private Map<String,String> meta;
    private boolean renewable = true;
    private String timeToLive;
    private TokenType type;
    private Integer maxNumberOfUses;
    private String maxTimeToLive;
}

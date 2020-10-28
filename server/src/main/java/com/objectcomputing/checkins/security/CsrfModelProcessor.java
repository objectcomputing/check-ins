package com.objectcomputing.checkins.security;

import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;


@Controller("/csrf")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class CsrfModelProcessor{

    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    @Get("/cookie")
    public Map getCsrfToken()  {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);

        return CollectionUtils.mapOf("_csrf", base64Encoder.encodeToString(bytes));

    }
}

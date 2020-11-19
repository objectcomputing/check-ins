package com.objectcomputing.checkins.security;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.cookie.SameSite;
import io.micronaut.http.netty.cookies.NettyCookie;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.security.SecureRandom;
import java.util.Base64;



@Controller("/csrf")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class CsrfModelProcessor{

    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    @Get("/cookie")
    public HttpResponse <?> getCsrfToken()  {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[24];
        random.nextBytes(randomBytes);
        String cookieValue = base64Encoder.encodeToString(randomBytes);

        return HttpResponse.ok()
        // set cookie
                .cookie(new NettyCookie("_csrf", cookieValue).path("/").sameSite(SameSite.Strict)).body(cookieValue);


    }
}

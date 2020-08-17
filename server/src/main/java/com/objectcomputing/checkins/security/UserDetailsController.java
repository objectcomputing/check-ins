package com.objectcomputing.checkins.security;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import java.util.*;

@Controller("/user")
@Secured(SecurityRule.IS_ANONYMOUS)
@Tag(name = "user")
public class UserDetailsController {

    /**
     * Get user details from Google authentication
     *
     * @param authentication {@link Authentication} or null
     * @return {@link HttpResponse<Map>}
     */
    @Get
    public HttpResponse<Map> userDetails(@Nullable Authentication authentication) {

        Map userinfo = new LinkedHashMap();

        if (authentication == null) {
            return HttpResponse
                    .ok()
                    .body(userinfo);
        }

        userinfo.put("email", authentication.getAttributes().get("email"));
        userinfo.put("image_url", authentication.getAttributes().get("picture"));

        return HttpResponse
                .ok()
                .body(userinfo);
    }
}

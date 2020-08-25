package com.objectcomputing.checkins.security;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;

import javax.annotation.Nullable;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
public class HomeController {

    @Get("/unauthorized")
    @View("unauthorized")
    public Map<String, Object> unauthorized(@Nullable Principal principal) {
        return new HashMap<>();
    }

    @Get("/forbidden")
    @View("forbidden")
    public Map<String, Object> forbidden(@Nullable Principal principal) {
        return new HashMap<>();
    }
}
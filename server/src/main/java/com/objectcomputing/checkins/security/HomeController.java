package com.objectcomputing.checkins.security;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
public class HomeController {

	@Get("/")
	@View("home")
	public Map<String, Object> home(@Nullable Principal principal) {
		
        if (principal == null) {
            return Collections.singletonMap("isLoggedIn", false);
        }
        return CollectionUtils.mapOf("isLoggedIn", true, "username", principal.getName());
	}

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
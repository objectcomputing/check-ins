package com.objectcomputing.checkins.security;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.context.annotation.Property;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
public class HomeController {

	@Property(name = "datasources.default.url")
	String jdbcUrl;

	private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

	@Get("/unauthorized")
	@View("unauthorized")
	public Map<String, Object> unauthorized(@Nullable Principal principal) {

		LOG.info("Value of jdbcUrl: " + jdbcUrl);
		return new HashMap<>();
	}

	@Get("/forbidden")
	@View("forbidden")
	public Map<String, Object> forbidden(@Nullable Principal principal) {
		return new HashMap<>();
	}
}
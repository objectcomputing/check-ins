package com.objectcomputing.checkins.security;

import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
public class OnboardingController {

    private final Environment environment;

    public OnboardingController(Environment environment) {
        this.environment = environment;
    }

    /**
     * Forwards any unmapped paths (except those containing a period) to the client {@code index.html}.
     * @return forward to client {@code index.html}.
     */
    @Get("/{path:^onboard[^\\.]*}")
    public Optional<StreamedFile> forward(String path) {
        return environment.getResource("public/onboard/index.html").map(StreamedFile::new);
    }
}

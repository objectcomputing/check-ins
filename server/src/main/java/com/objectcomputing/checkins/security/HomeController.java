package com.objectcomputing.checkins.security;

import io.micronaut.context.env.Environment;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@Secured(SecurityRule.IS_AUTHENTICATED)
public class HomeController {

    @Inject
    Environment environment;

    @Get("/forbidden")
    @View("forbidden")
    public Map<String, Object> forbidden(@Nullable Principal principal) {
        return new HashMap<>();
    }

    /**
     * Forwards any unmapped paths (except those containing a period or dash) to the client {@code index.html}.
     * @return forward to client {@code index.html}.
     */
    @Get("/{path:[^\\.\\-]*}")
    public Optional<StreamedFile> forward(String path) {
        return environment.getResource("public/index.html").map(StreamedFile::new);
    }
}
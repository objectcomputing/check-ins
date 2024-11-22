package com.objectcomputing.checkins.security;

import io.micronaut.context.env.Environment;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;
import io.micronaut.core.annotation.Nullable;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@Secured(SecurityRule.IS_AUTHENTICATED)
public class HomeController {

    private final Environment environment;

    public HomeController(Environment environment) {
        this.environment = environment;
    }

    @Get("/forbidden")
    @View("forbidden")
    public Map<String, Object> forbidden(@Nullable Principal principal) {
        return new HashMap<>();
    }

    /**
     * Forwards any unmapped paths (except those containing a period) to the client {@code index.html}.
     * @return forward to client {@code index.html}.
     */
    // 2024-10-29 - Note the path excludes "/externalFeedback", which is handled by HomeExternalRecipientController
    @Get("/{path:^(?!externalFeedback)([^\\.]+)$}")
    public Optional<StreamedFile> forward(String path) {
        return environment.getResource("public/index.html").map(StreamedFile::new);
    }

}

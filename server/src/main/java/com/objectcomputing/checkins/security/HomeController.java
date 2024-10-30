package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestExternalRecipientController;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@Secured(SecurityRule.IS_AUTHENTICATED)
public class HomeController {

    private final Environment environment;
    private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

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
    // 2024-10-29 - Note the path excludes "/feedbackExternalRecipient", which is handled by HomeExternalRecipientController
    @Get("/{path:^(?!feedbackExternalRecipient)([^\\.]+)$}")
    public Optional<StreamedFile> forward(String path) {
        LOG.info("HomeController, forward, path: " + path);
        return environment.getResource("public/index.html").map(StreamedFile::new);
    }

}

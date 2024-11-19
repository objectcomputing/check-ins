package com.objectcomputing.checkins.security;

import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
public class HomeExternalRecipientController {

    private final Environment environment;
    private static final Logger LOG = LoggerFactory.getLogger(HomeExternalRecipientController.class);

    public HomeExternalRecipientController(Environment environment) {
        this.environment = environment;
    }

    @Get("/externalFeedback/{path:([^\\.]+)$}")
    public Optional<StreamedFile> forward(String path) {
        LOG.info("HomeExternalRecipientController, forward, path: " + path);
        return environment.getResource("public-external-feedback/index.html").map(StreamedFile::new);
    }

}

package com.objectcomputing.checkins.security;

import io.micronaut.context.env.Environment;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.util.Optional;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
public class HomeExternalRecipientController {

    private final Environment environment;

    public HomeExternalRecipientController(Environment environment) {
        this.environment = environment;
    }

    @Get("/externalFeedback/{path:([^\\.]+)$}")
    public Optional<StreamedFile> forward(String path) {
        return environment.getResource("public-external-feedback/index.html").map(StreamedFile::new);
    }

}

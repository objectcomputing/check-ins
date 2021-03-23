package com.objectcomputing.checkins.services.feedback;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@Controller("/services/feedback")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "feedback")
public class FeedbackController {

    @Post("/")
    public void createFeedback() {

    }

    @Put("/")
    public void updateFeedback() {

    }

    @Delete("/{id}")
    public void deleteFeedback(UUID id) {

    }

    @Get("/{id}")
    public void readFeedback(UUID id) {

    }
}

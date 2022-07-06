package com.objectcomputing.checkins.services.signrequest;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "sign-request")

@Controller("/sign-request")
// CORS is already enabled and set to true
public class SignRequestController {

    @Get
    public String helloWorld() {
        return "Hello World";
    }
}
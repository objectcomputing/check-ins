package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller("/send-signrequest")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "signrequest")
public class SignRequestSendRequestController {

    private SignRequestClient signrequestClient;

    public SignRequestSendRequestController(SignRequestClient signrequestClient) { this.signrequestClient = signrequestClient; }

    @Post
    public String sendRequest(@Body @Valid @NotNull SignRequestDTO request) {
        return signrequestClient.sendRequest(request);
    }

}

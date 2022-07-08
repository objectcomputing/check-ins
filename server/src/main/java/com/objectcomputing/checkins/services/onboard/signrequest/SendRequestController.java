package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import io.micronaut.context.annotation.Property;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.util.*;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller
public class SendRequestController {
    @Inject
    @Client("https://ocitest.signrequest.com/api/v1/")
    private HttpClient httpClient;

    @Property(name = "signrequest-credentials.signrequest_token")
    private String SIGNREQUEST_TOKEN;

    @Get("/sign-request")
    public String getData(){

        try{
            String retrieve = httpClient.toBlocking()
                    .retrieve(HttpRequest.GET("/documents/152408ef-4b71-40a4-9416-92011a443450/")
                            .header("Authorization", SIGNREQUEST_TOKEN));
            return retrieve;
        }
        catch (Exception e){
            System.out.println(e);
        }

        return null;
    }
}

package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import io.micronaut.context.annotation.Property;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.json.JSONObject;

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

        JSONObject data = new JSONObject();
        data.put("file_from_url", "https://drive.google.com/file/d/14hrlFXWuHMwG7uPF__M7e2uUBbbJ6cIm/view?usp=sharing");
        //data.put("signers", "email");
        data.put("from_email", "lib@objectcomputing.com");

//        try{
//            String retrieve = httpClient.toBlocking()
//                    .retrieve(HttpRequest.POST("/signrequest-quick-create/", data)
//                            .header("Authorization", SIGNREQUEST_TOKEN));
//            return retrieve;
//        }
//        catch (Exception e){
//            System.out.println(e);
//        }

        return data.toString();
    }
}

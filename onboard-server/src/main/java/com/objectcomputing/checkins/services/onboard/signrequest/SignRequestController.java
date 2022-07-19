package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import io.micronaut.context.annotation.Property;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import static io.micronaut.http.HttpRequest.POST;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller
public class SignRequestController {

    @Client("https://ocihr.signrequest.com/api/v1/") @Inject HttpClient httpClient;
    private Map<String,String> signerInfo = new HashMap<>();

    @Property(name = "signrequest-credentials.signrequest_token")
    private String SIGNREQUEST_TOKEN;

    @Get("/signrequest-documents")
    public String getData(){

        try{
            String retrieve = httpClient.toBlocking()
                    .retrieve(HttpRequest.GET("/documents/")
                            .header("Authorization", SIGNREQUEST_TOKEN));
            return retrieve;
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    @Get("/json-test")
    public String sendForm() {

        JSONObject data = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject item = new JSONObject();

        item.put("email", "li.brandon@outlook.com");
        array.put(item);


        data.put("file_from_url", "https://drive.google.com/file/d/14hrlFXWuHMwG7uPF__M7e2uUBbbJ6cIm/view?usp=sharing");
        data.put("name", "demo_document.pdf");
        data.put("from_email", "berkenwalda@objectcomputing.com");
        data.put("message", "Please sign this document");
        data.put("needs_to_sign", "true");
        data.put("who", "o");
        data.put("subject", "SignTest - YourTeam API");
        data.put("auto_delete_days", "1");
        data.put("signers", array);

        try {
            String retrieve = httpClient.toBlocking().retrieve(POST("/signrequest-quick-create/", data.toString()).contentType(MediaType.APPLICATION_JSON).header("Authorization", SIGNREQUEST_TOKEN));
            System.out.println("Request Worked");
            return retrieve;
        }
        catch (HttpClientResponseException e){
            System.out.println("We Failed");
            System.out.println(e.getMessage());
            System.out.println(e.getResponse().reason());
            System.out.println(e.getResponse().body());
            return data.toString();
        }
    }

    @Get("/send-request")
    public String getSignRequest() {
        try {
            String retrieve = httpClient.toBlocking()
                    .retrieve(HttpRequest.GET("/signrequests/")
                            .header("Authorization", SIGNREQUEST_TOKEN));
            return retrieve;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}

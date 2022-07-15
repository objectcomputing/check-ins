package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import io.micronaut.context.annotation.Property;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.authentication.Authentication;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.print.attribute.standard.Media;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.*;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller
public class SignRequestController {

    @Inject
    @Client("https://ocitest.signrequest.com/api/v1/")
    private HttpClient httpClient;
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

    @Get("/send-request")
    public String sendForm() {

        JSONObject data = new JSONObject();

        JSONArray array = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("email", "lib@objectcomputing.com");
        item.put("embed_url_user_id", "TEST");
        array.put(item);

        data.put("file_from_url", "https://drive.google.com/file/d/14hrlFXWuHMwG7uPF__M7e2uUBbbJ6cIm/view?usp=sharing");
        data.put("signers", "email: lib@objectcomputing.com");
        data.put("from_email", "lib@objectcomputing.com");
        data.put("message", "Please sign this document");
        data.put("needs_to_sign", "true");
        data.put("subject", "SignTest - YourTeam API");
        data.put("signers", array);

//        try{
//            String retrieve = httpClient.toBlocking()
//                    .retrieve(HttpRequest.POST("/signrequest-quick-create/", data)
//                            .header("Authorization", SIGNREQUEST_TOKEN));
//            return retrieve;
//        }
//        catch (Exception e){
//            System.out.println(e);
//            return null;
//        }
        try {
            String retrieve = httpClient.toBlocking()
                    .retrieve(HttpRequest.GET("/documents/")
                            .header("Authorization", SIGNREQUEST_TOKEN));
            return retrieve;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}

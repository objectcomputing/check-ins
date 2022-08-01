package com.objectcomputing.checkins.services.onboard.signrequest;

import com.nimbusds.jose.shaded.json.parser.JSONParser;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import io.micronaut.context.annotation.Property;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

import static io.micronaut.http.HttpRequest.POST;

@Controller()
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "signrequest")
public class SignRequestController {

    @Client("https://ocitest.signrequest.com/api/v1/") @Inject HttpClient httpClient;

    @Property(name = "signrequest-credentials.signrequest_token")
    private String SIGNREQUEST_TOKEN;

    @Post
    public String sendSignRequest() {
        // 1) Get necessary SignRequestDTO information
        SignRequestDTO request = new SignRequestDTO();
        request.setDocument("https://s29.q4cdn.com/816090369/files/doc_downloads/test.pdf");
        request.setFromEmail("librandon0706@gmail.com");
        request.setMessage("Please sign this document. \n\nThanks!");
        String [] signers = {"li.brandon@outlook.com"};
        request.setSigners(signers);

        // 2) Construct appropriate JSON file

        // 3) Send out the SignRequest

        // 4) Get the SignRequest ID & Return Status

        // 5) return

        return "";
    }

//    @Get("/send-signrequest")
//    public String sendSignRequest() {
//
//        JSONObject data = new JSONObject();
//        JSONArray array = new JSONArray();
//        JSONObject item = new JSONObject();
//
//        item.put("email", "li.brandon@outlook.com"); // Pass in
//        array.put(item);
//
//        data.put("file_from_url", "https://drive.google.com/file/d/14hrlFXWuHMwG7uPF__M7e2uUBbbJ6cIm/view?usp=sharing");
//        data.put("name", "demo_document.pdf"); // Pass in
//        data.put("from_email", "librandon0706@gmail.com"); // Current user in the system
//        data.put("message", "Please sign this document"); // Pass in
//        data.put("needs_to_sign", "true");
//        data.put("subject", "SignTest - YourTeam API"); // Pass in
//        data.put("auto_delete_days", "1"); // Hardcode to 30 days
//        data.put("signers", array); // Pass in
//
//        try {
//            String retrieve = httpClient.toBlocking().retrieve(POST("/signrequest-quick-create/", data.toString()).contentType(MediaType.APPLICATION_JSON).header("Authorization", SIGNREQUEST_TOKEN));
//            System.out.println("Request Worked");
//            return retrieve;
//        }
//        catch (HttpClientResponseException e) {
//            System.out.println("We Failed");
//            System.out.println(e.getMessage());
//            System.out.println(e.getResponse().reason());
//            System.out.println(e.getResponse().body());
//            return data.toString();
//        }
//    }

//    @Get("/embed-signrequest")
//    public String embedSignRequest() {
//        JSONObject data = new JSONObject();
//        JSONArray array = new JSONArray();
//        JSONObject item = new JSONObject();
//
//        item.put("email", "li.brandon@outlook.com");
//
//        // Generating an embedded URL will not send out a SignRequest
//        item.put("order", "1");
//        item.put("embed_url_user_id", "testID");
//
//        array.put(item);
//
//        data.put("file_from_url", "https://drive.google.com/file/d/14hrlFXWuHMwG7uPF__M7e2uUBbbJ6cIm/view?usp=sharing");
//        data.put("name", "demo_document.pdf");
//        data.put("from_email", "librandon0706@gmail.com");
//        data.put("message", "Please sign this document");
//        data.put("needs_to_sign", "true");
//        data.put("who", "o");
//        data.put("subject", "SignTest - YourTeam API");
//        data.put("auto_delete_days", "1");
//        data.put("signers", array);
//
//        try {
//            String retrieve = httpClient.toBlocking().retrieve(POST("/signrequest-quick-create/", data.toString()).contentType(MediaType.APPLICATION_JSON).header("Authorization", SIGNREQUEST_TOKEN));
//            System.out.println("Request Worked");
//            return getEmbedSignRequestURL(retrieve);
//        }
//        catch (HttpClientResponseException e){
//            System.out.println("We Failed");
//            System.out.println(e.getMessage());
//            System.out.println(e.getResponse().reason());
//            System.out.println(e.getResponse().body());
//            return data.toString();
//        }
//    }

//    public String getEmbedSignRequestURL(String signRequestJSON) {
//        JSONObject signRequestJSONObject = new JSONObject(signRequestJSON);
//
//        String embedSignRequestURL = "";
//        JSONArray signers = new JSONArray();
//        JSONObject prospectiveEmployee = new JSONObject();
//
//        try {
//            //signers = (JSONArray) signRequestJSONObject.get("signers");
//            signers = signRequestJSONObject.getJSONArray("signers");
//            prospectiveEmployee = signers.getJSONObject(1);
//            embedSignRequestURL = (String) prospectiveEmployee.get("embed_url");
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//        return embedSignRequestURL;
//    }
}

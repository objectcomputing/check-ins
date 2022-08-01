package com.objectcomputing.checkins.services.onboard.document;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces("application/pdf;base64")
public class DocumentController {

    @Client("https://ocitest.signrequest.com/api/v1/") @Inject
    HttpClient httpClient;

    @Property(name = "signrequest-credentials.signrequest_token")
    private String SIGNREQUEST_TOKEN;

    // 1) Get Documents
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

    // Convert file to base64
    public void convertBase64() throws IOException {
        String originalFileName = "notEncoded.pdf";
        String newFileName = "encoded.pdf";

        byte[] input_file = Files.readAllBytes(Paths.get(originalFileName));

        byte[] encodedBytes  = Base64.getEncoder().encode(input_file);
        String encodedString = new String(encodedBytes);
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString.getBytes());

        FileOutputStream fos = new FileOutputStream(newFileName);
        fos.write(decodedBytes);
        fos.flush();
        fos.close();
    }

    @Post
    public String completedFileUpload() throws IOException {
        convertBase64();
        // Uploads SignRequest Document
        //httpClient.toBlocking().exchange("/documents/", data)

        return null;
        // Returns Document ID
    }

}

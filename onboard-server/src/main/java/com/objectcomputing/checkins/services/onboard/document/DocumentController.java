package com.objectcomputing.checkins.services.onboard.document;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import static io.micronaut.http.HttpRequest.POST;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "signrequest")
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

    @Get("/createDocument")
    //@Post
    public String completedFileUpload() throws IOException {
        // Create document's JSONObject
        JSONObject data = new JSONObject();

        // Prepare base64 file
        String filePath = "/Users/lib/summer2022/check-ins/onboard-server/src/main/java/com/objectcomputing/checkins/services/onboard/document/";
        String originalFileName = "notEncoded.pdf";
        String newFileName = "encoded.pdf";

//        byte[] input_file = Files.readAllBytes(Paths.get(filePath+originalFileName));
//
//        byte[] encodedBytes  = Base64.getEncoder().encode(input_file);
//        String encodedString = new String(encodedBytes);
//        byte[] decodedBytes = Base64.getDecoder().decode(encodedString.getBytes());
//
//        FileOutputStream fos = new FileOutputStream(filePath+newFileName);
//        fos.write(decodedBytes);
//        fos.flush();
//        fos.close();

//        byte[] inFileBytes = Files.readAllBytes(Paths.get(filePath+originalFileName));
//        byte[] encoded = java.util.Base64.getEncoder().encode(inFileBytes);
//        byte[] decoded = java.util.Base64.getDecoder().decode(encoded);
//
//        FileOutputStream fos = new FileOutputStream(filePath+newFileName);
//        fos.write(decoded);
//        fos.flush();
//        fos.close();

        data.put("file_from_url", "https://s29.q4cdn.com/816090369/files/doc_downloads/test.pdf");
//        data.put("file_from_content", "pdf_encoded_base64");
//        data.put("file_from_content_name", filePath+newFileName);
//        data.put("name", newFileName);

        // Uploads SignRequest Document
        String documentJSON = httpClient.toBlocking().retrieve(POST("/documents/", data.toString()).contentType(MediaType.APPLICATION_JSON).header("Authorization", SIGNREQUEST_TOKEN));

        // Returns Document ID
        String documentID = "";
        return getDocumentUUID(documentJSON);
    }

    public String getDocumentUUID(String signRequestJSON) {
        JSONObject signRequestJSONObject = new JSONObject(signRequestJSON);

        String documentUUID = "";
        JSONObject getDocumentUUID = new JSONObject();

        try {
            documentUUID = (String) signRequestJSONObject.get("uuid");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return documentUUID;
    }

}

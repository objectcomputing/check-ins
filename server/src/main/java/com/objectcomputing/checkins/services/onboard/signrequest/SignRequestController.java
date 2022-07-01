package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;

import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.authentication.Authentication;

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
    private Signer signer;
    private Map<String,String> signerInfo = new HashMap<>();

    @Get("/signer")
    public String getData(){

        try{
            String retrieve = httpClient.toBlocking()
                    .retrieve(HttpRequest.GET("/documents/152408ef-4b71-40a4-9416-92011a443450/")
                            .header("Authorization","Token 8e3b636de707468e40abd0d8e212c2507e5d2197"));
//            int openSignerIndex = retrieve.indexOf("signers");
//            int closeSignerIndex = retrieve.indexOf("after_document");
//            String substring = retrieve.substring(openSignerIndex+11,closeSignerIndex-2);
//            String[] stringArr = substring.split(",");
//            for (String item : stringArr) {
//                String[] stringPair = item.split(":");
//                signerInfo.put(stringPair[0],stringPair[1]);
//            }
//
//            signer = new Signer(signerInfo.get("\"email\""), signerInfo.get("\"signed\""), signerInfo.get("\"viewed\""));
//            return signer.toString();
            return retrieve;
        }
        catch (Exception e){
            System.out.println(e);
        }

        return null;
    }
}

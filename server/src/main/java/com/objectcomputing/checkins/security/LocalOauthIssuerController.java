package com.objectcomputing.checkins.security;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.oauth2.endpoint.token.response.TokenErrorResponse;
import io.micronaut.security.oauth2.endpoint.token.response.TokenResponse;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.media.Schema;
import org.json.JSONObject;

import javax.inject.Inject;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller("/oauth")
@Requires(env = "local")
@Secured(SecurityRule.IS_ANONYMOUS)
@Schema(hidden = true)
public class LocalOauthIssuerController {

    @Inject
    private UsersStore usersStore;

    @View("login")
    @Get("/auth")
    public HttpResponse<Map<String, String>> auth(@QueryValue("response_type") String responseType,
                                                  @QueryValue("redirect_uri") String redirectUri, @QueryValue("state") String state,
                                                  @QueryValue("client_id") String clientId) {
        Map<String, String> data = new HashMap<>();
        data.put("redirectUri", redirectUri);
        data.put("state", state);
        return HttpResponse.ok(data);
    }

    @Post("/auth")
    @Consumes(value = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> authPost(HttpRequest<?> request, String email, String role, URI redirectUri, String state) {
        if ((StringUtils.isEmpty(role)) || usersStore.getUserRole(role) != null) {
            JSONObject fakeCode = new JSONObject(Map.of("role", role == null ? "" : role, "email", email));
            return HttpResponse.redirect(UriBuilder.of(redirectUri)
                    .queryParam("code", fakeCode.toString()).queryParam("state", state).build());
        }
        return HttpResponse.redirect(UriBuilder.of(redirectUri)
                .queryParam("error", String.format("'%s is an invalid role'", role)).build());
    }

    @Post("/token")
    @Consumes(value = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> token(@Body LinkedHashMap<String, String> grantMap) {
        JSONObject fakeCodeAsJson = new JSONObject(grantMap.get("code"));
        String role = fakeCodeAsJson.getString("role");
        String email = fakeCodeAsJson.getString("email");
        if ((role == null || usersStore.getUserRole(role) != null)
                && email != null) {
            JSONObject fakeAccessToken = new JSONObject(Map.of("role", role == null ? "" : role, "email", email));
            TokenResponse response = new TokenResponse();
            response.setAccessToken(fakeAccessToken.toString());
            response.setTokenType("bearer");

            return HttpResponse.ok(response);
        }
        return HttpResponse.badRequest(new TokenErrorResponse());
    }
}
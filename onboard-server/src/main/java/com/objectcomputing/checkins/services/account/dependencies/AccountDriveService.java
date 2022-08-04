package com.objectcomputing.checkins.services.account.dependencies;

import com.objectcomputing.checkins.services.model.LoginAccount;
import com.objectcomputing.checkins.http.HttpRequestUtilities;
import com.objectcomputing.checkins.util.BuildableHashMap;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.Map;

@Singleton
public class AccountDriveService {
    @Value("${geoai.clients.drive.endpoints.init-drive.path:/api/system/initialize}")
    private String initDrivePath;

    private final HttpClient httpClient;

    public AccountDriveService(@Client("${geoai.clients.drive.url}") HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Mono<Object> initialize(LoginAccount userAccount) {
        Map<String, Object> profileData = new BuildableHashMap<String, Object>()
                .build("accountId", userAccount.getId());

        MutableHttpRequest<?> createProfileReq = HttpRequestUtilities.POST(initDrivePath, profileData);

        return Mono.from(httpClient.retrieve(createProfileReq));
    }

}

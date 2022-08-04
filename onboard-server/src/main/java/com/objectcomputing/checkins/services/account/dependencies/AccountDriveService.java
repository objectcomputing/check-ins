package com.objectcomputing.checkins.services.account.dependencies;

import com.objectcomputing.geoai.core.http.HttpRequestUtilities;
import com.objectcomputing.geoai.core.util.BuildableHashMap;
import com.objectcomputing.geoai.platform.account.model.UserAccount;
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

    public Mono<Object> initialize(UserAccount userAccount) {
        Map<String, Object> profileData = new BuildableHashMap<String, Object>()
                .build("accountId", userAccount.getId())
                .build("organizationId", userAccount.getOrganization().getId());

        MutableHttpRequest<?> createProfileReq = HttpRequestUtilities.POST(initDrivePath, profileData);

        return Mono.from(httpClient.retrieve(createProfileReq));
    }

}

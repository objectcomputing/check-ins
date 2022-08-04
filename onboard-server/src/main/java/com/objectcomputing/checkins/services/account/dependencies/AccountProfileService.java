package com.objectcomputing.checkins.services.account.dependencies;

import com.objectcomputing.checkins.services.model.LoginAccount;
import com.objectcomputing.geoai.core.http.HttpRequestUtilities;
import com.objectcomputing.geoai.core.util.BuildableHashMap;
import com.objectcomputing.geoai.platform.account.endpoint.UserAccountConfig;
import com.objectcomputing.geoai.platform.account.model.Organization;
import com.objectcomputing.geoai.platform.account.model.UserAccount;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.type.Argument;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.Map;

@Singleton
public class AccountProfileService {

    @Value("${geoai.clients.account.endpoints.init-account.path:/api/system/account/init}")
    private String initAccountPath;

    @Value("${geoai.clients.account.endpoints.init-organization.path:/api/system/organization/init}")
    private String initOrganizationPath;

    private final HttpClient httpClient;

    public AccountProfileService(@Client("${geoai.clients.account.url}") HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Mono<Object> createProfile(LoginAccount userAccount, UserAccountConfig userAccountConfig) {
        Map<String, Object> profileData = new BuildableHashMap<String, Object>()
                .build("accountId", userAccount.getId())
                .build("handle", userAccountConfig.getHandle())
                .build("firstName", userAccountConfig.getFirstName())
                .build("lastName", userAccountConfig.getLastName());

        MutableHttpRequest<?> createProfileReq = HttpRequestUtilities.POST(initAccountPath, profileData);

        return Mono.from(httpClient.retrieve(createProfileReq));
    }

}

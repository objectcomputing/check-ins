package com.objectcomputing.checkins.services.settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import javax.inject.Inject;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.SettingsFixture;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class SettingsControllerTest extends TestContainersSuite implements SettingsFixture, MemberProfileFixture {
    @Inject
    @Client("/services/settings")
    private HttpClient client;
    
    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @Test
    public void testGetAllSettings() {

        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));

        MemberProfile memberProfile = createADefaultMemberProfile();
        Setting setting = createADefaultSetting(alice.getId());
        setting.setUserId(memberProfile.getId());

        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(alice.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);

        final HttpResponse<Set<Setting>> response = client.toBlocking().exchange(request, Argument.setOf(Setting.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        response.equals(setting);
        assertNotNull(response.getContentLength());

    }
}

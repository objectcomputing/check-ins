package com.objectcomputing.checkins.services.settings;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import javax.inject.Inject;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.SettingsFixture;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;

public class SettingsControllerTest extends TestContainersSuite implements SettingsFixture {
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
    public void testGetAllSetting() {
        Setting setting = createADefaultSetting();
        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Set<Setting>> response = client.toBlocking().exchange(request, Argument.setOf(Setting.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        response.equals(setting);
        assertNotNull(response.getContentLength());

    }
}

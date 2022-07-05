package com.objectcomputing.checkins.services.settings;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.SettingsFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

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
    public void testGetAllSettingsUnauthorized() {

        final HttpRequest<Object> request = HttpRequest.
                GET("/");

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
    }

    @Test
    public void testPostUnauthorized() {
        SettingsCreateDTO newSetting = new SettingsCreateDTO();
        newSetting.setName("Mode");
        newSetting.setValue("Light");

        final HttpRequest<SettingsCreateDTO> request = HttpRequest.
                POST("/", newSetting);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
    }

    @Test
    public void testGetAllSettings() {

        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        Setting setting = createADefaultSetting(alice.getId());

        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(alice.getWorkEmail(), MEMBER_ROLE);

        final HttpResponse<List<SettingsResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(SettingsResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(response.body().get(0).getId(), setting.getId());
        assertEquals(response.body().size(), 1);

    }

    @Test
    public void testGETFindByValidName() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        Setting setting = createADefaultSetting(alice.getId());

        final HttpRequest<Object> request = HttpRequest.GET(String.format("/?name=%s", setting.getName()))
                .basicAuth(alice.getWorkEmail(), MEMBER_ROLE);

        HttpResponse<List<SettingsResponseDTO>> response =  client.toBlocking()
                .exchange(request, Argument.listOf(SettingsResponseDTO.class));


        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(response.body().get(0).getId(), setting.getId());
        assertEquals(response.body().size(), 1);
    }

    @Test
    public void testGETFindByWrongNameReturnsEmptyBody() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        Setting setting = createADefaultSetting(alice.getId());

        final HttpRequest<Object> request = HttpRequest.GET(String.format("/?name=%s", encodeValue("random")))
                .basicAuth(alice.getWorkEmail(), MEMBER_ROLE);

        HttpResponse<List<SettingsResponseDTO>> response =  client.toBlocking()
                .exchange(request, Argument.listOf(SettingsResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(response.body(), new ArrayList<>());
    }

    @Test
    public void testPOSTCreateASetting() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));

        SettingsCreateDTO newSetting = new SettingsCreateDTO();
        newSetting.setName("Mode");
        newSetting.setValue("Light");

        final HttpRequest<SettingsCreateDTO> request = HttpRequest.
                POST("/", newSetting).basicAuth(alice.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<SettingsResponseDTO> response = client.toBlocking().exchange(request,SettingsResponseDTO.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED,response.getStatus());
        assertEquals(newSetting.getName(), response.body().getName());
    }

    @Test
    public void testPOSTCreateASettingNoName() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));

        SettingsCreateDTO newSetting = new SettingsCreateDTO();

        final HttpRequest<SettingsCreateDTO> request = HttpRequest.
                POST("/", newSetting).basicAuth(alice.getWorkEmail(), MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testPUTSuccessfulUpdate() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));

        Setting settingToUpdate = createADefaultSetting(lucy.getId());
        SettingsUpdateDTO updatedSetting = new SettingsUpdateDTO();
        updatedSetting.setValue("off");
        updatedSetting.setId(settingToUpdate.getId());
        updatedSetting.setName(settingToUpdate.getName());

        final HttpRequest<SettingsUpdateDTO> request = HttpRequest.
                PUT("/", updatedSetting).basicAuth(lucy.getWorkEmail(),MEMBER_ROLE);
        final HttpResponse<SettingsResponseDTO> response = client.toBlocking().exchange(request, SettingsResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), updatedSetting.getId()),
                response.getHeaders().get("location"));
    }

    @Test
    public void testPUTWrongId() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));

        Setting settingToUpdate = createADefaultSetting(lucy.getId());
        SettingsUpdateDTO updatedSetting = new SettingsUpdateDTO();
        updatedSetting.setValue("off");
        updatedSetting.setId(UUID.randomUUID());
        updatedSetting.setName(settingToUpdate.getName());

        final HttpRequest<SettingsUpdateDTO> request = HttpRequest.
                PUT("/", updatedSetting).basicAuth(lucy.getWorkEmail(),MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(responseException.getMessage(), String.format("Setting %s does not exist, cannot update", updatedSetting.getId().toString()));
    }

    @Test
    public void testPUTNoId() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));

        SettingsUpdateDTO updatedSetting = new SettingsUpdateDTO();

        final HttpRequest<SettingsUpdateDTO> request = HttpRequest.
                PUT("/", updatedSetting).basicAuth(lucy.getWorkEmail(),MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(responseException.getMessage(), "Bad Request");
    }

    @Test
    public void testDELETESetting() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));

        Setting setting = createADefaultSetting(lucy.getId());

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", setting.getId())).basicAuth(lucy.getWorkEmail(),MEMBER_ROLE);

        final HttpResponse<Boolean> response = client.toBlocking().exchange(request, Boolean.class);

        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    public void testDELETESettingWrongId() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));

        Setting setting = createADefaultSetting(lucy.getId());

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", UUID.randomUUID())).basicAuth(lucy.getWorkEmail(),MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());

    }

    @Test
    public void testDELETESettingNoPermission() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));

        Setting setting = createADefaultSetting(lucy.getId());

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", setting.getId()));

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals("Unauthorized", responseException.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED,responseException.getStatus());

    }
}

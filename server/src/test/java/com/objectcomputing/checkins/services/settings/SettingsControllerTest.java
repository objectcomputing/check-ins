package com.objectcomputing.checkins.services.settings;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.SettingsFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

class SettingsControllerTest extends TestContainersSuite implements RoleFixture, SettingsFixture, MemberProfileFixture {
    @Inject
    @Client("/services/settings")
    private HttpClient client;

    @BeforeEach
    void createRolesAndPermissions() {
        createAndAssignRoles();
    }

    @Test
    void testGetAllSettingsUnauthorized() {

        final HttpRequest<Object> request = HttpRequest.GET("/");

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
    }

    @Test
    void testPostUnauthorized() {
        SettingsDTO newSetting = new SettingsDTO();
        newSetting.setName("Mode");
        newSetting.setValue("Light");

        final HttpRequest<SettingsDTO> request = HttpRequest.
                POST("/", newSetting);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.UNAUTHORIZED, responseException.getStatus());
    }

    @Test
    void testGetAllSettings() {

        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        Setting setting = createADefaultSetting();

        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(alice.getWorkEmail(), MEMBER_ROLE);

        final HttpResponse<List<SettingsResponseDTO>> response = client.toBlocking()
                .exchange(request, Argument.listOf(SettingsResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(setting.getId(), response.body().get(0).getId());
        assertEquals(1, response.body().size());
    }

    @Test
    void testGetOptions() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));

        final HttpRequest<Object> request = HttpRequest.
                GET("/options").basicAuth(alice.getWorkEmail(), MEMBER_ROLE);

        final HttpResponse<List<SettingOption>> response = client.toBlocking()
                .exchange(request, Argument.listOf(SettingOption.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(SettingOption.getOptions().size(), response.getBody().get().size());
        assertEquals(SettingOption.getOptions(), response.getBody().get());
    }

    @Test
    void testGETFindByValidName() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        Setting setting = createADefaultSetting();

        final HttpRequest<Object> request = HttpRequest.GET("/" + setting.getName())
                .basicAuth(alice.getWorkEmail(), MEMBER_ROLE);

        HttpResponse<SettingsResponseDTO> response =  client.toBlocking()
                .exchange(request, SettingsResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(response.body().getId(), setting.getId());
        assertEquals(response.body().getName(), setting.getName());
        assertEquals(response.body().getValue(), setting.getValue());
    }

    @Test
    void testPUTValidDTOButSettingNotFound() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));
        SettingsDTO updatedSetting = new SettingsDTO();
        updatedSetting.setName(SettingOption.LOGO_URL.toString());
        updatedSetting.setValue("Missing");
        final HttpRequest<SettingsDTO> request = HttpRequest.
                PUT("/", updatedSetting).basicAuth(lucy.getWorkEmail(),ADMIN_ROLE);

        final HttpResponse<SettingsResponseDTO> response = client.toBlocking().exchange(request,SettingsResponseDTO.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK,response.getStatus());
        assertEquals(updatedSetting.getName(), response.body().getName());
        assertEquals(updatedSetting.getValue(), response.body().getValue());

    }

    @Test
    void testGETFindByWrongNameThrowsException() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));
        Setting setting = createADefaultSetting();

        final HttpRequest<Object> request = HttpRequest.GET("/random")
                .basicAuth(alice.getWorkEmail(), MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException);
        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testPOSTCreateASetting() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));

        SettingsDTO newSetting = new SettingsDTO();
        newSetting.setName(SettingOption.LOGO_URL.name());
        newSetting.setValue("Light");

        final HttpRequest<SettingsDTO> request = HttpRequest.
                POST("/", newSetting).basicAuth(alice.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<SettingsResponseDTO> response = client.toBlocking().exchange(request,SettingsResponseDTO.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED,response.getStatus());
        assertEquals(newSetting.getName(), response.body().getName());
        assertEquals(newSetting.getValue(), response.body().getValue());
    }

    @Test
    void testPostNullName() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));

        SettingsDTO newSetting = new SettingsDTO();
        newSetting.setValue("value");
        final HttpRequest<SettingsDTO> request = HttpRequest.
                POST("/", newSetting).basicAuth(alice.getWorkEmail(), ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testPostNullValue() {
        final MemberProfile alice = getMemberProfileRepository().save(mkMemberProfile("Alice"));

        SettingsDTO newSetting = new SettingsDTO();
        newSetting.setName(SettingOption.LOGO_URL.name());
        final HttpRequest<SettingsDTO> request = HttpRequest.
                POST("/", newSetting).basicAuth(alice.getWorkEmail(), ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException);
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testPUTSuccessfulUpdate() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));

        Setting settingToUpdate = createADefaultSetting();
        SettingsDTO updatedSetting = new SettingsDTO();
        updatedSetting.setValue("off");
        updatedSetting.setName(settingToUpdate.getName());

        final HttpRequest<SettingsDTO> request = HttpRequest.
                PUT("/", updatedSetting).basicAuth(lucy.getWorkEmail(),ADMIN_ROLE);
        final HttpResponse<SettingsResponseDTO> response = client.toBlocking().exchange(request, SettingsResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(updatedSetting.getValue(), response.body().getValue());
        assertEquals(String.format("%s/%s", request.getPath(), settingToUpdate.getId()),
                response.getHeaders().get("location"));
    }

    @Test
    void testPUTBadName() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));

        Setting settingToUpdate = createADefaultSetting();
        SettingsDTO updatedSetting = new SettingsDTO();
        updatedSetting.setValue("off");
        updatedSetting.setName("wrong");

        final HttpRequest<SettingsDTO> request = HttpRequest.
                PUT("/", updatedSetting).basicAuth(lucy.getWorkEmail(),ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Provided setting name is invalid.", responseException.getMessage());
    }

    @Test
    void testPUTNoIdOrName() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));

        SettingsDTO updatedSetting = new SettingsDTO();

        final HttpRequest<SettingsDTO> request = HttpRequest.
                PUT("/", updatedSetting).basicAuth(lucy.getWorkEmail(),ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Bad Request", responseException.getMessage());
    }

    @Test
    void testDELETESetting() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));

        Setting setting = createADefaultSetting();

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", setting.getId())).basicAuth(lucy.getWorkEmail(),ADMIN_ROLE);

        final HttpResponse<Boolean> response = client.toBlocking().exchange(request, Boolean.class);

        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    void testDELETESettingWrongId() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));

        Setting setting = createADefaultSetting();

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", UUID.randomUUID())).basicAuth(lucy.getWorkEmail(),ADMIN_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());

    }

    @Test
    void testDELETESettingNoPermission() {
        final MemberProfile lucy = getMemberProfileRepository().save(mkMemberProfile("Lucy"));

        Setting setting = createADefaultSetting();

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", setting.getId()));

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals("Unauthorized", responseException.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED,responseException.getStatus());

    }

    @Test
    void testUniqueConstraint() {
        Setting setting1 = getSettingsRepository().save(new Setting(SettingOption.LOGO_URL.toString(), "url.com"));
        try{
            Setting setting2 = getSettingsRepository().save(new Setting(SettingOption.LOGO_URL.toString(), "url2.com"));
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("duplicate key value violates unique constraint"));
        }
    }

}

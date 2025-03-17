package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.GooglePhotoAccessorImplReplacement;
import io.micronaut.core.util.StringUtils;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "replace.googlephotoaccessorimpl", value = StringUtils.TRUE)
class MemberPhotoControllerTest extends TestContainersSuite {

    @Inject
    @Client("/services/member-profiles/member-photos")
    private HttpClient client;

    @Inject
    GooglePhotoAccessorImplReplacement googlePhotoAccessor;

    @Test
    void testGetForValidInput() {

        String testEmail = "test@test.com";
        String testPhotoData = "test.photo.data";
        byte[] testData = Base64.getUrlEncoder().encode(testPhotoData.getBytes());
        googlePhotoAccessor.setPhotoData(testEmail, testData);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", testEmail)).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<byte[]> response = client.toBlocking().exchange(request, byte[].class);
        client.toBlocking().exchange(request, byte[].class);
        client.toBlocking().exchange(request, byte[].class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        byte[] result = response.getBody().get();
        assertEquals(new String(testPhotoData), new String(result));
    }
}

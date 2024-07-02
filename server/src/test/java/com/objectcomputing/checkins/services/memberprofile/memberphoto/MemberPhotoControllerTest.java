package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MockBean;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;

import java.util.Base64;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Disabled in nativeTest, as we get an exception from Mockito
//   => Message: Could not initialize class org.mockito.Mockito
@DisabledInNativeImage
class MemberPhotoControllerTest extends TestContainersSuite {

    @Inject
    @Client("/services/member-profiles/member-photos")
    private HttpClient client;

    @Inject
    GooglePhotoAccessor googlePhotoAccessor;

    @Test
    void testGetForValidInput() {

        String testEmail = "test@test.com";
        byte[] testData = Base64.getUrlEncoder().encode("test.photo.data".getBytes());

        when(googlePhotoAccessor.getPhotoData(testEmail)).thenReturn(testData);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", testEmail)).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<byte[]> response = client.toBlocking().exchange(request, byte[].class);
        client.toBlocking().exchange(request, byte[].class);
        client.toBlocking().exchange(request, byte[].class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        byte[] result = response.getBody().get();
        assertEquals(new String(testData), new String(result));

        // Only called once due to the cache
        verify(googlePhotoAccessor, times(1)).getPhotoData(testEmail);
    }

    @MockBean(GooglePhotoAccessorImpl.class)
    public GooglePhotoAccessor googlePhotoAccessor() {
        return mock(GooglePhotoAccessor.class);
    }
}

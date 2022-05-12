package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import jakarta.inject.Inject;
import java.io.IOException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberPhotoControllerTest {

    @Inject
    @Client("/services/member-profiles/member-photos")
    private HttpClient client;

    @Inject
    private MemberPhotoService memberPhotoService;

    //Happy path
    @Test
    public void testGetForValidInput() throws IOException {

        String testEmail = "test@test.com";
        String testPhotoData = "test.photo.data";
        byte[] testData = Base64.getUrlEncoder().encode(testPhotoData.getBytes());

        when(memberPhotoService.getImageByEmailAddress(testEmail)).thenReturn(testData);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", testEmail));
        final HttpResponse<byte[]> response = client.toBlocking().exchange(request, byte[].class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        byte[] result = response.getBody().get();
        assertEquals(new String(testData), new String(result));
    }

    @MockBean(MemberPhotoServiceImpl.class)
    public MemberPhotoService memberPhotoService() {
        return mock(MemberPhotoService.class);
    }

}

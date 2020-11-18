package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileDoesNotExistException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberPhotoControllerTest {

    @Inject
    @Client("/services/member-profile/member-photo")
    private HttpClient client;

    @Inject
    private MemberPhotoService memberPhotoService;

    //Happy path
    @Test
    public void testGetForValidInput() {

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

    @Test
    public void testGetThrowsMemberProfileDoesNotExistException() {

        String testEmail = "test@test.com";

        when(memberPhotoService.getImageByEmailAddress(testEmail))
                .thenThrow(new MemberProfileDoesNotExistException(String.format("No member profile exists for the email %s", testEmail)));

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest
                    .GET(String.format("/%s", testEmail)));
        });
        assertEquals(String.format("No member profile exists for the email %s", testEmail), thrown.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @MockBean(MemberPhotoServiceImpl.class)
    public MemberPhotoService memberPhotoService() {
        return mock(MemberPhotoService.class);
    }

}

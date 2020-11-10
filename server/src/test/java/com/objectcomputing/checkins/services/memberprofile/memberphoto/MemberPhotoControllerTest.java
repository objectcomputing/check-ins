package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import com.google.api.services.admin.directory.Directory;
import com.objectcomputing.checkins.util.googleapiaccess.GoogleApiAccess;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;
import java.util.Base64;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberPhotoControllerTest {

    @Inject
    @Client("/services/member-profile/member-photo")
    private HttpClient client;

    @Mock
    private MemberPhotoService mockMemberPhotoService;

    @Mock
    private GoogleApiAccess googleApiAccess;

    @Mock
    private Directory mockDirectory;

//    @MockBean(GoogleApiAccess.class)
//    public GoogleApiAccess googleApiAccess() {
//        return mock(GoogleApiAccess.class);
//    }

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(mockMemberPhotoService);
        Mockito.reset(googleApiAccess);
        Mockito.reset(mockDirectory);
    }

    //Happy path
    @Test
    public void testGetForValidInput() {
        String testEmail = "test@test.com";
        String testPhotoData = "test.photo.data";
        byte[] testData = Base64.getUrlEncoder().encode(testPhotoData.getBytes());

        when(mockMemberPhotoService.getImageByEmailAddress(testEmail)).thenReturn(testData);
        when(googleApiAccess.getDirectory()).thenReturn(mockDirectory);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", testEmail)).basicAuth("some.email.id", MEMBER_ROLE);
        final HttpResponse<byte[]> response = client.toBlocking().exchange(request);

        assertNotNull(response);
        byte[] result = response.getBody().get();
        assertEquals(HttpStatus.OK, response.getStatus());
    }
}

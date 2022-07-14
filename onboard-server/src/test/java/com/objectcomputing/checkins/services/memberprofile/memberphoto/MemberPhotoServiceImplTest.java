package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.UserPhoto;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.util.googleapiaccess.GoogleApiAccess;
import io.micronaut.context.env.Environment;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberPhotoServiceImplTest {

    @Mock
    private MemberProfileServices mockMemberProfileServices;

    @Mock
    private GoogleApiAccess mockGoogleApiAccess;

    @Mock
    private Directory mockDirectory;

    @Mock
    private Directory.Users mockUsers;

    @Mock
    private Directory.Users.Photos mockPhotos;

    @Mock
    private Directory.Users.Photos.Get mockGet;

    @Mock
    private Environment mockEnvironment;

    @InjectMocks
    private MemberPhotoServiceImpl services;

    @BeforeAll
    void initMocks() throws IOException {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(mockMemberProfileServices);
        Mockito.reset(mockGoogleApiAccess);
        Mockito.reset(mockDirectory);
        Mockito.reset(mockUsers);
        Mockito.reset(mockPhotos);
        Mockito.reset(mockGet);
        Mockito.reset(mockEnvironment);
    }

    // happy path
    @Test
    public void testGetImageByEmailAddress() throws IOException {
        String testEmail = "test@test.com";
        String testPhotoData = "test.photo.data";
        byte[] testData = Base64.getUrlEncoder().encode(testPhotoData.getBytes());

        UserPhoto testUserPhoto = new UserPhoto();
        testUserPhoto.setId("test.id");
        testUserPhoto.setEtag("test.etag");
        testUserPhoto.setHeight(10);
        testUserPhoto.setKind("test.kind");
        testUserPhoto.setMimeType("test.mime.type");
        testUserPhoto.setPhotoData(new String(testData));

        when(mockGoogleApiAccess.getDirectory()).thenReturn(mockDirectory);
        when(mockDirectory.users()).thenReturn(mockUsers);
        when(mockUsers.photos()).thenReturn(mockPhotos);
        when(mockPhotos.get(testEmail)).thenReturn(mockGet);
        when(mockGet.execute()).thenReturn(testUserPhoto);

        final byte[] result = services.getImageByEmailAddress(testEmail);

        assertNotNull(result);
        assertEquals(testPhotoData, new String(result, StandardCharsets.UTF_8));
        verify(mockGoogleApiAccess, times(1)).getDirectory();
        verify(mockGet, times(1)).execute();
    }

    @Test
    public void testDirectoryServiceThrowsGoogleJsonResponseException() throws IOException {
        String testEmail = "test@test.com";

        when(mockGoogleApiAccess.getDirectory()).thenReturn(mockDirectory);
        when(mockDirectory.users()).thenReturn(mockUsers);
        when(mockUsers.photos()).thenReturn(mockPhotos);
        when(mockPhotos.get(testEmail)).thenReturn(mockGet);
        when(mockGet.execute()).thenThrow(GoogleJsonResponseException.class);

        final byte[] result = services.getImageByEmailAddress(testEmail);

        assertNotNull(result);
        assertEquals("", new String(result, StandardCharsets.UTF_8));
        verify(mockGoogleApiAccess, times(1)).getDirectory();
        verify(mockGet, times(1)).execute();
    }

    private URL setupUrl() throws IOException {
        //URL is a final class and cannot be mocked directly
        final URLConnection mockConnection = Mockito.mock(URLConnection.class);
        final URLStreamHandler handler = new URLStreamHandler() {

            @Override
            protected URLConnection openConnection(final URL arg0) {
                return mockConnection;
            }
        };

        return new URL("http://foo.bar", "foo.bar", 80, "", handler);
    }
}

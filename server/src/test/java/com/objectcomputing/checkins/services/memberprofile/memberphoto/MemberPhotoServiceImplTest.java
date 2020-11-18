package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.UserPhoto;
import com.objectcomputing.checkins.services.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.util.googleapiaccess.GoogleApiAccess;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static org.junit.jupiter.api.Assertions.*;
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

    @InjectMocks
    private MemberPhotoServiceImpl services;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(mockMemberProfileServices);
        Mockito.reset(mockGoogleApiAccess);
        Mockito.reset(mockDirectory);
        Mockito.reset(mockUsers);
        Mockito.reset(mockPhotos);
        Mockito.reset(mockGet);
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

        when(mockMemberProfileServices.findByValues(null, null, null, testEmail, null))
                .thenReturn((Set.of(mkMemberProfile())));
        when(mockGoogleApiAccess.getDirectory()).thenReturn(mockDirectory);
        when(mockDirectory.users()).thenReturn(mockUsers);
        when(mockUsers.photos()).thenReturn(mockPhotos);
        when(mockPhotos.get(testEmail)).thenReturn(mockGet);
        when(mockGet.execute()).thenReturn(testUserPhoto);

        final byte[] result = services.getImageByEmailAddress(testEmail);

        assertNotNull(result);
        assertEquals(testPhotoData, new String(result, StandardCharsets.UTF_8));
        verify(mockMemberProfileServices, times(1)).findByValues(null, null, null, testEmail, null);
        verify(mockGoogleApiAccess, times(1)).getDirectory();
        verify(mockGet, times(1)).execute();
    }

    @Test
    public void testWorkEmailDoesntExist() throws IOException {
        String testEmail = "test@test.com";
        when(mockMemberProfileServices.findByValues(null, null, null, testEmail, null))
                .thenReturn(Collections.emptySet());

        final NotFoundException responseException = assertThrows(NotFoundException.class,
                                                            () -> services.getImageByEmailAddress(testEmail));

        assertEquals(String.format("No member profile exists for the email %s", testEmail), responseException.getMessage());
        verify(mockMemberProfileServices, times(1)).findByValues(null, null, null, testEmail, null);
        verify(mockGoogleApiAccess, times(0)).getDirectory();
        verify(mockGet, times(0)).execute();
    }

    @Test
    public void testDirectoryServiceThrowsIOException() throws IOException {
        String testEmail = "test@test.com";

        when(mockMemberProfileServices.findByValues(null, null, null, testEmail, null))
                .thenReturn((Set.of(mkMemberProfile())));
        when(mockGoogleApiAccess.getDirectory()).thenReturn(mockDirectory);
        when(mockDirectory.users()).thenReturn(mockUsers);
        when(mockUsers.photos()).thenReturn(mockPhotos);
        when(mockPhotos.get(testEmail)).thenReturn(mockGet);
        when(mockGet.execute()).thenThrow(IOException.class);

        final byte[] result = services.getImageByEmailAddress(testEmail);

        assertNotNull(result);
        assertEquals(0, result.length);
        verify(mockMemberProfileServices, times(1)).findByValues(null, null, null, testEmail, null);
        verify(mockGoogleApiAccess, times(1)).getDirectory();
        verify(mockGet, times(1)).execute();
    }
}

package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.directory.model.UserPhoto;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.GooglePhotoAccessorImplReplacement;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import io.micronaut.core.util.StringUtils;
import io.micronaut.context.annotation.Property;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Property(name = "replace.googlephotoaccessorimpl", value = StringUtils.TRUE)
class MemberPhotoServiceImplTest extends TestContainersSuite {

    @Inject
    private GooglePhotoAccessorImplReplacement googlePhotoAccessorImpl;

    @Inject
    private MemberPhotoServiceImpl service;

    @BeforeEach
    void reset() {
        googlePhotoAccessorImpl.reset();
    }

    // happy path
    @Test
    void testGetImageByEmailAddress() throws IOException {
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
        googlePhotoAccessorImpl.setUserPhoto(testEmail, testUserPhoto);
        final byte[] result = service.getImageByEmailAddress(testEmail);

        assertNotNull(result);
        assertEquals(testPhotoData, new String(result, StandardCharsets.UTF_8));
    }

    @Test
    void testDirectoryServiceThrowsGoogleJsonResponseException() throws IOException {
        String testEmail = "notcached@test.com";

        final byte[] result = service.getImageByEmailAddress(testEmail);

        assertNotNull(result);
        assertEquals("", new String(result, StandardCharsets.UTF_8));
    }
}

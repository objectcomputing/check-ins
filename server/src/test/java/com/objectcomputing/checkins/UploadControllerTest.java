package com.objectcomputing.checkins;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.Create;
import com.google.api.services.drive.model.File;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import io.reactivex.Flowable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
class UploadControllerTest {

    private static final String FILE_TO_UPLOAD = "/micronaut.png";

    @Inject
    @Client("/upload")
    RxHttpClient client = null;

    @Inject
    UploadController uploadController;

    @Inject
    static GoogleDriveAccessor googleDriveAccessor;

    @Test
    void testGetUpload() {
        final HttpRequest<?> req = HttpRequest.GET("");
        final Flowable<?> flowable = client.exchange(req);
        final HttpResponse<?> response = (HttpResponse<?>) flowable.blockingFirst();
        Assertions.assertEquals(response.getStatus(), HttpStatus.OK);
    }

    // Wrong media type
    @Test
    void testUploadControllerWrongType() {
        final HttpRequest<?> req = HttpRequest.POST("", CollectionUtils.mapOf("file", null));
        final Flowable<?> flowable = client.retrieve(req);

        final HttpClientResponseException exception = Assertions.assertThrows(HttpClientResponseException.class,
                flowable::blockingFirst);

        Assertions.assertTrue(
                exception.getMessage().contains(String.format("Allowed types: [%s]", MediaType.MULTIPART_FORM_DATA)));
        Assertions.assertEquals(exception.getStatus(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    // Null File
    @Test
    void testUploadNullFile() {
        final HttpRequest<?> req = HttpRequest.POST("", MultipartBody.builder().addPart("dnc", "dnc").build())
                .contentType(MediaType.MULTIPART_FORM_DATA);
        final Flowable<?> flowable = client.retrieve(req);

        final HttpClientResponseException exception = Assertions.assertThrows(HttpClientResponseException.class,
                flowable::blockingFirst);

        Assertions.assertTrue(exception.getMessage().matches("Required argument .* not specified"));
        Assertions.assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
    }

    // Google Drive can't connect
    @Test
    void testDriveCantConnect() throws URISyntaxException, IOException {
        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(null);

        final java.io.File file = new java.io.File(this.getClass().getResource(FILE_TO_UPLOAD).toURI());
        final HttpRequest<?> req = HttpRequest.POST("", MultipartBody.builder().addPart("file", file).build())
                .contentType(MediaType.MULTIPART_FORM_DATA);
        final Flowable<?> flowable = client.retrieve(req);

        final HttpClientResponseException exception = Assertions.assertThrows(HttpClientResponseException.class,
                flowable::blockingFirst);

        Assertions.assertEquals(exception.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Happy path
    @Test
    void testUploadFile() throws URISyntaxException, IOException {
        final Drive drive = mock(Drive.class);
        final Files files = mock(Files.class);
        final Create create = mock(Create.class);
        when(drive.files()).thenReturn(files);
        when(files.create(any(File.class), any(AbstractInputStreamContent.class))).thenReturn(create);
        when(create.setSupportsAllDrives(true)).thenReturn(create);
        when(create.setFields(any(String.class))).thenReturn(create);
        when(create.execute()).thenReturn(null);

        when(googleDriveAccessor.accessGoogleDrive()).thenReturn(drive);

        final java.io.File file = new java.io.File(this.getClass().getResource(FILE_TO_UPLOAD).toURI());
        final HttpRequest<?> req = HttpRequest.POST("", MultipartBody.builder().addPart("file", file).build())
                .contentType(MediaType.MULTIPART_FORM_DATA);
        final Flowable<?> flowable = client.exchange(req);

        final HttpResponse<?> response = (HttpResponse<?>) flowable.blockingFirst();
        Assertions.assertEquals(response.getStatus(), HttpStatus.OK);
    }

    @MockBean(GoogleDriveAccessor.class)
    public GoogleDriveAccessor googleDriveAccessor() {
        if(googleDriveAccessor == null) {
            googleDriveAccessor = mock(GoogleDriveAccessor.class);
        }
        return googleDriveAccessor;
    }
}

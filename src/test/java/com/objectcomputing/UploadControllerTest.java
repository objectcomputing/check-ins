package com.objectcomputing;

import com.google.api.client.http.InputStreamContent;
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
import io.reactivex.Flowable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
class UploadControllerTest {

    @Inject
    @Client("/upload")
    private RxHttpClient client = null;

    private java.io.File file;

    private static final String fileToUpload = "/micronaut.png";


    @BeforeEach
    void setupBeforeEach() throws IOException, URISyntaxException {
        Drive drive = mock(Drive.class);
        Files files = mock(Files.class);
        Create create = mock(Create.class);
        when(create.execute()).thenReturn(null);
        when(files.create(any(File.class), any(InputStreamContent.class))).thenReturn(create);
        when(drive.files()).thenReturn(files);
        UploadController.drive = drive;

        GoogleDriveUtil googleDriveUtil = mock(GoogleDriveUtil.class);
        when(googleDriveUtil.accessGoogleDrive()).thenReturn(null);
        UploadController.googleDriveUtil = googleDriveUtil;

        if(file == null) {
            file = new java.io.File(this.getClass().getResource(fileToUpload).toURI());
        }
    }

    @Test
    void testGetUpload() {
        HttpRequest<?> req = HttpRequest.GET("");
        Flowable flowable = client.exchange(req);
        HttpResponse response = (HttpResponse) flowable.blockingFirst();
        Assertions.assertEquals(response.getStatus(), HttpStatus.OK);
    }

    // Wrong media type
    @Test
    void testUploadControllerWrongType() {
        HttpRequest<?> req = HttpRequest.POST("", CollectionUtils.mapOf("file", null));
        Flowable flowable = client.retrieve(req);

        HttpClientResponseException exception =
                Assertions.assertThrows(HttpClientResponseException.class, flowable::blockingFirst);

        Assertions.assertTrue(exception
                .getMessage()
                .contains(String.format("Allowed types: [%s]", MediaType.MULTIPART_FORM_DATA)));
        Assertions.assertEquals(exception.getStatus(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    // Null File
    @Test
    void testUploadNullFile() {
        HttpRequest<?> req = HttpRequest.POST("",
                MultipartBody.builder().addPart("dnc", "dnc")
                        .build()).contentType(MediaType.MULTIPART_FORM_DATA);
        Flowable flowable = client.retrieve(req);

        HttpClientResponseException exception =
                Assertions.assertThrows(HttpClientResponseException.class, flowable::blockingFirst);

        Assertions.assertTrue(exception.getMessage().matches("Required argument .* not specified"));
        Assertions.assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
    }

    // Google Drive can't connect
    @Test
    void testDriveCantConnect() {
        UploadController.drive = null;

        HttpRequest<?> req = HttpRequest.POST("",
                MultipartBody.builder().addPart("file", file).build()).contentType(MediaType.MULTIPART_FORM_DATA);
        Flowable flowable = client.retrieve(req);

        HttpClientResponseException exception =
                Assertions.assertThrows(HttpClientResponseException.class, flowable::blockingFirst);

        Assertions.assertEquals(exception.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Happy path
    @Test
    void testUploadFile() {
        HttpRequest<?> req = HttpRequest.POST("",
                MultipartBody.builder().addPart("file", file).build()).contentType(MediaType.MULTIPART_FORM_DATA);
        Flowable flowable = client.exchange(req);

        HttpResponse response = (HttpResponse) flowable.blockingFirst();
        Assertions.assertEquals(response.getStatus(), HttpStatus.OK);
    }
}

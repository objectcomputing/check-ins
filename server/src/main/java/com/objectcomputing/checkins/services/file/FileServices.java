package com.objectcomputing.checkins.services.file;

import com.google.api.services.drive.model.File;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.multipart.CompletedFileUpload;

import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public interface FileServices {
    HttpResponse<Set<File>> findFiles(UUID checkInId);
    HttpResponse<OutputStream> downloadFiles(UUID uploadDocId);
    HttpResponse<?> uploadFile(CompletedFileUpload file);
}

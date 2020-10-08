package com.objectcomputing.checkins.services.file;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.multipart.CompletedFileUpload;

import java.util.UUID;

public interface FileServices {
    HttpResponse<?> findFiles(UUID checkInId);
    HttpResponse<?> downloadFiles(String uploadDocId);
    HttpResponse<FileInfoDTO> uploadFile(UUID checkInID, CompletedFileUpload file);
    HttpResponse<?> deleteFile(String uploadDocId);
}

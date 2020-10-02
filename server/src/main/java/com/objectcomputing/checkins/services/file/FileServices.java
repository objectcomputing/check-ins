package com.objectcomputing.checkins.services.file;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.multipart.CompletedFileUpload;

import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public interface FileServices {
    HttpResponse<Set<FileInfoDTO>> findFiles(UUID checkInId);
    HttpResponse<OutputStream> downloadFiles(String uploadDocId);
    HttpResponse<FileInfoDTO> uploadFile(UUID checkInID, CompletedFileUpload file);
    HttpResponse<?> deleteFile(String uploadDocId);
}

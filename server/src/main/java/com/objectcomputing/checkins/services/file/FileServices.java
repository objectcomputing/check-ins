package com.objectcomputing.checkins.services.file;

import io.micronaut.http.multipart.CompletedFileUpload;

import java.io.File;
import java.util.Set;
import java.util.UUID;

public interface FileServices {
    Set<FileInfoDTO> findFiles(UUID checkInId);
    File downloadFiles(String uploadDocId);
    FileInfoDTO uploadFile(UUID checkInID, CompletedFileUpload file);
    boolean deleteFile(String uploadDocId);
}

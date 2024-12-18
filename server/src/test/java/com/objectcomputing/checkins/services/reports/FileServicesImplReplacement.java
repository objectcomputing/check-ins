package com.objectcomputing.checkins.services.reports;

/*************************************************************************
 *
 * This class is here due to the fact that the ReportDataController now
 * references the FileServices.  The real FileServicesImpl requires the
 * GoogleApiAccess class that does not exist during testing.
 *
 * This replacement class does not require that and can help us test the
 * output of the MarkdownGeneration class.
 *
 ************************************************************************/

import com.objectcomputing.checkins.services.file.FileInfoDTO;
import com.objectcomputing.checkins.services.file.FileServices;
import com.objectcomputing.checkins.services.file.FileServicesImpl;

import io.micronaut.http.multipart.CompletedFileUpload;

import java.io.File;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

import jakarta.inject.Singleton;
import io.micronaut.context.env.Environment;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;

@Singleton
@Replaces(FileServicesImpl.class)
@Requires(env = Environment.TEST)
public class FileServicesImplReplacement implements FileServices {
    public String documentName = "";
    public String documentText = "";

    public Set<FileInfoDTO> findFiles(UUID checkInId) {
        return new HashSet<FileInfoDTO>();
    }

    public File downloadFiles(String uploadDocId) {
        return null;
    }

    public FileInfoDTO uploadFile(UUID checkInID, CompletedFileUpload file) {
        return new FileInfoDTO();
    }

    public FileInfoDTO uploadDocument(String directory,
                                      String name, String text) {
        documentName = name;
        documentText = text;
        return new FileInfoDTO();
    }

    public boolean deleteFile(String uploadDocId) {
        return true;
    }
}

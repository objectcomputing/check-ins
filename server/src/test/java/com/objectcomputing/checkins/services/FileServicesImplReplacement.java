package com.objectcomputing.checkins.services;

import com.objectcomputing.checkins.services.file.FileInfoDTO;
import com.objectcomputing.checkins.services.file.FileServicesImpl;
import com.objectcomputing.checkins.services.file.FileServicesBaseImpl;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocument;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocumentServices;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import io.micronaut.http.multipart.CompletedFileUpload;

import java.io.IOException;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Set;
import java.util.HashMap;
import java.util.function.Function;

import jakarta.inject.Singleton;
import io.micronaut.core.util.StringUtils;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;

@Singleton
@Replaces(FileServicesImpl.class)
@Requires(property = "replace.fileservicesimpl", value = StringUtils.TRUE)
public class FileServicesImplReplacement extends FileServicesBaseImpl {
    private HashMap<String, byte[]> map = new HashMap<>();

    public FileServicesImplReplacement(
                                CheckInServices checkInServices,
                                CheckinDocumentServices checkinDocumentServices,
                                MemberProfileServices memberProfileServices,
                                CurrentUserServices currentUserServices) {
        super(checkInServices, checkinDocumentServices, memberProfileServices,
              currentUserServices);
    }

    // *******************************************************************
    // Test Interface
    // *******************************************************************

    public boolean shouldThrow = false;

    public void reset() {
        shouldThrow = false;
        map.clear();
    }

    public FileInfoDTO addFile(String key, byte[] content) {
        map.put(key, content);
        return setFileInfo(key, null);
    }

    public FileInfoDTO addFile(String key, byte[] content,
                               Function<String, CheckinDocument> consumer) {
        map.put(key, content);
        return setFileInfo(key, consumer.apply(key));
    }

    public String getFile(String key) {
        return new String(map.get(key));
    }

    // *******************************************************************
    // Overrides for FileServicesBaseImpl
    // *******************************************************************

    @Override
    public FileInfoDTO uploadDocument(String directory,
                                      String name, String text) {
        return addFile(directory + "/" + name, text.getBytes());
    }

    @Override
    protected void getCheckinDocuments(
        Set<FileInfoDTO> result, Set<CheckinDocument> checkinDocuments) throws IOException {
        checkThrow();
        if (checkinDocuments.isEmpty()) {
            for(String key : map.keySet()) {
                result.add(setFileInfo(key, null));
            }
        } else {
            for (CheckinDocument cd : checkinDocuments) {
                result.add(setFileInfo(cd.getUploadDocId(), cd));
            }
        }
    }

    @Override
    protected void downloadSingleFile(
                   String docId, FileOutputStream myWriter) throws IOException {
        checkThrow();
        if (map.containsKey(docId)) {
            myWriter.write(map.get(docId));
        } else {
          throw new IOException("File does not exist.");
        }
    }

    @Override
    protected FileInfoDTO uploadSingleFile(
                CompletedFileUpload file, String directoryName,
                Function<String, CheckinDocument> consumer) throws IOException {
        checkThrow();
        String key = directoryName + "/" + file.getFilename();
        map.put(key, file.getInputStream().readAllBytes());
        return setFileInfo(key, consumer.apply(key));
    }

    @Override
    protected void deleteSingleFile(String docId) throws IOException {
        checkThrow();
        if (map.containsKey(docId)) {
            map.remove(docId);
        } else {
          throw new IOException("File does not exist.");
        }
    }

    private FileInfoDTO setFileInfo(String key, CheckinDocument cd) {
        FileInfoDTO dto = new FileInfoDTO();
        dto.setFileId(key);
        dto.setName(Paths.get(key).getFileName().toString());
        dto.setSize((long)(map.get(key).length));
        if (cd != null) {
            dto.setCheckInId(cd.getCheckinsId());
        }
        return dto;
    }

    private void checkThrow() throws IOException {
      if (shouldThrow) {
          // This ensures that IOExceptions thrown from these overridden
          // methods are caught in FileServicesBaseImpl and converted to
          // FileRetrievalException objects.
          throw new IOException("Unable to access Google Drive");
      }
    }
}

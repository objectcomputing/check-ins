package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;

import io.micronaut.http.multipart.CompletedFileUpload;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.UUID;

public interface ReportDataUploadServices {
    public enum DataType {
        compensationHistory, positionHistory, currentInformation
    }

    void store(UUID memberId, CompletedFileUpload file) throws IOException, BadArgException;

    ByteBuffer get(UUID memberId, DataType dataType) throws NotFoundException;
}

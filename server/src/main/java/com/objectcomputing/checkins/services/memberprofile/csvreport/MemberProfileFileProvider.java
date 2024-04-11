package com.objectcomputing.checkins.services.memberprofile.csvreport;

import jakarta.inject.Singleton;

import java.io.File;
import java.io.IOException;

@Singleton
public class MemberProfileFileProvider {

    public File provideFile() {
        try {
            File csvFile = File.createTempFile("member_profiles", ".csv");
            csvFile.deleteOnExit();
            return csvFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

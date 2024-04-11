package com.objectcomputing.checkins.services.skill_record;

import jakarta.inject.Singleton;

import java.io.File;
import java.io.IOException;

@Singleton
public class SkillRecordFileProvider {
    public File provideFile() {
        try {
            File csvFile = File.createTempFile("skill_records", ".csv");
            csvFile.deleteOnExit();
            return csvFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.objectcomputing.checkins.services.skill_record;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;

import jakarta.inject.Singleton;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Singleton
class SkillRecordServicesImpl implements SkillRecordServices {

    private final SkillRecordRepository skillRecordRepository;
    private final SkillRecordFileProvider skillRecordFileProvider;

    public SkillRecordServicesImpl(SkillRecordRepository skillRecordRepository, SkillRecordFileProvider skillRecordFileProvider) {
        this.skillRecordRepository = skillRecordRepository;
        this.skillRecordFileProvider = skillRecordFileProvider;
    }

    @Override
    @RequiredPermission(Permission.CAN_VIEW_SKILL_CATEGORIES)
    public File generateFile() throws IOException {
        List<SkillRecord> skillRecords = skillRecordRepository.findAll();

        String[] headers = {"name", "description", "extraneous", "pending", "category_name"};
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader(headers).setQuote('"').build();

        File csvFile = skillRecordFileProvider.provideFile();
        try (FileWriter fileWriter = new FileWriter(csvFile, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(fileWriter, csvFormat)
        ) {
            for (SkillRecord skillRecord : skillRecords) {
                printer.printRecord(
                        skillRecord.getName(),
                        skillRecord.getDescription(),
                        skillRecord.isExtraneous(),
                        skillRecord.isPending(),
                        skillRecord.getCategoryName()
                );
            }
        }

        return csvFile;
    }
}

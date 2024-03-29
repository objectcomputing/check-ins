package com.objectcomputing.checkins.services.skill_record;

import jakarta.inject.Singleton;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Singleton
public class SkillRecordServicesImpl implements SkillRecordServices {

    private final SkillRecordRepository skillRecordRepository;

    public SkillRecordServicesImpl(SkillRecordRepository skillRecordRepository) {
        this.skillRecordRepository = skillRecordRepository;
    }

    @Override
    public File generateFile() {
        List<SkillRecord> skillRecords = skillRecordRepository.findAll();

        String[] headers = { "name", "description", "extraneous", "pending", "category_name" };
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(headers).withQuote('"');

        File csvFile = new File("skill_records.csv");
        try (final CSVPrinter printer = new CSVPrinter(new FileWriter(csvFile, StandardCharsets.UTF_8), csvFormat)) {
            for (SkillRecord record : skillRecords) {
                printer.printRecord(record.getName(), record.getDescription(), record.isExtraneous(), record.isPending(), record.getCategoryName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return csvFile;
    }
}

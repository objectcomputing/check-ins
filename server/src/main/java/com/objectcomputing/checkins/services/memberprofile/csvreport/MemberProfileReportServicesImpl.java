package com.objectcomputing.checkins.services.memberprofile.csvreport;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class MemberProfileReportServicesImpl implements MemberProfileReportServices {

    private final MemberProfileReportRepository memberProfileReportRepository;

    @Value("${aes.key}")
    private String key;

    public MemberProfileReportServicesImpl(MemberProfileReportRepository memberProfileReportRepository) {
        this.memberProfileReportRepository = memberProfileReportRepository;
    }

    @Override
    public File generateFile(MemberProfileReportQueryDTO queryDTO) {
        List<MemberProfileRecord> memberRecords = new ArrayList<>();
        if (queryDTO == null || queryDTO.getMemberIds() == null) {
            List<MemberProfileRecord> allRecords = memberProfileReportRepository.findAll();
            memberRecords.addAll(allRecords);
        } else {
            List<String> memberIds = queryDTO.getMemberIds().stream().map(UUID::toString).collect(Collectors.toList());
            List<MemberProfileRecord> filteredRecords = memberProfileReportRepository.findAllByMemberIds(memberIds, key);
            memberRecords.addAll(filteredRecords);
        }

        return createCsv(memberRecords);
    }

    private File createCsv(List<MemberProfileRecord> memberRecords) {
        File csvFile = new File("member_profiles.csv");
        CSVFormat csvFormat = getCsvFormat();

        try (final CSVPrinter printer = new CSVPrinter(new FileWriter(csvFile, StandardCharsets.UTF_8), csvFormat)) {
            for (MemberProfileRecord record : memberRecords) {
                printer.printRecord(
                        record.getFirstName(),
                        record.getLastName(),
                        record.getTitle(),
                        record.getLocation(),
                        record.getWorkEmail(),
                        record.getStartDate(),
                        record.getTenure(),
                        record.getPdlName(),
                        record.getPdlEmail(),
                        record.getSupervisorName(),
                        record.getSupervisorEmail()
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return csvFile;
    }

    public static CSVFormat getCsvFormat() {
        String[] headers = { "First Name", "Last Name", "Title", "Location", "Work Email", "Start Date", "Tenure",
                "PDL Name", "PDL Email", "Supervisor Name", "Supervisor Email" };
        return CSVFormat.DEFAULT
                .withHeader(headers)
                .withQuote('"');
    }
}

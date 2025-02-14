package com.objectcomputing.checkins.services.memberprofile.csvreport;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;

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

@Singleton
public class MemberProfileReportServicesImpl implements MemberProfileReportServices {

    private final MemberProfileReportRepository memberProfileReportRepository;
    private final MemberProfileFileProvider memberProfileFileProvider;

    public MemberProfileReportServicesImpl(MemberProfileReportRepository memberProfileReportRepository,
                                           MemberProfileFileProvider memberProfileFileProvider) {
        this.memberProfileReportRepository = memberProfileReportRepository;
        this.memberProfileFileProvider = memberProfileFileProvider;
    }

    @Override
    @RequiredPermission(Permission.CAN_VIEW_PROFILE_REPORT)
    public File generateFile(MemberProfileReportQueryDTO queryDTO) throws IOException {
        List<MemberProfileRecord> memberRecords = new ArrayList<>();
        if (queryDTO == null || queryDTO.getMemberIds() == null) {
            List<MemberProfileRecord> allRecords = memberProfileReportRepository.findAll();
            memberRecords.addAll(allRecords);
        } else {
            List<String> memberIds = queryDTO.getMemberIds().stream().map(UUID::toString).toList();
            List<MemberProfileRecord> filteredRecords = memberProfileReportRepository.findAllByMemberIds(memberIds);
            memberRecords.addAll(filteredRecords);
        }
        return createCsv(memberRecords);
    }

    private File createCsv(List<MemberProfileRecord> memberRecords) throws IOException {
        File csvFile = memberProfileFileProvider.provideFile();
        CSVFormat csvFormat = getCsvFormat();
        try (final CSVPrinter printer = new CSVPrinter(new FileWriter(csvFile, StandardCharsets.UTF_8), csvFormat)) {
            for (MemberProfileRecord memberProfileRecord : memberRecords) {
                printer.printRecord(
                        memberProfileRecord.getFirstName(),
                        memberProfileRecord.getLastName(),
                        memberProfileRecord.getTitle(),
                        memberProfileRecord.getLocation(),
                        memberProfileRecord.getWorkEmail(),
                        memberProfileRecord.getStartDate(),
                        memberProfileRecord.getTenure(),
                        memberProfileRecord.getPdlName(),
                        memberProfileRecord.getPdlEmail(),
                        memberProfileRecord.getSupervisorName(),
                        memberProfileRecord.getSupervisorEmail()
                );
            }
        }
        return csvFile;
    }

    public static CSVFormat getCsvFormat() {
        String[] headers = {"First Name", "Last Name", "Title", "Location", "Work Email", "Start Date", "Tenure",
                "PDL Name", "PDL Email", "Supervisor Name", "Supervisor Email"};
        return CSVFormat.DEFAULT
                .builder()
                .setHeader(headers)
                .setQuote('"')
                .build();
    }
}

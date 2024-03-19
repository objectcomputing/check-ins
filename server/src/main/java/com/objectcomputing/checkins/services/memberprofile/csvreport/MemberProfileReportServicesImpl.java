package com.objectcomputing.checkins.services.memberprofile.csvreport;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import jakarta.inject.Singleton;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Singleton
public class MemberProfileReportServicesImpl implements MemberProfileReportServices {

    private final MemberProfileServices memberProfileServices;

    public MemberProfileReportServicesImpl(MemberProfileServices memberProfileServices) {
        this.memberProfileServices = memberProfileServices;
    }

    @Override
    public File generateFile() {
        List<MemberProfile> memberProfiles = memberProfileServices.findAll();

        String[] headers = { "First Name", "Last Name", "Title", "Location", "Work Email", "Start Date", "Tenure",
                "PDL Name", "PDL Email", "Supervisor Name", "Supervisor Email" };
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(headers).withQuote('"');

        File csvFile = new File("member_profiles.csv");
        try (final CSVPrinter printer = new CSVPrinter(new FileWriter(csvFile, StandardCharsets.UTF_8), csvFormat)) {
            for (MemberProfile memberProfile : memberProfiles) {
                printer.printRecord(memberProfile.getFirstName(), memberProfile.getLastName(),
                        memberProfile.getTitle(), memberProfile.getLocation(), memberProfile.getWorkEmail(),
                        memberProfile.getStartDate(), "Tenure", memberProfile.getPdlId(),
                        memberProfile.getSupervisorid(), memberProfile.getSupervisorid());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return csvFile;
    }
}

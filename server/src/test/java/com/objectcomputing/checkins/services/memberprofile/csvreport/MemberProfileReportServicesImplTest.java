package com.objectcomputing.checkins.services.memberprofile.csvreport;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.CurrentUserServicesReplacement;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.StringUtils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Property(name = "replace.currentuserservices", value = StringUtils.TRUE)
class MemberProfileReportServicesImplTest extends TestContainersSuite
                                          implements MemberProfileFixture, RoleFixture {
    @Inject
    CurrentUserServicesReplacement currentUserServices;

    @Inject
    private MemberProfileServices memberProfileServices;

    @Inject
    private MemberProfileReportServicesImpl memberProfileReportServices;

    @BeforeEach
    void setUp() {
        createAndAssignRoles();
    }

    @Test
    void testGenerateFileWithAllMemberProfiles() throws IOException {
        List<MemberProfileRecord> expectedRecords = createSampleRecords();
        File tmpFile = File.createTempFile("member",".csv");
        tmpFile.deleteOnExit();

        // Generate a file with all members
        File file = memberProfileReportServices.generateFile(null);
        assertNotNull(file);

        List<CSVRecord> records = parseRecordsFromFile(file);

        assertEquals(3, records.size());
        CSVRecord csvRecord1 = records.get(0);
        assertRecordEquals(expectedRecords.get(0), csvRecord1);
        CSVRecord csvRecord2 = records.get(1);
        assertRecordEquals(expectedRecords.get(1), csvRecord2);
        CSVRecord csvRecord3 = records.get(2);
        assertRecordEquals(expectedRecords.get(2), csvRecord3);
    }

    @Test
    void testGenerateFileWithSelectedMemberProfiles() throws IOException {
        List<MemberProfileRecord> allRecords = createSampleRecords();
        MemberProfileRecord expectedRecord = allRecords.get(1);
        File tmpFile = File.createTempFile("member",".csv");
        tmpFile.deleteOnExit();

        // Generate a file with selected members
        MemberProfileReportQueryDTO dto = new MemberProfileReportQueryDTO();
        dto.setMemberIds(List.of(expectedRecord.getId()));
        File file = memberProfileReportServices.generateFile(dto);
        assertNotNull(file);

        List<CSVRecord> records = parseRecordsFromFile(file);

        assertEquals(1, records.size());
        CSVRecord csvRecord1 = records.get(0);
        assertRecordEquals(expectedRecord, csvRecord1);
    }

    private static void assertRecordEquals(MemberProfileRecord record, CSVRecord csvRecord) {
        assertEquals(record.getFirstName(), csvRecord.get("First Name"));
        assertEquals(record.getLastName(), csvRecord.get("Last Name"));
        assertEquals(record.getTitle(), csvRecord.get("Title"));
        assertEquals(record.getLocation(), csvRecord.get("Location"));
        assertEquals(record.getWorkEmail(), csvRecord.get("Work Email"));
        assertEquals(record.getStartDate().toString(), csvRecord.get("Start Date"));
        assertEquals(record.getTenure(), csvRecord.get("Tenure"));
        assertEquals(record.getPdlName() == null ? "" : record.getPdlName(), csvRecord.get("PDL Name"));
        assertEquals(record.getPdlEmail() == null ? "" : record.getPdlEmail(), csvRecord.get("PDL Email"));
        assertEquals(record.getSupervisorName() == null ? "" : record.getSupervisorName(), csvRecord.get("Supervisor Name"));
        assertEquals(record.getSupervisorEmail() == null ? "" : record.getSupervisorEmail(), csvRecord.get("Supervisor Email"));
    }

    static List<CSVRecord> parseRecordsFromFile(File file) throws IOException {
        Reader fileReader = new FileReader(file);
        CSVFormat csvFormat = MemberProfileReportServicesImpl.getCsvFormat().withSkipHeaderRecord();

        CSVParser parser = csvFormat.parse(fileReader);
        return parser.getRecords();
    }

    MemberProfileRecord from(MemberProfile profile) {
        MemberProfileRecord record1 = new MemberProfileRecord();
        record1.setId(profile.getId());
        record1.setFirstName(profile.getFirstName());
        record1.setLastName(profile.getLastName());
        record1.setTitle(profile.getTitle());
        record1.setLocation(profile.getLocation());
        record1.setWorkEmail(profile.getWorkEmail());
        record1.setStartDate(profile.getStartDate());
        UUID pdlId = profile.getPdlId();
        if (pdlId != null) {
            MemberProfile pdl = memberProfileServices.getById(pdlId);
            if (pdl != null) {
                record1.setPdlName(pdl.getFirstName() + " " + pdl.getLastName());
                record1.setPdlEmail(pdl.getWorkEmail());
            }
        }
        UUID supervisorId = profile.getSupervisorid();
        if (supervisorId != null) {
            MemberProfile supervisor =
                memberProfileServices.getById(supervisorId);
            if (supervisor != null) {
                record1.setSupervisorName(supervisor.getFirstName() + " " +
                                          supervisor.getLastName());
                record1.setSupervisorEmail(supervisor.getWorkEmail());
            }
        }
        return record1;
    }

    private List<MemberProfileRecord> createSampleRecords() {
        // A user must have the CAN_VIEW_PROFILE_REPORT to create this report.
        currentUserServices.currentUser = createAThirdDefaultMemberProfile();
        assignAdminRole(currentUserServices.currentUser);

        // The createADefaultMemberProfileForPdl() method actually sets both
        // the PDL and Supervisor to the id of the member profile passed in.
        MemberProfileRecord record1 = from(currentUserServices.currentUser);
        MemberProfile pdl = createADefaultMemberProfile();
        MemberProfileRecord record2 = from(pdl);
        MemberProfileRecord record3 = from(createADefaultMemberProfileForPdl(pdl));
        return List.of(record1, record2, record3);
    }
}

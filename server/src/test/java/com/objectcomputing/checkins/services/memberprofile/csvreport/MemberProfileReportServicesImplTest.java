package com.objectcomputing.checkins.services.memberprofile.csvreport;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberProfileReportServicesImplTest {

    @Mock
    private MemberProfileReportRepository memberProfileReportRepository;

    @InjectMocks
    private MemberProfileReportServicesImpl memberProfileReportServices;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(memberProfileReportRepository);
    }

    @Test
    void testGenerateFileWithAllMemberProfiles() throws IOException {
        List<MemberProfileRecord> expectedRecords = createSampleRecords();
        when(memberProfileReportRepository.findAll()).thenReturn(expectedRecords);

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

    private static List<MemberProfileRecord> createSampleRecords() {
        MemberProfileRecord record1 = new MemberProfileRecord();
        record1.setId(UUID.randomUUID());
        record1.setFirstName("John");
        record1.setLastName("Doe");
        record1.setTitle("Software Engineer");
        record1.setLocation("St. Louis");
        record1.setWorkEmail("johndoe@objectcomputing.com");
        record1.setStartDate(LocalDate.of(2024, 1, 1));
        record1.setTenure("3 months");
        record1.setPdlName("Jane Miller");
        record1.setPdlEmail("janemiller@objectcomputing.com");
        record1.setSupervisorName("Tom Smith");
        record1.setSupervisorEmail("tomsmith@objectcomputing.com");

        MemberProfileRecord record2 = new MemberProfileRecord();
        record2.setId(UUID.randomUUID());
        record2.setFirstName("Jane");
        record2.setLastName("Miller");
        record2.setTitle("Principal Software Engineer");
        record2.setLocation("St. Louis");
        record2.setWorkEmail("janemiller@objectcomputing.com");
        record2.setStartDate(LocalDate.of(2023, 1, 1));
        record2.setTenure("1 year, 3 months");
        record2.setPdlName("Eve Williams");
        record2.setPdlEmail("evewilliams@objectcomputing.com");
        record2.setSupervisorName("Tom Smith");
        record2.setSupervisorEmail("tomsmith@objectcomputing.com");

        MemberProfileRecord record3 = new MemberProfileRecord();
        record3.setId(UUID.randomUUID());
        record3.setFirstName("Tom");
        record3.setLastName("Smith");
        record3.setTitle("Manager");
        record3.setLocation("St. Louis");
        record3.setWorkEmail("tomsmith@objectcomputing.com");
        record3.setStartDate(LocalDate.of(2022, 1, 1));
        record3.setTenure("2 years, 3 months");
        record3.setPdlName(null);
        record3.setPdlEmail(null);
        record3.setSupervisorName(null);
        record3.setSupervisorEmail(null);

        return List.of(record1, record2, record3);
    }
}

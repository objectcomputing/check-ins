package com.objectcomputing.checkins.services.memberprofile.csvreport;

import com.objectcomputing.checkins.services.TestContainersSuite;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

// Disabled in nativeTest, as we get an exception from Mockito
//    => java.lang.NoClassDefFoundError: Could not initialize class org.mockito.internal.configuration.plugins.Plugins
@DisabledInNativeImage
class MemberProfileReportServicesImplTest extends TestContainersSuite {

    @Mock
    private MemberProfileReportRepository memberProfileReportRepository;

    @Mock
    private MemberProfileFileProvider memberProfileFileProvider;

    @InjectMocks
    private MemberProfileReportServicesImpl memberProfileReportServices;

    private AutoCloseable mockFinalizer;

    @BeforeAll
    void initMocks() {
        mockFinalizer = MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(memberProfileReportRepository);
    }

    @AfterAll
    void close() throws Exception {
        mockFinalizer.close();
    }

    @Test
    void testGenerateFileWithAllMemberProfiles() throws IOException {
        List<MemberProfileRecord> expectedRecords = createSampleRecords();
        when(memberProfileReportRepository.findAll()).thenReturn(expectedRecords);
        File tmpFile = File.createTempFile("member",".csv");
        tmpFile.deleteOnExit();
        when(memberProfileFileProvider.provideFile()).thenReturn(tmpFile);

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
        when(memberProfileReportRepository
                .findAllByMemberIds(List.of(expectedRecord.getId().toString())))
                .thenReturn(List.of(expectedRecord));
        File tmpFile = File.createTempFile("member",".csv");
        tmpFile.deleteOnExit();
        when(memberProfileFileProvider.provideFile()).thenReturn(tmpFile);
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

    @Test
    void testGenerateFileNotGenerated() throws IOException {
        List<MemberProfileRecord> allRecords = createSampleRecords();
        MemberProfileRecord expectedRecord = allRecords.get(1);
        when(memberProfileReportRepository
                .findAllByMemberIds(List.of(expectedRecord.getId().toString())))
                .thenReturn(List.of(expectedRecord));

        when(memberProfileFileProvider.provideFile()).thenThrow(new RuntimeException());
        // Generate a file with selected members
        MemberProfileReportQueryDTO dto = new MemberProfileReportQueryDTO();
        dto.setMemberIds(List.of(expectedRecord.getId()));

        assertThrows(RuntimeException.class, () -> {
            memberProfileReportServices.generateFile(dto);
        });
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
        record2.setPdlName("Eve Williams");
        record2.setPdlEmail("evewilliams@objectcomputing.com");
        record2.setSupervisorName("Tom Smith");
        record2.setSupervisorEmail("tomsmith@objectcomputing.com");

        MemberProfileRecord record3 = new MemberProfileRecord();
        record3.setId(UUID.randomUUID());
        record3.setFirstName("Tom");
        record3.setLastName("Smith");
        record3.setTitle("Manager, HR, and Head of Sales");
        record3.setLocation("New York City, New York");
        record3.setWorkEmail("tomsmith@objectcomputing.com");
        record3.setStartDate(LocalDate.of(2022, 1, 1));
        record3.setPdlName(null);
        record3.setPdlEmail(null);
        record3.setSupervisorName(null);
        record3.setSupervisorEmail(null);

        return List.of(record1, record2, record3);
    }
}

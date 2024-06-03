package com.objectcomputing.checkins.services.skill_record;

import com.objectcomputing.checkins.services.TestContainersSuite;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SkillRecordServicesImplTest extends TestContainersSuite {

    @Mock
    private SkillRecordRepository skillRecordRepository;

    @Mock
    private SkillRecordFileProvider skillRecordFileProvider;

    @InjectMocks
    private SkillRecordServicesImpl skillRecordServices;

    private AutoCloseable mockFinalizer;

    @BeforeAll
    void initMocks() {
        mockFinalizer = MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        reset(skillRecordRepository);
    }

    @AfterAll
    void close() throws Exception {
        mockFinalizer.close();
    }

    @Test
    @Order(1)
    void testFileGeneration() throws IOException {
        SkillRecord record1 = new SkillRecord();
        record1.setName("Java");
        record1.setDescription("Various technical skills");
        record1.setExtraneous(true);
        record1.setPending(true);
        record1.setCategoryName("Languages, Libraries, and Frameworks");

        when(skillRecordRepository.findAll()).thenReturn(Collections.singletonList(record1));
        File tmpFile = File.createTempFile("foobar",".csv");
        tmpFile.deleteOnExit();
        when(skillRecordFileProvider.provideFile()).thenReturn(tmpFile);
        File file = skillRecordServices.generateFile();
        assertNotNull(file);

        Reader fileReader = new FileReader(file);

        String[] headers = { "name", "description", "extraneous", "pending", "category_name" };
        CSVFormat csvFormat = CSVFormat.DEFAULT
                .withHeader(headers)
                .withQuote('"')
                .withSkipHeaderRecord();

        CSVParser parser = csvFormat.parse(fileReader);
        List<CSVRecord> records = parser.getRecords();

        assertEquals(1, records.size());
        CSVRecord csvRecord = records.get(0);
        assertEquals(record1.getName(), csvRecord.get("name"));
        assertEquals(record1.getDescription(), csvRecord.get("description"));
        assertEquals(record1.isExtraneous(), Boolean.valueOf(csvRecord.get("extraneous")));
        assertEquals(record1.isPending(), Boolean.valueOf(csvRecord.get("pending")));
        assertEquals(record1.getCategoryName(), csvRecord.get("category_name"));
    }

    @Test
    @Order(2)
    void testNoFileGenerated() throws IOException {
        SkillRecord record1 = new SkillRecord();
        record1.setName("Java");
        record1.setDescription("Various technical skills");
        record1.setExtraneous(true);
        record1.setPending(true);
        record1.setCategoryName("Languages, Libraries, and Frameworks");

        when(skillRecordRepository.findAll()).thenReturn(Collections.singletonList(record1));

        when(skillRecordFileProvider.provideFile()).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> {
            skillRecordServices.generateFile();
        });
    }

}
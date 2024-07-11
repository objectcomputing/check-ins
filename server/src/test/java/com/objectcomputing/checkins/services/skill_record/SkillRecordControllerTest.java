package com.objectcomputing.checkins.services.skill_record;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.SkillCategoryFixture;
import com.objectcomputing.checkins.services.fixture.SkillCategorySkillFixture;
import com.objectcomputing.checkins.services.fixture.SkillFixture;
import com.objectcomputing.checkins.services.skillcategory.SkillCategory;
import com.objectcomputing.checkins.services.skills.Skill;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkillRecordControllerTest extends TestContainersSuite implements RoleFixture, SkillCategoryFixture, SkillCategorySkillFixture, SkillFixture {

    private final Map<String, Skill> skills = new HashMap<>();

    @Inject
    @Client("/services/skills/records")
    private HttpClient client;

    @BeforeEach
    void buildSkillHierarchy() {
        createAndAssignRoles();

        SkillCategory magicCategory = createSkillCategory("Magic", "Magical skills");
        skills.put("conjuring", createSkill("Conjuring", false, "Conjuring skills", false));
        skills.put("divination", createSkill("Divination", true, "Conjuring skills", false));
        createSkillCategorySkill(magicCategory.getId(), skills.get("conjuring").getId());
        createSkillCategorySkill(magicCategory.getId(), skills.get("divination").getId());

        SkillCategory programmingCategory = createSkillCategory("Programming", "Programming skills");
        skills.put("java", createSkill("Java", false, "Java programming skills", false));
        skills.put("rust", createSkill("Rust", false, "Rust programming skills", true));
        createSkillCategorySkill(programmingCategory.getId(), skills.get("java").getId());
        createSkillCategorySkill(programmingCategory.getId(), skills.get("rust").getId());
    }

    @Test
    @SuppressWarnings("resource")
    void testGetSuccess() {
        HttpRequest<?> request = HttpRequest.GET("/csv").basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        String contentDisposition = response.header(HttpHeaders.CONTENT_DISPOSITION);
        assertTrue(contentDisposition.startsWith("attachment; filename=skill_records"), "Unexpected content disposition: " + contentDisposition);
        assertTrue(contentDisposition.endsWith(".csv"), "Unexpected content disposition: " + contentDisposition);

        String body = response.body();

        String[] headers = {"name", "description", "extraneous", "pending", "category_name"};
        CSVFormat csvFormat = CSVFormat.DEFAULT
                .builder()
                .setHeader(headers)
                .setQuote('"')
                .setSkipHeaderRecord(true)
                .build();

        CSVParser parser = assertDoesNotThrow(() -> csvFormat.parse(new StringReader(body)));
        List<CSVRecord> records = parser.getRecords();

        assertEquals(skills.size(), records.size());

        // There's no order guaranteed in the view, so to avoid flakiness we need to check for the presence of each line
        List<String> lines = records.stream().map(r -> r.stream().collect(Collectors.joining(","))).toList();
        List<Executable> expectations = """
                Conjuring,Conjuring skills,false,false,Magic
                Divination,Conjuring skills,false,true,Magic
                Java,Java programming skills,false,false,Programming
                Rust,Rust programming skills,true,false,Programming"""
                .lines()
                .map(line -> (Executable) () -> assertTrue(lines.contains(line), "Line not found: " + line))
                .toList();
        assertAll(expectations);
    }

    @Test
    void testGetNotAllowed() {
        HttpRequest<?> request = HttpRequest.GET("/csv").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, String.class)
        );

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }
}
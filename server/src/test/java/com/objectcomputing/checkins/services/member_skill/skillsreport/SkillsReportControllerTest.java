package com.objectcomputing.checkins.services.member_skill.skillsreport;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.MemberSkillFixture;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import org.junit.Test;

import javax.inject.Inject;

public class SkillsReportControllerTest extends TestContainersSuite implements MemberSkillFixture, MemberProfileFixture {

    @Inject
    @Client("/reports/skills")
    HttpClient client;

    @Test
    void testReport() {

    }
}

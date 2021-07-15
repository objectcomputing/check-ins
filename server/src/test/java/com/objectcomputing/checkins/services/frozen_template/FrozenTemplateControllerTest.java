package com.objectcomputing.checkins.services.frozen_template;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.*;

import static org.junit.jupiter.api.Assertions.*;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class FrozenTemplateControllerTest extends TestContainersSuite implements RepositoryFixture, RoleFixture, FrozenTemplateFixture, MemberProfileFixture, FeedbackRequestFixture {

    @Inject
    @Client("/services/feedback/frozen_templates")
    HttpClient client;

    void assertContentEqualsEntity(FrozenTemplate template, FrozenTemplateResponseDTO res) {
        assertEquals(template.getCreatedBy(), res.getCreatedBy());
        assertEquals(template.getDescription(), res.getDescription());
        assertEquals(template.getTitle(), res.getTitle());
    }


    @Test
    void testPostByRequestCreator( ) {
        MemberProfile memberOne = createADefaultMemberProfile();
        MemberProfile requestee = createADefaultMemberProfileForPdl(memberOne);
        MemberProfile recipient = createADefaultRecipient();
        FrozenTemplate temp = new FrozenTemplate("Random Title", "Random description", memberOne.getId());
//        //create frozen template
        FrozenTemplateCreateDTO dto = new FrozenTemplateCreateDTO();
        dto.setTitle("Random Title");
        dto.setDescription("Random description");
        dto.setCreatedBy(memberOne.getId());

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FrozenTemplateResponseDTO> response = client.toBlocking().exchange(request, FrozenTemplateResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertContentEqualsEntity(temp, response.getBody().get());


    }


    @Test
    void testPostAdmin() {
        MemberProfile memberOne = createADefaultMemberProfile();
        FrozenTemplate temp = new FrozenTemplate("Random Title", "Random description", memberOne.getId());
//        //create frozen template
        FrozenTemplateCreateDTO dto = new FrozenTemplateCreateDTO();
        dto.setTitle("Random Title");
        dto.setDescription("Random description");
        dto.setCreatedBy(memberOne.getId());

        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FrozenTemplateResponseDTO> response = client.toBlocking().exchange(request, FrozenTemplateResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertContentEqualsEntity(temp, response.getBody().get());

    }

    @Test
    void testGetByIdAdmin() {
        MemberProfile memberOne = createADefaultMemberProfile();
        MemberProfile memberTwo = createADefaultSupervisor();
        MemberProfile admin = createASecondDefaultMemberProfile();
        createDefaultAdminRole(admin);
        FrozenTemplate temp = saveDefaultFrozenTemplate(memberTwo.getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", temp.getId()))
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FrozenTemplateResponseDTO> response = client.toBlocking().exchange(request, FrozenTemplateResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertContentEqualsEntity(temp, response.getBody().get());


    }

    @Test
    void getByIdMember() {
        MemberProfile memberOne = createADefaultMemberProfile();
        MemberProfile memberTwo = createADefaultSupervisor();
        MemberProfile admin = createASecondDefaultMemberProfile();
        createDefaultAdminRole(admin);
        FrozenTemplate temp = saveDefaultFrozenTemplate(memberTwo.getId());

        final HttpRequest<?> request = HttpRequest.GET(String.format("%s", temp.getId()))
                .basicAuth(memberOne.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FrozenTemplateResponseDTO> response = client.toBlocking().exchange(request, FrozenTemplateResponseDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertContentEqualsEntity(temp, response.getBody().get());

    }

}

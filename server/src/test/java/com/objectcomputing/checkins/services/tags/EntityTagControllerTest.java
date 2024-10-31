package com.objectcomputing.checkins.services.tags;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.EntityTagFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.TagFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.tags.entityTag.EntityTag;
import com.objectcomputing.checkins.services.tags.entityTag.EntityTag.EntityType;
import com.objectcomputing.checkins.services.tags.entityTag.EntityTagCreateDTO;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityTagControllerTest extends TestContainersSuite implements EntityTagFixture, TagFixture, MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/entity-tags")
        HttpClient client;


    @Test
    void testCreateAEntityTag() {
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Tag tag = createADefaultTag() ;
        EntityType type = EntityType.SKILL;

        EntityTagCreateDTO entityTagCreateDTO = new EntityTagCreateDTO();
        entityTagCreateDTO.setEntityId(memberProfile.getId());
        entityTagCreateDTO.setTagId(tag.getId());
        entityTagCreateDTO.setType(type);

            final HttpRequest<EntityTagCreateDTO> request = HttpRequest.POST("", entityTagCreateDTO).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
            final HttpResponse<EntityTag> response = client.toBlocking().exchange(request, EntityTag.class);

        EntityTag entityTag = response.body();

        assertEquals(entityTag, response.body());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), entityTag.getId()), response.getHeaders().get("location"));
        assertEquals(entityTag.getType(), type);

    }


    @Test
    void testCreateAnInvalidEntityTag() {
        EntityTagCreateDTO entityTagCreateDTO = new EntityTagCreateDTO();

        final HttpRequest<EntityTagCreateDTO> request = HttpRequest.POST("", entityTagCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("_embedded").get("errors");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        List<String> errorList = List.of(errors.get(0).get("message").asText(), errors.get(1).get("message").asText(), errors.get(2).get("message").asText())
                .stream().sorted().collect(Collectors.toList());
        assertEquals("entityTag.entityId: must not be null", errorList.get(0));
        assertEquals("entityTag.tagId: must not be null", errorList.get(1));
        assertEquals("entityTag.type: must not be null", errorList.get(2));
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateANullEntityTag() {

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [entityTag] not specified", error.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    void createAEntityTagForNonExistingTag() {
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        EntityTagCreateDTO entityTagCreateDTO = new EntityTagCreateDTO();
        entityTagCreateDTO.setEntityId(UUID.randomUUID());
        entityTagCreateDTO.setType(EntityType.TEAM);
        entityTagCreateDTO.setTagId(UUID.randomUUID());

        final HttpRequest<EntityTagCreateDTO> request = HttpRequest.POST("", entityTagCreateDTO).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Tag %s doesn't exist",entityTagCreateDTO.getTagId()),error);

    }

    @Test
    void createAEntityTagForExistingTag() {
        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();

        EntityTagCreateDTO entityTagCreateDTO = new EntityTagCreateDTO();
        entityTagCreateDTO.setEntityId(memberProfile.getId());
        entityTagCreateDTO.setType(EntityType.SKILL);
        entityTagCreateDTO.setTagId(UUID.randomUUID());

        final HttpRequest<EntityTagCreateDTO> request = HttpRequest.POST("", entityTagCreateDTO).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Tag %s doesn't exist",entityTagCreateDTO.getTagId()),error);

    }

    @Test
    void deleteEntityTagAsAdmin() {

        MemberProfile user = createAnUnrelatedUser();
        createAndAssignAdminRole(user);

        MemberProfile memberProfile = createADefaultMemberProfile();
        Tag tag = createADefaultTag();
        EntityType type = EntityType.SKILL;

        final HttpRequest<Object> request = HttpRequest.DELETE(memberProfile.getId().toString()).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadAllEntityTags() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Tag tag = createADefaultTag();
        EntityType type = EntityType.SKILL;

        EntityTag entityTag = createADefaultEntityTag(memberProfile,tag, type);

        final HttpRequest<Object> request = HttpRequest.GET(String.format("/?entityId=%s&tagId=%s","","")).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<EntityTag>> response = client.toBlocking().exchange(request, Argument.setOf(EntityTag.class));

        assertEquals(Set.of(entityTag), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testReadEntityTag() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Tag tag = createADefaultTag();
        EntityType type = EntityType.TEAM;

        EntityTag entityTag = createADefaultEntityTag(memberProfile,tag, type);

        final HttpRequest<Object> request = HttpRequest.GET(String.format("/%s", entityTag.getId().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<EntityTag> response = client.toBlocking().exchange(request, EntityTag.class);

        assertEquals(entityTag, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindEntityTags() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Tag tag = createADefaultTag();
        EntityType type = EntityType.SKILL;

        EntityTag entityTag = createADefaultEntityTag(memberProfile, tag, type);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?entityId=%s&tagId=%s", entityTag.getEntityId(),
                entityTag.getTagId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<EntityTag>> response = client.toBlocking().exchange(request, Argument.setOf(EntityTag.class));

        assertEquals(Set.of(entityTag), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindEntityTagsByEntityId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Tag tag = createADefaultTag();
        EntityType type = EntityType.SKILL;

        EntityTag entityTag = createADefaultEntityTag(memberProfile,tag, type);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?entityId=%s", entityTag.getEntityId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<EntityTag>> response = client.toBlocking().exchange(request, Argument.setOf(EntityTag.class));

        assertEquals(Set.of(entityTag), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindEntityIdByTagId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Tag tag = createADefaultTag();
        EntityType type = EntityType.TEAM;

        EntityTag entityTag = createADefaultEntityTag(memberProfile, tag, type);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?tagId=%s", tag.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<EntityTag>> response = client.toBlocking().exchange(request, Argument.setOf(EntityTag.class));

        assertEquals(Set.of(entityTag), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

}

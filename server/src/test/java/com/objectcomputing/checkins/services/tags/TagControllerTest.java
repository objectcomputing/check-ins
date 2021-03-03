package com.objectcomputing.checkins.services.tags;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.TagFixture;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TagControllerTest extends TestContainersSuite implements TagFixture {

    @Inject
    @Client("/services/tag")
    HttpClient client;


    @Test
    void testCreateATag() {
        String generatedString = RandomStringUtils.randomAlphabetic(10);
        TagCreateDTO tagCreateDTO = new TagCreateDTO();
        tagCreateDTO.setName(generatedString);

        final HttpRequest<TagCreateDTO> request = HttpRequest.POST("", tagCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Tag> response = client.toBlocking().exchange(request, Tag.class);

        Tag tag = response.body();

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), tag.getId()), response.getHeaders().get("location"));
        assertEquals(tag.getName(), generatedString);
    }

    @Test
    void deleteTagAsAdmin() {
        Tag tag = createADefaultTag();
        String name = "";


        final HttpRequest<Object> request = HttpRequest.DELETE(tag.getId().toString()).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
    }


    @Test
    void testReadAllTags() {
        Tag tag = createADefaultTag();
        Tag secondTag = createASecondaryTag();

        final HttpRequest<Object> request = HttpRequest.GET("/?").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<Tag>> response = client.toBlocking().exchange(request, Argument.setOf(Tag.class));

        assertEquals(Set.of(tag, secondTag), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testReadTag() {
        Tag tag = createADefaultTag();
        String name = null;

        final HttpRequest<Object> request = HttpRequest.GET(String.format("/?%s", tag.getId().toString())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Tag> response = client.toBlocking().exchange(request, Tag.class);

        assertEquals(tag, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadTagNotFound() {

        final HttpRequest<Object> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Tag.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testFindTag() {
        Tag tag = createADefaultTag();
        String name = null;

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?tagid=%s", tag.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<Tag>> response = client.toBlocking().exchange(request, Argument.setOf(Tag.class));

        assertEquals(Set.of(tag), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindTagByTagId() {
        Tag tag = createADefaultTag();
        String name = null;


        final HttpRequest<?> request = HttpRequest.GET(String.format("/?tagid=%s", tag.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<Tag>> response = client.toBlocking().exchange(request, Argument.setOf(Tag.class));

        assertEquals(Set.of(tag), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    public void testPUTUpdateTag() {

        String name = null;
        Tag tag = createADefaultTag();

        final HttpRequest<?> request = HttpRequest.PUT("/", tag).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        final HttpResponse<Tag> response = client.toBlocking().exchange(request, Tag.class);

        assertEquals(tag, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), tag.getId()),
                response.getHeaders().get("location"));
    }

    @Test
    public void testPUTUpdateNullTag() {

        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals("Required Body [tag] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());

    }

    @Test
    public void testPUTUpdateNonexistentTag() {

        Tag tag = createADefaultTag();
        String name = null;

        tag.setId(UUID.randomUUID());

        final HttpRequest<Tag> request = HttpRequest.PUT("/", tag).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");

        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());

    }

}

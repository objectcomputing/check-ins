package com.objectcomputing.checkins.services.github;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GithubControllerTest extends TestContainersSuite {
    @Inject
    @Client("/services/github-issue")
    HttpClient client;

    @Test
    public void testBadRequestExceptionThrownWithBlankTitle() {
        IssueCreateDTO issue = new IssueCreateDTO();
        issue.setTitle("");
        issue.setBody("body");

        MutableHttpRequest<IssueCreateDTO> request = HttpRequest.POST("", issue).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    public void testBadRequestExceptionThrownWithBlankBody() {
        IssueCreateDTO issue = new IssueCreateDTO();
        issue.setTitle("title");
        issue.setBody("");

        MutableHttpRequest<IssueCreateDTO> request = HttpRequest.POST("", issue).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }
}

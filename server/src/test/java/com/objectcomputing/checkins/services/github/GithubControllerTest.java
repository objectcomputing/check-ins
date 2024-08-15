package com.objectcomputing.checkins.services.github;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GithubControllerTest extends TestContainersSuite {

    private EmbeddedServer mockGithub;
    private EmbeddedServer application;
    private BlockingHttpClient client;

    @BeforeAll
    @SuppressWarnings("resource")
        // We close all these in the cleanup
    void startClient() {
        // Make a GitHub mock application (with no datasource, and security turned off)
        mockGithub = ApplicationContext.run(EmbeddedServer.class, Map.of(
                "datasources.enabled", StringUtils.FALSE,
                "micronaut.security.enabled", StringUtils.FALSE,
                "spec.name", "GithubControllerTest"
        ));

        application = ApplicationContext.run(EmbeddedServer.class, Stream.concat(
                Map.of("github-credentials.github-url", mockGithub.getURL() + "/").entrySet().stream(),
                getProperties().entrySet().stream()
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        client = application.getApplicationContext().createBean(HttpClient.class, application.getURL()).toBlocking();
    }

    @AfterAll
    void cleanup() throws IOException {
        mockGithub.close();
        application.close();
        client.close();
    }

    @Test
    void testBadRequestExceptionThrownWithBlankTitle() {
        IssueCreateDTO issue = new IssueCreateDTO();
        issue.setTitle("");
        issue.setBody("body");

        MutableHttpRequest<IssueCreateDTO> request = HttpRequest.POST("/services/github-issue", issue).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.exchange(request, Map.class));
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testBadRequestExceptionThrownWithBlankBody() {
        IssueCreateDTO issue = new IssueCreateDTO();
        issue.setTitle("title");
        issue.setBody("");

        MutableHttpRequest<IssueCreateDTO> request = HttpRequest.POST("/services/github-issue", issue).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.exchange(request, Map.class));
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void checkWeCanPostDataToMockedGithub() {
        IssueCreateDTO issue = new IssueCreateDTO();
        issue.setTitle("title");
        issue.setBody("body");
        issue.setLabels(new String[]{"label1", "label2"});

        MutableHttpRequest<IssueCreateDTO> request = HttpRequest.POST("/services/github-issue", issue).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpResponse<IssueResponseDTO> response = client.exchange(request, IssueResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("title", response.body().getUrl());
        assertEquals("body-label1-label2-token test github token", response.body().getHtmlUrl());
    }

    @Requires(property = "spec.name", value = "GithubControllerTest")
    @Controller("/repos/objectcomputing/check-ins")
    static class GithubIssues {

        /**
         * Returns an issue as defined in <a href="https://docs.github.com/en/rest/issues/issues?apiVersion=2022-11-28#create-an-issue">the docs</a>.
         * But with url and html_url set, so we can test the deserialization upstream.
         *
         * @param request       the request object
         * @param authorization the auth header passed through
         * @return the json document for the issue
         */
        @Post("/issues")
        @Status(HttpStatus.CREATED)
        @Produces(MediaType.APPLICATION_JSON)
        String sendIssue(@Body IssueCreateDTO request, @Header String authorization) {
            return """
                    {
                      "id": 1,
                      "node_id": "MDU6SXNzdWUx",
                      "url": "%s",
                      "repository_url": "https://api.github.com/repos/octocat/Hello-World",
                      "labels_url": "https://api.github.com/repos/octocat/Hello-World/issues/1347/labels{/name}",
                      "comments_url": "https://api.github.com/repos/octocat/Hello-World/issues/1347/comments",
                      "events_url": "https://api.github.com/repos/octocat/Hello-World/issues/1347/events",
                      "html_url": "%s",
                      "number": 1347,
                      "state": "open",
                      "title": "Found a bug",
                      "body": "I'm having a problem with this.",
                      "user": {
                        "login": "octocat",
                        "id": 1,
                        "node_id": "MDQ6VXNlcjE=",
                        "avatar_url": "https://github.com/images/error/octocat_happy.gif",
                        "gravatar_id": "",
                        "url": "https://api.github.com/users/octocat",
                        "html_url": "https://github.com/octocat",
                        "followers_url": "https://api.github.com/users/octocat/followers",
                        "following_url": "https://api.github.com/users/octocat/following{/other_user}",
                        "gists_url": "https://api.github.com/users/octocat/gists{/gist_id}",
                        "starred_url": "https://api.github.com/users/octocat/starred{/owner}{/repo}",
                        "subscriptions_url": "https://api.github.com/users/octocat/subscriptions",
                        "organizations_url": "https://api.github.com/users/octocat/orgs",
                        "repos_url": "https://api.github.com/users/octocat/repos",
                        "events_url": "https://api.github.com/users/octocat/events{/privacy}",
                        "received_events_url": "https://api.github.com/users/octocat/received_events",
                        "type": "User",
                        "site_admin": false
                      },
                      "labels": [
                        {
                          "id": 208045946,
                          "node_id": "MDU6TGFiZWwyMDgwNDU5NDY=",
                          "url": "https://api.github.com/repos/octocat/Hello-World/labels/bug",
                          "name": "bug",
                          "description": "Something isn't working",
                          "color": "f29513",
                          "default": true
                        }
                      ],
                      "assignee": {
                        "login": "octocat",
                        "id": 1,
                        "node_id": "MDQ6VXNlcjE=",
                        "avatar_url": "https://github.com/images/error/octocat_happy.gif",
                        "gravatar_id": "",
                        "url": "https://api.github.com/users/octocat",
                        "html_url": "https://github.com/octocat",
                        "followers_url": "https://api.github.com/users/octocat/followers",
                        "following_url": "https://api.github.com/users/octocat/following{/other_user}",
                        "gists_url": "https://api.github.com/users/octocat/gists{/gist_id}",
                        "starred_url": "https://api.github.com/users/octocat/starred{/owner}{/repo}",
                        "subscriptions_url": "https://api.github.com/users/octocat/subscriptions",
                        "organizations_url": "https://api.github.com/users/octocat/orgs",
                        "repos_url": "https://api.github.com/users/octocat/repos",
                        "events_url": "https://api.github.com/users/octocat/events{/privacy}",
                        "received_events_url": "https://api.github.com/users/octocat/received_events",
                        "type": "User",
                        "site_admin": false
                      },
                      "assignees": [
                        {
                          "login": "octocat",
                          "id": 1,
                          "node_id": "MDQ6VXNlcjE=",
                          "avatar_url": "https://github.com/images/error/octocat_happy.gif",
                          "gravatar_id": "",
                          "url": "https://api.github.com/users/octocat",
                          "html_url": "https://github.com/octocat",
                          "followers_url": "https://api.github.com/users/octocat/followers",
                          "following_url": "https://api.github.com/users/octocat/following{/other_user}",
                          "gists_url": "https://api.github.com/users/octocat/gists{/gist_id}",
                          "starred_url": "https://api.github.com/users/octocat/starred{/owner}{/repo}",
                          "subscriptions_url": "https://api.github.com/users/octocat/subscriptions",
                          "organizations_url": "https://api.github.com/users/octocat/orgs",
                          "repos_url": "https://api.github.com/users/octocat/repos",
                          "events_url": "https://api.github.com/users/octocat/events{/privacy}",
                          "received_events_url": "https://api.github.com/users/octocat/received_events",
                          "type": "User",
                          "site_admin": false
                        }
                      ],
                      "milestone": {
                        "url": "https://api.github.com/repos/octocat/Hello-World/milestones/1",
                        "html_url": "https://github.com/octocat/Hello-World/milestones/v1.0",
                        "labels_url": "https://api.github.com/repos/octocat/Hello-World/milestones/1/labels",
                        "id": 1002604,
                        "node_id": "MDk6TWlsZXN0b25lMTAwMjYwNA==",
                        "number": 1,
                        "state": "open",
                        "title": "v1.0",
                        "description": "Tracking milestone for version 1.0",
                        "creator": {
                          "login": "octocat",
                          "id": 1,
                          "node_id": "MDQ6VXNlcjE=",
                          "avatar_url": "https://github.com/images/error/octocat_happy.gif",
                          "gravatar_id": "",
                          "url": "https://api.github.com/users/octocat",
                          "html_url": "https://github.com/octocat",
                          "followers_url": "https://api.github.com/users/octocat/followers",
                          "following_url": "https://api.github.com/users/octocat/following{/other_user}",
                          "gists_url": "https://api.github.com/users/octocat/gists{/gist_id}",
                          "starred_url": "https://api.github.com/users/octocat/starred{/owner}{/repo}",
                          "subscriptions_url": "https://api.github.com/users/octocat/subscriptions",
                          "organizations_url": "https://api.github.com/users/octocat/orgs",
                          "repos_url": "https://api.github.com/users/octocat/repos",
                          "events_url": "https://api.github.com/users/octocat/events{/privacy}",
                          "received_events_url": "https://api.github.com/users/octocat/received_events",
                          "type": "User",
                          "site_admin": false
                        },
                        "open_issues": 4,
                        "closed_issues": 8,
                        "created_at": "2011-04-10T20:09:31Z",
                        "updated_at": "2014-03-03T18:58:10Z",
                        "closed_at": "2013-02-12T13:22:01Z",
                        "due_on": "2012-10-09T23:39:01Z"
                      },
                      "locked": true,
                      "active_lock_reason": "too heated",
                      "comments": 0,
                      "pull_request": {
                        "url": "https://api.github.com/repos/octocat/Hello-World/pulls/1347",
                        "html_url": "https://github.com/octocat/Hello-World/pull/1347",
                        "diff_url": "https://github.com/octocat/Hello-World/pull/1347.diff",
                        "patch_url": "https://github.com/octocat/Hello-World/pull/1347.patch"
                      },
                      "closed_at": null,
                      "created_at": "2011-04-22T13:33:48Z",
                      "updated_at": "2011-04-22T13:33:48Z",
                      "closed_by": {
                        "login": "octocat",
                        "id": 1,
                        "node_id": "MDQ6VXNlcjE=",
                        "avatar_url": "https://github.com/images/error/octocat_happy.gif",
                        "gravatar_id": "",
                        "url": "https://api.github.com/users/octocat",
                        "html_url": "https://github.com/octocat",
                        "followers_url": "https://api.github.com/users/octocat/followers",
                        "following_url": "https://api.github.com/users/octocat/following{/other_user}",
                        "gists_url": "https://api.github.com/users/octocat/gists{/gist_id}",
                        "starred_url": "https://api.github.com/users/octocat/starred{/owner}{/repo}",
                        "subscriptions_url": "https://api.github.com/users/octocat/subscriptions",
                        "organizations_url": "https://api.github.com/users/octocat/orgs",
                        "repos_url": "https://api.github.com/users/octocat/repos",
                        "events_url": "https://api.github.com/users/octocat/events{/privacy}",
                        "received_events_url": "https://api.github.com/users/octocat/received_events",
                        "type": "User",
                        "site_admin": false
                      },
                      "author_association": "COLLABORATOR",
                      "state_reason": "completed"
                    }""".formatted(request.getTitle(), "%s-%s-%s".formatted(request.getBody(), String.join("-", request.getLabels()), authorization));
        }
    }
}

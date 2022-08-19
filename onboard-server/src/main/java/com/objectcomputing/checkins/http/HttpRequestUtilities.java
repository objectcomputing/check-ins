package com.objectcomputing.checkins.http;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpRequest;

import static io.micronaut.http.HttpHeaders.*;

public interface HttpRequestUtilities<B> extends HttpRequest<B> {

    @SuppressWarnings("unchecked")
    static <T> MutableHttpRequest<T> GET(String uri) {
        return (MutableHttpRequest<T>)HttpRequest.GET(uri)
                .header(USER_AGENT, "Micronaut HTTP Client")
                .header(ACCEPT, "application/vnd.github.v3+json, application/json");

    }

    @SuppressWarnings("unchecked")
    static <T> MutableHttpRequest<T> GET(String uri, String token) {
        return (MutableHttpRequest<T>)GET(uri)
                .bearerAuth(token);
    }

    static <T> MutableHttpRequest<T> POST(String uri, T body) {
        return HttpRequest.POST(uri, body)
                .header(USER_AGENT, "Micronaut HTTP Client")
                .header(ACCEPT, "application/vnd.github.v3+json, application/json");

    }

    static <T> MutableHttpRequest<T> POST(String uri, T body, String token) {
        return POST(uri, body)
                .bearerAuth(token);
    }
}

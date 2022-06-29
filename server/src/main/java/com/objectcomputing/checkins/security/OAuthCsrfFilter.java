package com.objectcomputing.checkins.security;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.*;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import org.reactivestreams.Publisher;


@Filter(value = {"/services/*"})
@Requires(env = Environment.GOOGLE_COMPUTE)
public class OAuthCsrfFilter implements HttpServerFilter {

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        HttpHeaders headers = request.getHeaders();

        String headerValue = headers.get("X-CSRF-Header", String.class).orElse("");
        String cookieValue = request.getCookies().findCookie("_csrf").map(Cookie::getValue).orElse(null);


        if (cookieValue == null || !cookieValue.equals(headerValue)) {
            return Publishers.just(HttpResponse.status(HttpStatus.FORBIDDEN));
        }

        return chain.proceed(request);
    }
}
package com.objectcomputing.checkins.security;

import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.*;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.filter.OncePerRequestHttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.reactivestreams.Publisher;

import java.util.*;


@Filter(value = {"/services/*", "/services/*"})
public class OAuthCsrfFilter extends OncePerRequestHttpServerFilter {

    @Override
    protected Publisher<MutableHttpResponse<?>> doFilterOnce(HttpRequest<?> request, ServerFilterChain chain) {
        HttpHeaders headers = request.getHeaders();

        String connectValue1 = headers.get("cookie", String.class).orElse("").toLowerCase(Locale.ENGLISH);
        System.out.println("request paramater is:::"+connectValue1);
        String cookieValue = request.getCookies().findCookie("_csrf").map(Cookie::getValue).orElse(null);
        System.out.println("request cookie is:::"+cookieValue);


//        if (cookieValue == null || !cookieValue.equals(requestParameter)) {
//            return Publishers.just(HttpResponse.status(HttpStatus.FORBIDDEN));
//        }

        return chain.proceed(request);
    }
}
package com.objectcomputing.checkins.security;

import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.filter.OncePerRequestHttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import org.reactivestreams.Publisher;


@Filter(value = {"/services/check-in", "/services/check-in/*"})
public class OAuthCsrfFilter extends OncePerRequestHttpServerFilter {

    @Override
    protected Publisher<MutableHttpResponse<?>> doFilterOnce(HttpRequest<?> request, ServerFilterChain chain) {
        String requestParameter = request.getParameters().get("myCat");
        System.out.println("request paramater is:::"+requestParameter);
        String cookieValue = request.getCookies().findCookie("myCat").map(Cookie::getValue).orElse(null);
        System.out.println("request cookie is:::"+cookieValue);


//        if (cookieValue == null || !cookieValue.equals(requestParameter)) {
//            return Publishers.just(HttpResponse.status(HttpStatus.FORBIDDEN));
//        }

        return chain.proceed(request);
    }
}
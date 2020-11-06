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


@Filter(value = {"/*", "/*"})
public class OAuthCsrfFilter extends OncePerRequestHttpServerFilter {

    @Override
    protected Publisher<MutableHttpResponse<?>> doFilterOnce(HttpRequest<?> request, ServerFilterChain chain) {

        Map<String, String> map = new HashMap<String, String>();
        HttpHeaders headers = request.getHeaders();
        String connectValue = headers.get("host", String.class).orElse("").toLowerCase(Locale.ENGLISH);


        Iterator it = headers.iterator();

        String connectValue1 = headers.get("cookie", String.class).orElse("").toLowerCase(Locale.ENGLISH);


        while(it.hasNext()) {
            System.out.println("value::"+it.next());
        }


        Collection<MediaType> headerNames = request.accept();
//        while (headerNames.)headerNames {
//            String key = (String) headerNames.nextElement();
//            String value = request.getHeader(key);
//            map.put(key, value);
//        }


        String requestParameter = request.getParameters().get("_csrf");
        System.out.println("request paramater is:::"+requestParameter);
        String cookieValue = request.getCookies().findCookie("_csrf").map(Cookie::getValue).orElse(null);
        System.out.println("request cookie is:::"+cookieValue);


//        if (cookieValue == null || !cookieValue.equals(requestParameter)) {
//            return Publishers.just(HttpResponse.status(HttpStatus.FORBIDDEN));
//        }

        return chain.proceed(request);
    }
}
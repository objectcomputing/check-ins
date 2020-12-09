package com.objectcomputing.checkins.logging;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.inject.ExecutableMethod;
import io.micronaut.security.authentication.AuthenticationUserDetailsAdapter;
import io.micronaut.web.router.MethodBasedRoute;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static io.micronaut.http.annotation.Filter.MATCH_ALL_PATTERN;

@Filter(MATCH_ALL_PATTERN)
public class RequestLoggingInterceptor implements HttpServerFilter {
    private final Logger LOG = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    public boolean intercept(HttpRequest request) {
        String requestVerb = request.getMethodName();
        String username = "not authenticated";
        Optional<AuthenticationUserDetailsAdapter> auth = request.getAttribute("micronaut.AUTHENTICATION", AuthenticationUserDetailsAdapter.class);
        if (auth.isEmpty()) {
           return false; //Seems to fire twice per request. First time without auth, so we just skip that one.
        }
        else if (!auth.get().getName().isBlank()){
            username = auth.get().getName();
        }
        Optional<MethodBasedRoute> route = request.getAttribute("micronaut.http.route", MethodBasedRoute.class);
        if(route.isPresent()) {
            MethodBasedRoute routeBuilder = route.get();
            ExecutableMethod targetMethod = routeBuilder.getTargetMethod().getExecutableMethod();
            String params = "";
            request.getParameters().forEach((key, value) -> params.concat(key).concat(":").concat(value.toString()));
            Optional<String> requestBody = request.getBody(String.class);
            LOG.info(String.format("User %s %s request to %s with body %s and parameters %s being handled by %s.%s",
                    username, requestVerb, request.getUri().getPath(), requestBody.orElse("empty"), params.isEmpty() ? "empty" : params,
                    targetMethod.getDeclaringType().getSimpleName(), targetMethod.getName()));
        }
        return true;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        return Flowable.fromCallable(() -> intercept(request))
                .switchMap(bool -> chain.proceed(request));
    }
}

package com.objectcomputing.checkins.logging;

import com.objectcomputing.checkins.Environments;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import io.micronaut.inject.ExecutableMethod;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.filters.SecurityFilter;
import io.micronaut.web.router.MethodBasedRouteInfo;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.micronaut.http.annotation.Filter.MATCH_ALL_PATTERN;

@Filter(MATCH_ALL_PATTERN)
@Requires(env = Environments.LOCAL)
public class RequestLoggingInterceptor implements HttpServerFilter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public int getOrder() {
        // Run after the security filter, so we can log the user
        return ServerFilterPhase.SECURITY.order() + 1;
    }

    private boolean intercept(HttpRequest<?> request) {
        Optional<Authentication> auth = request.getAttribute(SecurityFilter.AUTHENTICATION, Authentication.class);
        request.getAttribute(HttpAttributes.ROUTE_INFO, MethodBasedRouteInfo.class).ifPresent(routeBuilder -> {
            String username = auth.map(Principal::getName).map(n -> n.isBlank() ? null : n).orElse("not authenticated");
            String requestVerb = request.getMethodName();
            ExecutableMethod<?, ?> targetMethod = routeBuilder.getTargetMethod().getExecutableMethod();
            String params = request.getParameters().asMap().entrySet().stream()
                    .map(e -> e.getKey() + ":" + e.getValue())
                    .collect(Collectors.joining(","));
            String requestBody = request.getBody(String.class).orElse("empty");
            LOG.info(
                    "User {} {} request to {} with body {} and parameters {} being handled by {}.{}",
                    username,
                    requestVerb,
                    request.getUri().getPath(),
                    requestBody,
                    params.isEmpty() ? "empty" : params,
                    targetMethod.getDeclaringType().getSimpleName(),
                    targetMethod.getName()
            );
        });
        return true;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        Mono<Boolean> interceptMono = Mono.fromCallable(() -> intercept(request));
        return Flux.from(interceptMono).switchMap(bool -> chain.proceed(request));
    }
}

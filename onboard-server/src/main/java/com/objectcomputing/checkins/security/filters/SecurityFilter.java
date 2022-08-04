package com.objectcomputing.checkins.security.filters;

import com.objectcomputing.checkins.security.authentication.AuthenticatedActor;
import com.objectcomputing.checkins.security.authentication.AuthenticatedActorFetcher;
import com.objectcomputing.checkins.security.authorization.AuthorizationException;
import com.objectcomputing.checkins.security.filters.config.SecurityFilterConfigurationProperties;
import com.objectcomputing.checkins.security.rules.SecurityRule;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.order.Ordered;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import io.micronaut.security.rules.SecurityRuleResult;
import io.micronaut.web.router.RouteMatch;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Requires(property = SecurityFilterConfigurationProperties.PREFIX + ".enabled", notEquals = StringUtils.FALSE, defaultValue = StringUtils.TRUE)
@Filter("${" + SecurityFilterConfigurationProperties.PREFIX + ".pattern:" + Filter.MATCH_ALL_PATTERN + "}")
public class SecurityFilter implements HttpServerFilter {
    public static final String KEY = SecurityFilterConfigurationProperties.PREFIX + "." + SecurityFilter.class.getSimpleName();

    private static final Logger LOG = LoggerFactory.getLogger(SecurityFilter.class);

    public static final String TOKEN = io.micronaut.security.filters.SecurityFilter.TOKEN.toString();
    public static final String JWT_TOKEN = "geoai.jwt-token";

    /**
     * The attribute used to store the authentication object in the request.
     */
    public static final CharSequence AUTHENTICATED_ACTOR = HttpAttributes.PRINCIPAL.toString();

    /**
     * The attribute used to store if the request was rejected and why.
     */
    public static final CharSequence REJECTION = "micronaut.security.REJECTION";

    private static final Integer ORDER = ServerFilterPhase.SECURITY.order();

    private final Collection<SecurityRule> securityRules;
    private final Collection<AuthenticatedActorFetcher> authenticatedActorFetchers;

    SecurityFilter(Collection<SecurityRule> securityRules,
                   Collection<AuthenticatedActorFetcher> authenticatedActorFetchers) {

        this.securityRules = securityRules;
        this.authenticatedActorFetchers = authenticatedActorFetchers;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(final HttpRequest<?> request, ServerFilterChain chain) {
        request.getAttributes().put(KEY, true);
        RouteMatch<?> routeMatch = request.getAttribute(HttpAttributes.ROUTE_MATCH, RouteMatch.class).orElse(null);

        return Flux.fromIterable(authenticatedActorFetchers)
                .flatMap(authenticatedActorFetcher -> {
                    LOG.debug("Authenticated actor fetcher {}", authenticatedActorFetcher);
                    return authenticatedActorFetcher.fetchAuthenticatedActor(request);
                })
                .next()
                .flatMap(authenticatedActor -> {
                    LOG.debug("Authenticated Actor {}", authenticatedActor);
                    return Mono.from(createResponse(authenticatedActor, request, chain, routeMatch));
                })
                .switchIfEmpty(Flux.defer(() -> {
                    LOG.debug("Empty Actor");
                    return createResponse(null, request, chain, routeMatch);
                }).next());
    }

    private Publisher<MutableHttpResponse<?>> createResponse(AuthenticatedActor<?,?,?> authenticatedActor, HttpRequest<?> request, ServerFilterChain chain, RouteMatch<?> routeMatch) {
        final String method = request.getMethod().toString();
        final String path = request.getPath();

        boolean forbidden = authenticatedActor != null;

        return Flux.fromIterable(securityRules)
                .concatMap(rule -> Mono.from(rule.check(request, routeMatch, authenticatedActor))
                        .defaultIfEmpty(SecurityRuleResult.UNKNOWN)
                        // Ideally should return just empty but filter the unknowns
                        .filter((result) -> result != SecurityRuleResult.UNKNOWN)
                        .doOnSuccess((result) -> logResult(result, method, path, rule)))
                .next()
                .flatMapMany(result -> {
                    if (result == SecurityRuleResult.REJECTED) {
                        request.setAttribute(
                                REJECTION, forbidden ? HttpStatus.FORBIDDEN : HttpStatus.UNAUTHORIZED);
                        return Mono.error(new AuthorizationException(authenticatedActor));
                    } else if (result == SecurityRuleResult.ALLOWED) {
                        return chain.proceed(request);
                    } else {
                        return Mono.empty();
                    }
                })
                .switchIfEmpty(Flux.defer(() -> {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                                "Authorized request {} {}. No rule provider authorized or rejected the request.",
                                method,
                                path);
                    }

                    // no rule found for the given request
                    return chain.proceed(request);
                }));
    }

    private void logResult(SecurityRuleResult result, String method, String path, Ordered ordered) {
        if (LOG.isDebugEnabled()) {
            if (result == SecurityRuleResult.REJECTED) {
                LOG.debug(
                        "Unauthorized request {} {}. The rule provider {} rejected the request.",
                        method,
                        path,
                        ordered.getClass().getName());
            } else if (result == SecurityRuleResult.ALLOWED) {
                LOG.debug(
                        "Authorized request {} {}. The rule provider {} authorized the request.",
                        method,
                        path,
                        ordered.getClass().getName());
            }
        }
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}

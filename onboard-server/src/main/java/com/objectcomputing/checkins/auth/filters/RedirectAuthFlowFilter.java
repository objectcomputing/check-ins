package com.objectcomputing.checkins.auth.filters;

import com.objectcomputing.checkins.security.filters.config.SecurityFilterConfigurationProperties;
import com.objectcomputing.checkins.security.authentication.token.jwt.SignedJsonWebToken;
import com.objectcomputing.checkins.security.authentication.token.jwt.signature.SignedJsonWebTokenSignatureVerifier;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.security.token.jwt.cookie.AccessTokenCookieConfiguration;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.text.ParseException;
import java.time.Duration;
import java.util.Optional;

@Requires(property = SecurityFilterConfigurationProperties.PREFIX + ".enabled", notEquals = StringUtils.FALSE, defaultValue = StringUtils.TRUE)
@Filter("/auth/redirect")
//@Filter(patternStyle =  FilterPatternStyle.REGEX, patterns = "\\/redirect")
public class RedirectAuthFlowFilter implements HttpServerFilter {
    public static final String KEY = SecurityFilterConfigurationProperties.PREFIX + "." + RedirectAuthFlowFilter.class.getSimpleName();

    private static final Integer ORDER = ServerFilterPhase.SECURITY.after();

    private static final Logger LOG = LoggerFactory.getLogger(RedirectAuthFlowFilter.class);

    private final AccessTokenCookieConfiguration cookieConfiguration;
    private final SignedJsonWebTokenSignatureVerifier tokenVerifier;

    public RedirectAuthFlowFilter(AccessTokenCookieConfiguration cookieConfiguration,
                                  SignedJsonWebTokenSignatureVerifier tokenVerifier) {
        this.cookieConfiguration = cookieConfiguration;
        this.tokenVerifier = tokenVerifier;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        String target = request.getParameters().get("target");
        String token = request.getParameters().get("token");

        if(StringUtils.isEmpty(target) || StringUtils.isEmpty(token)) {
            //TODO 2/15/2022 LOG IT
            return Flux.just(HttpResponse.unauthorized());
        }

        // TODO 2/15/2022 Is Target an approved site ?  Add Check

        Optional<SignedJsonWebToken> jsonWebTokenOpt = parseToken(token);
        if(jsonWebTokenOpt.isPresent() && tokenVerifier.verify(jsonWebTokenOpt.get())) {
            MutableHttpResponse<?> response = HttpResponse.seeOther(UriBuilder.of(target).build());
            response.cookie(createCookieOf(token));
            return Flux.just(response);
        }
        return Flux.just(HttpResponse.serverError());
    }

    private Optional<SignedJsonWebToken> parseToken(String token) {
        try {
            return Optional.of(SignedJsonWebToken.parse(token));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    private Cookie createCookieOf(String token) {
        Cookie cookie = Cookie.of(cookieConfiguration.getCookieName(), token);
        cookie.configure(cookieConfiguration);
        cookie.maxAge(cookieConfiguration.getCookieMaxAge().orElseGet(() -> Duration.ofDays(14)));
        return cookie;
    }

    @Override
    public int getOrder() {
        return HttpServerFilter.super.getOrder();
    }
}

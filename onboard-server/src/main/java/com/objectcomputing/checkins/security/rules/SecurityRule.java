package com.objectcomputing.checkins.security.rules;

import com.objectcomputing.geoai.security.authentication.AuthenticatedActor;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.order.Ordered;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.rules.SecurityRuleResult;
import io.micronaut.web.router.RouteMatch;
import org.reactivestreams.Publisher;

public interface SecurityRule extends Ordered {

    Publisher<SecurityRuleResult> check(HttpRequest<?> request, @Nullable RouteMatch<?> routeMatch, @Nullable AuthenticatedActor<?,?,?> authentication);
}

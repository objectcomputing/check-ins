package com.objectcomputing.checkins.services;

import com.objectcomputing.checkins.notifications.social_media.SlackPoster;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;

import jakarta.inject.Singleton;

import java.util.List;
import java.util.ArrayList;

@Singleton
@Replaces(SlackPoster.class)
@Requires(property = "replace.slackposter", value = StringUtils.TRUE)
public class SlackPosterReplacement extends SlackPoster {
    public final List<String> posted = new ArrayList<>();

    public void reset() {
        posted.clear();
    }

    public HttpResponse post(String slackBlock) {
        posted.add(slackBlock);
        return HttpResponse.status(HttpStatus.OK);
    }
}


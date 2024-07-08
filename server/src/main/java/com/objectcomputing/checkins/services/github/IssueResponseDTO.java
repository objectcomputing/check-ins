package com.objectcomputing.checkins.services.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Introspected
public class IssueResponseDTO {

    private String url;

    @JsonProperty("html_url")
    private String htmlUrl;
}

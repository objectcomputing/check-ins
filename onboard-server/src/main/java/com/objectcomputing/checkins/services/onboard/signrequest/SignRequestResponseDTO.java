package com.objectcomputing.checkins.services.onboard.signrequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

@Introspected
public class SignRequestResponseDTO {
    private String url;

    @JsonProperty("html_url")
    private String htmlUrl;

    public SignRequestResponseDTO() {}

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public String getHtmlUrl() { return htmlUrl; }

    public void setHtmlUrl(String htmlUrl) { this.htmlUrl = htmlUrl; }

    @Override
    public String toString() {
        return "SignRequestResponseDTO{" +
                "url='" + url + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                '}';
    }
}

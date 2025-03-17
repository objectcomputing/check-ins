package com.objectcomputing.checkins.services.github;

import io.micronaut.context.annotation.ConfigurationProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("github-credentials")
class GithubConfig {

    @NotBlank
    private String githubToken;

    @NotBlank
    private String githubUrl;
}

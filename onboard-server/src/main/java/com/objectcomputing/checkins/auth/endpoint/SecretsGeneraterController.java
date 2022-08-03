package com.objectcomputing.checkins.auth.endpoint;

import com.nimbusds.srp6.SRP6ClientCredentials;
import com.objectcomputing.geoai.platform.auth.commons.ChallengeEncodingRequest;
import com.objectcomputing.geoai.platform.auth.commons.SecretsRequest;
import com.objectcomputing.geoai.security.authentication.srp6.Srp6Challenge;
import com.objectcomputing.geoai.security.authentication.srp6.Srp6Secrets;
import com.objectcomputing.geoai.security.authentication.srp6.client.Srp6ClientCredentialsFactory;
import com.objectcomputing.geoai.security.authentication.srp6.client.Srp6ClientSecretsFactory;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static com.nimbusds.srp6.BigIntegerUtils.toHex;

@Controller("/auth/api/secrets")
public class SecretsGeneraterController {

    private final Srp6ClientSecretsFactory srp6ClientSecretsFactory;
    private final Srp6ClientCredentialsFactory srp6ClientCredentialsFactory;

    public SecretsGeneraterController(Srp6ClientSecretsFactory srp6ClientSecretsFactory,
                                      Srp6ClientCredentialsFactory srp6ClientCredentialsFactory) {

        this.srp6ClientSecretsFactory = srp6ClientSecretsFactory;
        this.srp6ClientCredentialsFactory = srp6ClientCredentialsFactory;
    }

    @Post("/generate")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String,Object> generate(@Body SecretsRequest secretsRequest) {
        Srp6Secrets secrets =
                srp6ClientSecretsFactory.generateSecrets(secretsRequest.getIdentity(), secretsRequest.getSecret());

        return Map.of("salt", secrets.getSalt(), "verifier", secrets.getVerifier(), "timestamp", Instant.now().toString());
    }

    @Post("/encode")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String,Object> encode(@Body ChallengeEncodingRequest challengeEncodingRequest) {
        Optional<SRP6ClientCredentials> opt =  srp6ClientCredentialsFactory.generateSrp6ClientCredentials(
                challengeEncodingRequest.getIdentity(), challengeEncodingRequest.getSecret(),
                new Srp6Challenge(challengeEncodingRequest.getSalt(), challengeEncodingRequest.getB()));

        if(opt.isPresent()) {
            SRP6ClientCredentials srp6credentials = opt.get();
            String encodedSecret = String.format("%s:%s", toHex(srp6credentials.M1), toHex(srp6credentials.A));
            return Map.of("encoding", encodedSecret, "timestamp", Instant.now().toString());
        } else {
            return Map.of("success", Boolean.FALSE);
        }
    }
}

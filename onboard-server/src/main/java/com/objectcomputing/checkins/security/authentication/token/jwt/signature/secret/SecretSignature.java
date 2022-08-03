package com.objectcomputing.checkins.security.authentication.token.jwt.signature.secret;

import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenHeader;
import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenPayload;
import com.objectcomputing.geoai.security.token.jwt.SignedJsonWebToken;
import com.objectcomputing.geoai.security.token.jwt.signature.DefaultJsonWebTokenSignatureSigner;
import com.objectcomputing.geoai.security.token.jwt.signature.DefaultJsonWebTokenSignatureVerifier;
import com.objectcomputing.geoai.security.token.jwt.signature.SignedJsonWebTokenSignatureGenerator;
import com.objectcomputing.geoai.security.token.jwt.signature.SignedJsonWebTokenSignatureVerifier;
import com.objectcomputing.geoai.security.token.signature.SignatureConfig;
import com.objectcomputing.geoai.security.token.signature.SignatureException;
import io.micronaut.context.annotation.EachBean;
import jakarta.inject.Singleton;

import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

@EachBean(SecretSignatureConfiguration.class)
@Singleton
public class SecretSignature implements SignatureConfig, SignedJsonWebTokenSignatureGenerator, SignedJsonWebTokenSignatureVerifier {

    private static final String DEFAULT_ALGORITHM = "HS256";

    private final byte[] secret;

    private String algorithm;
    private String type = "JWT";
    private boolean base64Enabled = true;

    public SecretSignature(SecretSignatureConfiguration config) {
        Objects.requireNonNull(config.getSecret(), "The secret for the secret signature cannot be null");
        this.secret = config.isBase64() ? Base64.getDecoder().decode(config.getSecret()) : config.getSecret().getBytes(UTF_8);
        this.algorithm = Optional.ofNullable(config.getAlgorithm()).orElse(DEFAULT_ALGORITHM);
    }

    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public SignedJsonWebToken sign(JsonWebTokenPayload payload) throws SignatureException {
        final DefaultJsonWebTokenSignatureSigner signer = new DefaultJsonWebTokenSignatureSigner(secret);
        SignedJsonWebToken token = new SignedJsonWebToken(buildDefaultHeader(), payload);
        token.sign(signer);
        return token;
    }

    private JsonWebTokenHeader buildDefaultHeader() {
        return JsonWebTokenHeader.build(algorithm).withType(type).withBase64URLEncoding(base64Enabled).build();
    }

    @Override
    public boolean verify(SignedJsonWebToken token) {
        final DefaultJsonWebTokenSignatureVerifier verifier = new DefaultJsonWebTokenSignatureVerifier(secret);
        return token.verify(verifier);
    }
}

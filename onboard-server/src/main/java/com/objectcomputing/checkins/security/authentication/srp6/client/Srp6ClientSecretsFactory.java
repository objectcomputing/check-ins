package com.objectcomputing.checkins.security.authentication.srp6.client;

import com.nimbusds.srp6.SRP6CryptoParams;
import com.nimbusds.srp6.SRP6VerifierGenerator;
import com.objectcomputing.checkins.security.authentication.srp6.Srp6Secrets;
import com.objectcomputing.checkins.security.authentication.srp6.config.Srp6EncryptionConfiguration;
import io.micronaut.context.annotation.Factory;

import java.nio.charset.StandardCharsets;

import static com.nimbusds.srp6.BigIntegerUtils.bigIntegerFromBytes;
import static com.nimbusds.srp6.BigIntegerUtils.toHex;

@Factory
public class Srp6ClientSecretsFactory {

    private final Srp6EncryptionConfiguration srp6EncryptionConfiguration;

    public Srp6ClientSecretsFactory(Srp6EncryptionConfiguration srp6EncryptionConfiguration) {
        this.srp6EncryptionConfiguration = srp6EncryptionConfiguration;
    }

    public Srp6Secrets generateSecrets(String identity, String secret) {
        //Setup Config
        SRP6CryptoParams config = SRP6CryptoParams.getInstance(
                srp6EncryptionConfiguration.getBitSize(), srp6EncryptionConfiguration.getAlgorithm());

        SRP6VerifierGenerator verifierGenerator = new SRP6VerifierGenerator(config);

        final byte[] saltBytes = verifierGenerator.generateRandomSalt(16);

        byte[] identityBytes = identity.getBytes(StandardCharsets.UTF_8);
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

        final String verifier = toHex(verifierGenerator.generateVerifier(saltBytes, identityBytes, secretBytes));

        return new Srp6Secrets(toHex(bigIntegerFromBytes(saltBytes)), verifier);
    }
}

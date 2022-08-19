package com.objectcomputing.checkins.services.onboardeecreate.security.authentication.srp6.server;

import com.nimbusds.srp6.BigIntegerUtils;
import com.nimbusds.srp6.SRP6CryptoParams;
import com.nimbusds.srp6.SRP6ServerSession;
import com.objectcomputing.checkins.services.onboardeecreate.security.authentication.srp6.Srp6Challenge;
import com.objectcomputing.checkins.services.onboardeecreate.security.authentication.srp6.Srp6Credentials;
import com.objectcomputing.checkins.services.onboardeecreate.security.authentication.srp6.config.Srp6EncryptionConfiguration;
import io.micronaut.context.annotation.Factory;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.nimbusds.srp6.BigIntegerUtils.fromHex;
import static com.nimbusds.srp6.BigIntegerUtils.toHex;

@Factory
public class Srp6AuthenticationChallengeFactory {
    private final Srp6EncryptionConfiguration srp6EncryptionConfiguration;

    public Srp6AuthenticationChallengeFactory(Srp6EncryptionConfiguration srp6EncryptionConfiguration) {
        this.srp6EncryptionConfiguration = srp6EncryptionConfiguration;
    }

    public Srp6Challenge create(SRP6ServerSession session, Srp6Credentials credentials) {
        BigInteger B = session.step1(credentials.getEmailAddress(), fromHex(credentials.getSalt()), fromHex(credentials.getVerifier()));

        if(B != null) {
            return new Srp6Challenge(credentials.getSalt(), toHex(B));
        }

        return null;
    }

    public Srp6Challenge createFake(String identity) {
        //Setup Config
        SRP6CryptoParams config = SRP6CryptoParams.getInstance(512, "SHA-256");

        final SRP6ServerSession session = new SRP6ServerSession(config);

        final String fakeSalt = generateFakeSalt(identity);

        final BigInteger B = session.step1(identity, fromHex(fakeSalt), new BigInteger("0"));

        return new Srp6Challenge(fakeSalt, toHex(B));
    }

    private String generateFakeSalt(String identity) {
        MessageDigest md = createMessageDigest();
        byte[] output = md.digest(identity.getBytes(StandardCharsets.UTF_8));
        return toHex(BigIntegerUtils.bigIntegerFromBytes(output));
    }

    private MessageDigest createMessageDigest() {
        try {
            return MessageDigest.getInstance(srp6EncryptionConfiguration.getAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

}

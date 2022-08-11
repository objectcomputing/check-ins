package com.objectcomputing.checkins.services.onboardeecreate.security.authentication.srp6.client;

import com.nimbusds.srp6.SRP6ClientCredentials;
import com.nimbusds.srp6.SRP6ClientSession;
import com.nimbusds.srp6.SRP6CryptoParams;
import com.nimbusds.srp6.SRP6Exception;
import com.objectcomputing.checkins.services.onboardeecreate.security.authentication.srp6.Srp6Challenge;
import com.objectcomputing.checkins.services.onboardeecreate.security.authentication.srp6.config.Srp6EncryptionConfiguration;
import io.micronaut.context.annotation.Factory;

import java.util.Optional;

import static com.nimbusds.srp6.BigIntegerUtils.fromHex;

@Factory
public class Srp6ClientCredentialsFactory {

    private final Srp6EncryptionConfiguration srp6EncryptionConfiguration;

    public Srp6ClientCredentialsFactory(Srp6EncryptionConfiguration srp6EncryptionConfiguration) {
        this.srp6EncryptionConfiguration = srp6EncryptionConfiguration;
    }

    public Optional<SRP6ClientCredentials> generateSrp6ClientCredentials(String identity, String secret, Srp6Challenge c)  {
        //Setup Config
        SRP6CryptoParams config = SRP6CryptoParams.getInstance(
                srp6EncryptionConfiguration.getBitSize(), srp6EncryptionConfiguration.getAlgorithm());

        // TODO: 3/9/2022 Get timeout from configuration srp6EncryptionConfiguration.getSessionTimeout()
        SRP6ClientSession client = new SRP6ClientSession();

        //Client Step1
        client.step1(identity, secret);

        //Client Step2
        try {
            return Optional.of(client.step2(config, fromHex(c.getSalt()), fromHex(c.getB())));
        } catch (SRP6Exception e) {
            return Optional.empty();
        }
    }

}

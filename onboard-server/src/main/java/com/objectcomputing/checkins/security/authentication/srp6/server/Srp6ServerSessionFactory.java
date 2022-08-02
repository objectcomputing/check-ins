package com.objectcomputing.checkins.security.authentication.srp6.server;

import com.nimbusds.srp6.SRP6CryptoParams;
import com.nimbusds.srp6.SRP6ServerSession;
import com.objectcomputing.geoai.security.authentication.srp6.config.Srp6EncryptionConfiguration;
import io.micronaut.context.annotation.Factory;

@Factory
public class Srp6ServerSessionFactory {

    private final Srp6EncryptionConfiguration srp6EncryptionConfiguration;

    public Srp6ServerSessionFactory(Srp6EncryptionConfiguration srp6EncryptionConfiguration) {
        this.srp6EncryptionConfiguration = srp6EncryptionConfiguration;
    }

    public SRP6ServerSession create() {
        //Setup Config
        SRP6CryptoParams config = SRP6CryptoParams.getInstance(
                srp6EncryptionConfiguration.getBitSize(), srp6EncryptionConfiguration.getAlgorithm());

        return new SRP6ServerSession(config);
    }
}

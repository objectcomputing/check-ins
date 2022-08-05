package com.objectcomputing.checkins.security.authentication.token.jwt.signature;

import com.objectcomputing.checkins.security.authentication.token.TokenSignature;
import io.micronaut.core.order.Ordered;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface SignatureSigner<T extends SignatureConfig, R extends TokenSignature> extends Ordered {
    R sign(T config, byte[] signingInput) throws NoSuchAlgorithmException, InvalidKeyException;
}

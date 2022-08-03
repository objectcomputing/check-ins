package com.objectcomputing.checkins.security.authentication.token.jwt.signature;

import com.nimbusds.jose.crypto.utils.ConstantTimeUtils;
import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenHeader;
import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenSignature;
import com.objectcomputing.geoai.security.token.signature.SignatureException;
import com.objectcomputing.geoai.security.token.signature.SignatureVerifier;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class DefaultJsonWebTokenSignatureVerifier implements SignatureVerifier<JsonWebTokenHeader, JsonWebTokenSignature> {

    private final byte[] secret;

    public DefaultJsonWebTokenSignatureVerifier(byte[] secret) {
        this.secret = secret;
    }

    @Override
    public boolean verify(JsonWebTokenHeader config, byte[] signingInput, JsonWebTokenSignature signature) {
        try {
            Mac algorithm = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret, "HmacSHA256");
            algorithm.init(secretKey);

            byte[] signedBytes = algorithm.doFinal(signingInput);

            return ConstantTimeUtils.areEqual(signedBytes, signature.toBase64Text().decode());
        } catch(Throwable cause) {
            throw new SignatureException("cannot verify signature", cause);
        }
    }
}

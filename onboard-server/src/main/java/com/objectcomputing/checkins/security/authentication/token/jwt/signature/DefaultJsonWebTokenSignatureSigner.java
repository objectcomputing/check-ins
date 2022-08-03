package com.objectcomputing.checkins.security.authentication.token.jwt.signature;

import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenHeader;
import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenSignature;
import com.objectcomputing.geoai.security.token.signature.SignatureException;
import com.objectcomputing.geoai.security.token.signature.SignatureSigner;
import com.objectcomputing.geoai.security.util.Base64Text;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class DefaultJsonWebTokenSignatureSigner implements SignatureSigner<JsonWebTokenHeader, JsonWebTokenSignature> {

    private final byte[] secret;

    public DefaultJsonWebTokenSignatureSigner(byte[] secret) {
        this.secret = secret;
    }

    @Override
    public JsonWebTokenSignature sign(JsonWebTokenHeader config, byte[] signingInput) {
        try {
            Mac algorithm = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret, "HmacSHA256");
            algorithm.init(secretKey);

            byte[] signedBytes = algorithm.doFinal(signingInput);

            return new JsonWebTokenSignature(Base64Text.encode(signedBytes));
        } catch(Throwable cause) {
            throw new SignatureException("cannot create the signature", cause);
        }
    }
}

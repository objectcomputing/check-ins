package com.objectcomputing.checkins.security.authentication.token.jwt;

import com.nimbusds.jose.util.StandardCharset;
import com.objectcomputing.checkins.security.authentication.token.jwt.signature.SignatureSigner;
import com.objectcomputing.checkins.security.authentication.token.jwt.signature.SignatureVerifier;
import com.objectcomputing.checkins.security.authentication.token.util.Base64Text;

import javax.validation.constraints.NotNull;
import java.text.ParseException;

public class SignedJsonWebToken extends JsonWebToken {
    private final String signingInput;

    public SignedJsonWebToken(@NotNull JsonWebTokenHeader header, @NotNull JsonWebTokenPayload payload, @NotNull JsonWebTokenSignature signature) {
        super(header, payload, signature);

        signingInput = composeSignedInputs();
    }

    public SignedJsonWebToken(@NotNull JsonWebTokenHeader header, @NotNull JsonWebTokenPayload payload) {
        this(header, payload, null);
    }

    public byte[] getSigningInput() {
        return signingInput.getBytes(StandardCharset.UTF_8);
    }

    private String composeSignedInputs() {
        if (header.isBase64URLEncoded()) {
            return getHeader().toBase64Text().toString() + '.' + getPayload().toBase64Text().toString();
        } else {
            return getHeader().toBase64Text().toString() + '.' + getPayload().toString();
        }
    }

    public boolean sign(SignatureSigner<JsonWebTokenHeader, JsonWebTokenSignature> signer) {
        boolean result = false;
        try {
            signature = signer.sign(getHeader(), getSigningInput());
            state.set(JsonWebTokenState.Signed);
            result = true;
        } catch(Throwable ignore) {
            state.set(JsonWebTokenState.Unsigned);
        }
        return result;
    }

    public boolean verify(SignatureVerifier<JsonWebTokenHeader, JsonWebTokenSignature> verifier) {
        boolean verified = false;
        try {
            verified = verifier.verify(getHeader(), getSigningInput(), getSignature());
        } catch(Throwable ignore) {
        }

        if(verified) {
            state.set(JsonWebTokenState.Verified);
        }

        return verified;
    }

    @Override
    public String serialize() {
        if(!canSerialize()) {
            throw new IllegalStateException("token was not signed or verified first");
        }
        return super.serialize();
    }

    private boolean canSerialize() {
        return state.get() == JsonWebTokenState.Signed || state.get() == JsonWebTokenState.Verified;
    }

    public static SignedJsonWebToken parse(String jsonWebTokenText) throws ParseException {
        Base64Text[] parts = JsonWebTokenPart.split(jsonWebTokenText);

        if(parts.length != 3) {
            throw new ParseException("Unexpected number of Base64URL parts, must be three", 0);
        }

        return new SignedJsonWebToken(
                JsonWebTokenHeader.parse(parts[0]),
                JsonWebTokenPayload.parse(parts[1]),
                JsonWebTokenSignature.parse(parts[2]));
    }
}

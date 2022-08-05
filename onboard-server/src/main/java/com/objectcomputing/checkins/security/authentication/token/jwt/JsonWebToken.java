package com.objectcomputing.checkins.security.authentication.token.jwt;

import com.objectcomputing.geoai.security.token.TokenRoot;
import io.micronaut.core.annotation.Nullable;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public abstract class JsonWebToken extends JsonWebTokenObject implements TokenRoot, Serializable {
    protected final AtomicReference<JsonWebTokenState> state = new AtomicReference<>();

    protected final JsonWebTokenHeader header;
    protected final JsonWebTokenPayload payload;
    protected JsonWebTokenSignature signature;

    public JsonWebToken(@NotNull JsonWebTokenHeader header, @NotNull JsonWebTokenPayload payload, @Nullable JsonWebTokenSignature signature) {
        this.header = header;
        this.payload = payload;
        this.signature = signature;

        JsonWebTokenState tmp = JsonWebTokenState.Unsigned;
        if (null != signature && signature.hasParsedPart()) {
            tmp = JsonWebTokenState.Signed;
        }
        state.set(tmp);
    }

    public boolean isSigned() {
        return state.get() == JsonWebTokenState.Signed;
    }

    public boolean isVerified() {
        return state.get() == JsonWebTokenState.Verified;
    }

    public JsonWebTokenHeader getHeader() {
        return header;
    }

    public JsonWebTokenPayload getPayload() {
        return payload;
    }

    public JsonWebTokenSignature getSignature() {
        return signature;
    }

    public boolean hasExpired() {
        return Instant.now().isAfter(Instant.ofEpochMilli(getPayload().getExpirationTime()));
    }

    public boolean isAvailable() {
        return null == getPayload().getNotBeforeTime() || Instant.now().isAfter(Instant.ofEpochMilli(getPayload().getNotBeforeTime()));
    }

    public String serialize() {
        return String.join(".",
                getHeader().toBase64Text().toString(),
                getPayload().toBase64Text().toString(),
                null == getSignature() ? "" : getSignature().toBase64Text().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonWebToken that = (JsonWebToken) o;
        return header.equals(that.header) && payload.equals(that.payload) && Objects.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, payload, signature);
    }

    public JsonWebTokenClaims getClaims() {
        return getPayload();
    }
}

package com.objectcomputing.checkins.security.authentication.token.jwt;

import com.objectcomputing.checkins.security.authentication.token.TokenSignature;
import com.objectcomputing.checkins.security.authentication.token.util.Base64Text;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class JsonWebTokenSignature extends JsonWebTokenPart implements TokenSignature {

    public JsonWebTokenSignature(@NotNull Base64Text parsedPart) {
        super(parsedPart);
    }

    @Override
    public Base64Text toBase64Text() {
        return getParsedPart();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonWebTokenSignature that = (JsonWebTokenSignature) o;
        return Objects.equals(getParsedPart(), that.getParsedPart());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParsedPart());
    }

    @Override
    public String toString() {
        return getParsedPart().toString();
    }


    public static JsonWebTokenSignature parse(Base64Text parsedPart) {
        return new JsonWebTokenSignature(parsedPart);
    }
}

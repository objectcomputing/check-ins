package com.objectcomputing.checkins.security.authentication.token.jwt;

import com.objectcomputing.geoai.security.util.Base64Text;

import javax.validation.constraints.NotNull;
import java.text.ParseException;

public class SimpleJsonWebToken extends JsonWebToken {

    public SimpleJsonWebToken(@NotNull JsonWebTokenHeader header, @NotNull JsonWebTokenPayload payload, JsonWebTokenSignature signature) {
        super(header, payload, signature);
    }

    @Override
    public String serialize() {
        return null;
    }

    public static JsonWebToken parse(String jsonWebTokenText) throws ParseException {
        Base64Text[] parts = JsonWebTokenPart.split(jsonWebTokenText);

        if(parts.length != 3) {
            throw new ParseException("Unexpected number of Base64URL parts, must be three", 0);
        }

        return new SimpleJsonWebToken(
                JsonWebTokenHeader.parse(parts[0]),
                JsonWebTokenPayload.parse(parts[1]),
                JsonWebTokenSignature.parse(parts[2]));
    }
}

package com.objectcomputing.checkins.security.authentication.token.jwt;

import com.google.gson.Gson;
import com.objectcomputing.checkins.security.authentication.token.util.Base64Text;

import java.text.ParseException;
import java.util.Map;
import java.util.stream.Stream;

public abstract class JsonWebTokenPart extends JsonWebTokenObject {
    protected static final Gson GSON = new Gson();

    private final Base64Text parsedPart;

    public JsonWebTokenPart(Base64Text parsedPart) {
        this.parsedPart = parsedPart;
    }

    public Base64Text getParsedPart() {
        return parsedPart;
    }

    public static Map<String, Object> parse(String jsonText) throws ParseException {
        try {
            return GSON.fromJson(jsonText, Map.class);
        } catch(Throwable cause) {
            throw new ParseException("malformed json text", 0);
        }
    }

    public static Base64Text[] split(String jsonWebTokenText) throws ParseException {
        String[] parts = jsonWebTokenText.split("\\.");

        switch(parts.length) {
            case 1: throw new ParseException("Invalid serialized unsecured/JWS/JWE object: Missing part delimiters", 0);
            case 2: throw new ParseException("Invalid serialized unsecured/JWS/JWE object: Missing second delimiter", 0);
            case 3: break; //OK
            case 4: throw new ParseException("Invalid serialized JWE object: Missing fourth delimiter", 0);
            case 5: break; //OK
            default: new ParseException("Invalid serialized unsecured/JWS/JWE object: Too many parts delimiters", 0);
        }

        return Stream.of(parts).map(Base64Text::fromUrlSafeText).toList().toArray(new Base64Text[0]);
    }

    abstract public Base64Text toBase64Text();

    public boolean hasParsedPart() {
        return null != parsedPart;
    }
}

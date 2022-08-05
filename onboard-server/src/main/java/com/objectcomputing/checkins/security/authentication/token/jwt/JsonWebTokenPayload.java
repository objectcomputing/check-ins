package com.objectcomputing.checkins.security.authentication.token.jwt;

import com.nimbusds.jwt.JWTClaimNames;
import com.objectcomputing.checkins.security.authentication.token.util.InterpretableObjectMap;
import com.objectcomputing.checkins.security.authentication.token.util.JsonMapProvider;
import com.objectcomputing.checkins.security.authentication.token.util.TypeUtilities;
import com.objectcomputing.checkins.security.authentication.token.TokenPayload;
import com.objectcomputing.checkins.security.authentication.token.util.Base64Text;

import java.text.ParseException;
import java.util.*;

public class JsonWebTokenPayload extends JsonWebTokenPart implements JsonMapProvider, TokenPayload, JsonWebTokenClaims {
    public static final Set<String> RESERVED_CLAIMS = Set.of(
            JWTClaimNames.ISSUER, JWTClaimNames.SUBJECT, JWTClaimNames.AUDIENCE,
            JWTClaimNames.EXPIRATION_TIME, JWTClaimNames.NOT_BEFORE, JWTClaimNames.ISSUED_AT, JWTClaimNames.JWT_ID);

    private final String iss;
    private final String sub;
    private final String aud;
    private final long exp;
    private final long iat;
    private Long nbf;
    private String jti;
    private final Map<String, Object> publicClaims;

    public JsonWebTokenPayload(String iss, String sub, String aud, long exp, Long nbf, long iat, String jti,
                               Map<String, Object> publicClaims, Base64Text parsedPart) {

        super(parsedPart);

        if (iss == null) {
            throw new IllegalArgumentException("The issuer \"iss\" parameter must not be null");
        }

        this.iss = iss;

        if (sub == null) {
            throw new IllegalArgumentException("The subject \"sub\" parameter must not be null");
        }

        this.sub = sub;

        if (aud == null) {
            throw new IllegalArgumentException("The audience \"aud\" parameter must not be null");
        }

        this.aud = aud;

        if (iat < 0) {
            throw new IllegalArgumentException("The issued time \"iat\" parameter must greater than or equal to zero");
        }

        this.iat = iat;

        this.exp = exp;
        this.jti = jti;

        if (null != publicClaims) {
            this.publicClaims = Map.copyOf(publicClaims);
        } else {
            this.publicClaims = new HashMap<>();
        }

    }

    public JsonWebTokenPayload(String iss, String sub, String aud, long exp, long iat, Base64Text parsedPart) {
        this(iss, sub, aud, exp, null, iat, null, null, parsedPart);
    }

    public String getIssuer() {
        return iss;
    }

    public String getSubject() {
        return sub;
    }

    public String getAudience() {
        return aud;
    }

    public Long getExpirationTime() {
        return exp;
    }

    public Map<String, Object> getPublicClaims() {
        return Collections.unmodifiableMap(publicClaims);
    }

    public Optional<Object> getPublicClaim(String claim) {
        return Optional.ofNullable(publicClaims.get(claim));
    }

    @Override
    public <T> Optional<T> getPublicClaim(String claim, Class<T> type) {
        return TypeUtilities.asType(publicClaims.get(claim), type);
    }

    public void setPublicClaim(String claim, Object value) {
        if (RESERVED_CLAIMS.contains(claim)) {
            throw new IllegalArgumentException("public claim cannot override a private claim");
        }
        publicClaims.put(claim, value);
    }

    public Long getIssuedTime() {
        return iat;
    }

    public Long getNotBeforeTime() {
        return nbf;
    }

    public void setNotBeforeTime(long notBeforeTime) {
        this.nbf = notBeforeTime;
    }

    public String getIdentifier() {
        return jti;
    }

    public void setIdentifier(String identifier) {
        this.jti = identifier;
    }

    @Override
    public Map<String, Object> toJsonMap() {
        Map<String, Object> claims = new HashMap<>(publicClaims);
        claims.put(JWTClaimNames.ISSUER, iss);
        claims.put(JWTClaimNames.SUBJECT, sub);
        claims.put(JWTClaimNames.AUDIENCE, aud);
        claims.put(JWTClaimNames.EXPIRATION_TIME, exp);

        if (null != nbf) {
            claims.put(JWTClaimNames.NOT_BEFORE, nbf);
        }

        claims.put(JWTClaimNames.ISSUED_AT, iat);

        if (null != jti && !"".equals(jti)) {
            claims.put(JWTClaimNames.JWT_ID, jti);
        }

        return claims;
    }

    @Override
    public Base64Text toBase64Text() {
        if(null != getParsedPart()) {
            return getParsedPart();
        }

        return Base64Text.encode(GSON.toJson(toJsonMap()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonWebTokenPayload that = (JsonWebTokenPayload) o;
        return exp == that.exp
                && iat == that.iat
                && iss.equals(that.iss)
                && sub.equals(that.sub)
                && aud.equals(that.aud)
                && Objects.equals(nbf, that.nbf)
                && Objects.equals(jti, that.jti)
                && publicClaims.equals(that.publicClaims);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iss, sub, aud, exp, nbf, iat, jti, publicClaims);
    }

    @Override
    public String toString() {
        if(null != getParsedPart()) {
            return getParsedPart().decodeToString();
        }
        return GSON.toJson(toJsonMap());
    }

    public static JsonWebTokenPayload parse(Base64Text parsedPart) throws ParseException {
        return parse(parsedPart.decodeToString(), parsedPart);
    }

    public static JsonWebTokenPayload parse(String jsonText, Base64Text parsedPart) throws ParseException {
        return parse(parse(jsonText), parsedPart);
    }

    public static JsonWebTokenPayload parse(Map<String, Object> claims, Base64Text parsedPart) {
        InterpretableObjectMap parsableClaims = new InterpretableObjectMap(claims);
        Builder builder = build(parsableClaims.getAsString(JWTClaimNames.ISSUER)).withParsedPart(parsedPart);

        for(String claim : claims.keySet()) {
            if(claim.equals(JWTClaimNames.SUBJECT)) {
                builder = builder.withSubject(parsableClaims.getAsString(claim));
            } else if(claim.equals(JWTClaimNames.AUDIENCE)) {
                builder = builder.withAudience(parsableClaims.getAsString(claim));
            } else if(claim.equals(JWTClaimNames.EXPIRATION_TIME)) {
                builder = builder.withExpirationTime(parsableClaims.getAsLong(claim));
            } else if(claim.equals(JWTClaimNames.NOT_BEFORE)) {
                builder = builder.withNotBeforeTime(parsableClaims.getAsLong(claim));
            } else if(claim.equals(JWTClaimNames.ISSUED_AT)) {
                builder = builder.withIssuedAt(parsableClaims.getAsLong(claim));
            } else if(claim.equals(JWTClaimNames.JWT_ID)) {
                builder = builder.withIdentifier(parsableClaims.getAsString(claim));
            } else if(!RESERVED_CLAIMS.contains(claim)){
                builder = builder.withPublicClaim(claim, claims.get(claim));
            }
        }
        return builder.build();
    }

    public static Builder build(String iss) {
        return new Builder(iss);
    }

    public static final class Builder {
        private String iss;
        private String sub;
        private String aud;
        private long exp;
        private long iat;
        private Long nbf;
        private String jti;
        private Map<String, Object> publicClaims;
        private Base64Text parsedPart;

        public Builder(String iss) {
            this.iss = iss;
        }

        public Builder withParsedPart(Base64Text parsedPart) {
            this.parsedPart = parsedPart;
            return this;
        }

        public Builder withSubject(String sub) {
            this.sub = sub;
            return this;
        }

        public Builder withAudience(String aud) {
            this.aud = aud;
            return this;
        }

        public Builder withExpirationTime(Long exp) {
            this.exp = exp;
            return this;
        }

        public Builder withNotBeforeTime(Long nbf) {
            this.nbf = nbf;
            return this;
        }

        public Builder withIssuedAt(Long iat) {
            this.iat = iat;
            return this;
        }

        public Builder withIdentifier(String jti) {
            this.jti = jti;
            return this;
        }

        public Builder withPublicClaims(Map<String, Object> publicClaims) {
            this.publicClaims = Map.copyOf(publicClaims);
            return this;
        }

        public Builder withPublicClaim(String claim, Object value) {
            if(null == publicClaims) {
                publicClaims = new HashMap<>();
            }
            publicClaims.put(claim, value);
            return this;
        }

        public JsonWebTokenPayload build() {
            return new JsonWebTokenPayload(iss, sub, aud, exp, nbf, iat, jti, publicClaims, parsedPart);
        }
    }
}

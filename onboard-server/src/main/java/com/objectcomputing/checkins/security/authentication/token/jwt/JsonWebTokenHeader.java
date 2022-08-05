package com.objectcomputing.checkins.security.authentication.token.jwt;

import com.nimbusds.jose.HeaderParameterNames;
import com.objectcomputing.geoai.core.util.InterpretableObjectMap;
import com.objectcomputing.geoai.core.util.JsonMapProvider;
import com.objectcomputing.geoai.security.token.signature.SignatureConfig;
import com.objectcomputing.geoai.security.util.Base64Text;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.*;

public class JsonWebTokenHeader extends JsonWebTokenPart implements JsonMapProvider, SignatureConfig {

    private static final List<String> RESERVED_HEADERS = List.of(
            HeaderParameterNames.ALGORITHM, HeaderParameterNames.TYPE, HeaderParameterNames.CONTENT_TYPE,
            HeaderParameterNames.BASE64_URL_ENCODE_PAYLOAD, HeaderParameterNames.CRITICAL);

    private static final Map<String, Object> EMPTY_CUSTOM_PARAMS = Collections.unmodifiableMap(new HashMap<>());

    private final String alg;
    private final String typ;
    private final String cty;
    private final Boolean b64;
    private final Set<String> crit;
    private final Map<String, Object> custom;

    public JsonWebTokenHeader(@NotNull String alg, @NotNull String typ, String cty, Boolean b64,
                              Set<String> crit, Map<String,Object> custom, Base64Text parsedPart) {
        super(parsedPart);

        if (alg == null) {
            throw new IllegalArgumentException("The algorithm \"alg\" header parameter must not be null");
        }

        this.alg = alg;
        this.typ = typ;
        this.cty = cty;
        this.b64 = b64;

        if (crit != null) {
            // Copy and make unmodifiable
            this.crit = Collections.unmodifiableSet(Set.copyOf(crit));
        } else {
            this.crit = null;
        }

        if (custom != null) {
            // Copy and make unmodifiable
            this.custom = Collections.unmodifiableMap(Map.copyOf(custom));
        } else {
            this.custom = EMPTY_CUSTOM_PARAMS;
        }
    }

    public String getAlgorithm() {
        return alg;
    }

    public String getType() {
        return typ;
    }

    public String getContentType() {
        return cty;
    }

    public boolean isBase64URLEncoded() {
        return Boolean.TRUE.equals(b64);
    }

    public Set<String> getCriticalAttributes() {
        return crit;
    }

    public Object getCustomAttribute(String name) {
        return custom.get(name);
    }

    public Map<String,Object> getCustomAttributes() {
        return custom;
    }

    public Set<String> getIncludedAttributes() {
        Set<String> headers =
                new HashSet<>(getCustomAttributes().keySet());

        headers.add(HeaderParameterNames.ALGORITHM);

        if (typ != null) {
            headers.add(HeaderParameterNames.TYPE);
        }

        if (null != b64) {
            headers.add(HeaderParameterNames.BASE64_URL_ENCODE_PAYLOAD);
        }


        if (cty != null) {
            headers.add(HeaderParameterNames.CONTENT_TYPE);
        }

        if (crit != null && !crit.isEmpty()) {
            headers.add(HeaderParameterNames.CRITICAL);
        }

        return headers;
    }

    @Override
    public Map<String, Object> toJsonMap() {
        Map<String, Object> headers = new HashMap<>(custom);

        // Alg is always defined
        headers.put(HeaderParameterNames.ALGORITHM, alg);

        if (typ != null) {
            headers.put(HeaderParameterNames.TYPE, typ);
        }

        if (cty != null) {
            headers.put(HeaderParameterNames.CONTENT_TYPE, cty);
        }

        if (b64 != null) {
            headers.put(HeaderParameterNames.BASE64_URL_ENCODE_PAYLOAD, b64);
        }

        if (crit != null && ! crit.isEmpty()) {
            headers.put(HeaderParameterNames.CRITICAL, List.copyOf(crit));
        }

        return headers;
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
        JsonWebTokenHeader that = (JsonWebTokenHeader) o;
        return alg.equals(that.alg)
                && typ.equals(that.typ)
                && Objects.equals(cty, that.cty)
                && Objects.equals(crit, that.crit)
                && Objects.equals(custom, that.custom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alg, typ, cty, crit, custom);
    }

    @Override
    public String toString() {
        if(null != getParsedPart()) {
            return getParsedPart().decodeToString();
        }
        return GSON.toJson(toJsonMap());
    }

    public static JsonWebTokenHeader parse(Base64Text parsedPart) throws ParseException {
        return parse(parsedPart.decodeToString(), parsedPart);
    }

    public static JsonWebTokenHeader parse(String jsonText, Base64Text parsedPart) throws ParseException {
        return parse(parse(jsonText), parsedPart);
    }

    public static JsonWebTokenHeader parse(Map<String, Object> headers, Base64Text parsedPart) {
        InterpretableObjectMap parsableHeaders = new InterpretableObjectMap(headers);
        Builder builder = build(parsableHeaders.getAsString(HeaderParameterNames.ALGORITHM)).withParsedPart(parsedPart);

        for(String header : headers.keySet()) {
            if(HeaderParameterNames.TYPE.equals(header)) {
                builder = builder.withType(parsableHeaders.getAsString(header));
            } else if(HeaderParameterNames.CONTENT_TYPE.equals(header)) {
                builder = builder.withContentType(parsableHeaders.getAsString(header));
            } else if(HeaderParameterNames.BASE64_URL_ENCODE_PAYLOAD.equals(header)) {
                builder = builder.withBase64URLEncoding(parsableHeaders.getAsBoolean(header));
            } else if(HeaderParameterNames.CRITICAL.equals((header))) {
                builder = builder.withCriticalAttributes(parsableHeaders.getAsStringArray(header));
            } else if(!RESERVED_HEADERS.contains(header)){
                builder = builder.withCustomAttribute(header, headers.get(header));
            }
        }
        return builder.build();
    }

    public static Builder build(String alg) {
        return new Builder(alg);
    }

    public static final class Builder {
        private final String alg;
        private String typ;
        private String cty;
        private boolean b64;
        private Set<String> crit;
        private Map<String, Object> custom;
        private Base64Text parsedPart;

        public Builder(String alg) {
            if(null == alg) {
                throw new IllegalArgumentException("The algorithm \"alg\" header parameter must not be null");
            }
            this.alg = alg;
        }

        public Builder withParsedPart(Base64Text parsedPart) {
            this.parsedPart = parsedPart;
            return this;
        }

        public Builder withType(String typ) {
            this.typ = typ;
            return this;
        }

        public Builder withContentType(String cty) {
            this.cty = cty;
            return this;
        }

        public Builder withBase64URLEncoding(Boolean b64) {
            this.b64 = b64;
            return this;
        }

        public JsonWebTokenHeader build() {
            return new JsonWebTokenHeader(alg, typ, cty, b64, crit, custom, parsedPart);
        }

        public Builder withCriticalAttributes(String... criticalAttributes) {
            if(null != criticalAttributes) {
                crit = Set.of(criticalAttributes);
            }
            return this;
        }

        public Builder withCustomAttribute(String header, Object value) {
            if(null == custom) {
                this.custom = new HashMap<>();
            }
            custom.put(header, value);
            return this;
        }
    }
}

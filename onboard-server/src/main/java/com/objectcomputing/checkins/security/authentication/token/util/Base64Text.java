package com.objectcomputing.checkins.security.authentication.token.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

public class Base64Text {
    private final String value;
    private final boolean urlSafe;

    protected Base64Text(String value, boolean urlSafe) {
        this.value = value;
        this.urlSafe = urlSafe;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Base64Text that = (Base64Text) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }

    public static Base64Text encode(BigInteger bigInteger) {
        return encode(bigIntegerToBytes(bigInteger));
    }

    private static byte[] bigIntegerToBytes(BigInteger bigInteger) {
        assert(bigInteger.signum() != -1); // NOSONAR
        byte[] bytes = bigInteger.toByteArray();
        if (bytes[0] == 0) {
            return Arrays.copyOfRange(bytes, 1, bytes.length);
        }
        return bytes;
    }

    public static Base64Text encode(String text) {
        return encode(text.getBytes(StandardCharsets.UTF_8));
    }

    public static Base64Text encode(byte[] bytes) {
        return new Base64Text(Base64.getUrlEncoder().encodeToString(bytes), true);
    }

    public byte[] decode() {
        if(urlSafe) {
            return Base64.getUrlDecoder().decode(getValue());
        } else {
            return Base64.getDecoder().decode(getValue());
        }
    }

    public String decodeToString() {
        return new String(decode(), StandardCharsets.UTF_8);
    }

    public BigInteger decodeToBigInteger() {
        return new BigInteger(1, decode());
    }

    public static Base64Text fromUrlSafeText(String urlSafeBase64Text) {
        return new Base64Text(urlSafeBase64Text, true);
    }
}

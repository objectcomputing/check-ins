package com.objectcomputing.checkins.services.slack;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.configuration.CheckInsConfiguration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.time.Instant;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class SlackSignature {
    @Inject
    private CheckInsConfiguration configuration;

    public SlackSignature() {}

    public boolean verifyRequest(String slackSignature, String timestamp, String requestBody) {
        try {
            // Prevent replay attacks by checking the timestamp
            if (!isRecentRequest(timestamp)) {
                return false;
            }

            // Create the base string
            String baseString = "v0:" + timestamp + ":" + requestBody;

            // Generate HMAC-SHA256 signature
            String secret = configuration.getApplication()
                                         .getSlack().getSigningSecret();
            String computedSignature = "v0=" + hmacSha256(secret, baseString);

            // Compare the computed signature with Slack's signature
            return computedSignature.equals(slackSignature);
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isRecentRequest(String timestamp) {
        long currentTime = Instant.now().getEpochSecond();
        long requestTime = Long.parseLong(timestamp);
        return Math.abs(currentTime - requestTime) < 60;
    }

    private String hmacSha256(String secret, String message) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);

        // Convert hash to hex
        StringBuilder hexString = new StringBuilder();
        byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String generate(String timestamp, String rawBody) {
        String baseString = "v0:" + timestamp + ":" + rawBody;
        String secret = configuration.getApplication()
                                     .getSlack().getSigningSecret();

        try {
            // Generate HMAC SHA-256 signature
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec =
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                                  "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(
                              baseString.getBytes(StandardCharsets.UTF_8));

            // Convert hash to hex
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return "v0=" + hexString.toString();
        } catch (Exception ex) {
            throw new BadArgException(ex.toString());
        }
    }
}

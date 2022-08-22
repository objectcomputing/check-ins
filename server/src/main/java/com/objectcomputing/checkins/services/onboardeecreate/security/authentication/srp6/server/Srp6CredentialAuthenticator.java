package com.objectcomputing.checkins.services.onboardeecreate.security.authentication.srp6.server;

import com.nimbusds.srp6.SRP6ServerSession;

import static com.nimbusds.srp6.BigIntegerUtils.fromHex;
import static com.nimbusds.srp6.BigIntegerUtils.toHex;

/**
 * This class will execute the step two of the server authentication
 * it will accept the A from the client M1 from the client it will
 * return M2 which is a token generated after successful authentication
 * of the client
 *
 * Created by Andrew Montgomery on 12/27/2021
 */
public final class Srp6CredentialAuthenticator {
    private final SRP6ServerSession server;

    private static String M2 = null;

    public Srp6CredentialAuthenticator(SRP6ServerSession server) {
        this.server = server;
    }

    public boolean authenticate(String M1A) {
        // The concatenated string will be spliced
        // The format is M1:A
        String [] m1aArray = M1A.split(":");
        String M1 = m1aArray[0];
        String A = m1aArray[1];

        return authenticate(M1, A);
    }

    public boolean authenticate(String M1, String A) {
        try {
            this.M2 = toHex(getServer().step2(fromHex(A), fromHex(M1)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getM2() {
        return M2;
    }

    public boolean isAuthenticated() {
        return null != getM2();
    }

    public SRP6ServerSession getServer() {
        return server;
    }
}

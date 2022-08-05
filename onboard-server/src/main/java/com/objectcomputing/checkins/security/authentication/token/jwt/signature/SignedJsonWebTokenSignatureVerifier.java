package com.objectcomputing.checkins.security.authentication.token.jwt.signature;

import com.objectcomputing.checkins.security.authentication.token.jwt.SignedJsonWebToken;
import com.objectcomputing.geoai.security.token.signature.SignedTokenSignatureVerifier;

public interface SignedJsonWebTokenSignatureVerifier extends SignedTokenSignatureVerifier<SignedJsonWebToken> {
}

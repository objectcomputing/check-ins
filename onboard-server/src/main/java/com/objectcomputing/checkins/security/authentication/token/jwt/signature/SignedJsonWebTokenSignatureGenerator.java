package com.objectcomputing.checkins.security.authentication.token.jwt.signature;

import com.objectcomputing.checkins.security.authentication.token.jwt.JsonWebTokenPayload;
import com.objectcomputing.checkins.security.authentication.token.jwt.SignedJsonWebToken;
import com.objectcomputing.checkins.security.authentication.token.jwt.signature.SignedTokenGenerator;

public interface SignedJsonWebTokenSignatureGenerator extends SignedTokenGenerator<SignedJsonWebToken, JsonWebTokenPayload> {
}

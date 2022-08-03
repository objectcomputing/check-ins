package com.objectcomputing.checkins.security.authentication.token.jwt.signature;

import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenPayload;
import com.objectcomputing.geoai.security.token.jwt.SignedJsonWebToken;
import com.objectcomputing.geoai.security.token.signature.SignedTokenGenerator;

public interface SignedJsonWebTokenSignatureGenerator extends SignedTokenGenerator<SignedJsonWebToken, JsonWebTokenPayload> {
}

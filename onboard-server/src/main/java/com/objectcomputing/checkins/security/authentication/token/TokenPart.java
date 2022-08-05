package com.objectcomputing.checkins.security.authentication.token;

import com.objectcomputing.checkins.security.authentication.token.util.Base64Text;

public interface TokenPart {
    Base64Text toBase64Text();
}

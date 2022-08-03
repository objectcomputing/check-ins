package com.objectcomputing.checkins.auth;

public interface AuthErrorCodes {
    String NO_ACCOUNT_FOUND          = "AUTH0001";
    String INVALID_CODE_SECRETS      = "AUTH0002";
    String ACTIVATION_LIMIT_EXPIRED  = "AUTH0011";
    String ACTIVATION_CODE_NOT_FOUND = "AUTH0012";
    String UNKNOWN_ERROR             = "UKN01000";
}

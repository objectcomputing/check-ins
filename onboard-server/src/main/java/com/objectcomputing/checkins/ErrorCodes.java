package com.objectcomputing.checkins;

public interface ErrorCodes {
    String NO_ACCOUNT_FOUND       = "OB00001";
    String ACCOUNT_ALREADY_EXISTS = "OB00010";
    String FATAL_ERROR            = "OB00100";

    String INVALID_CODE_SECRETS      = "OB0002";
    String ACTIVATION_LIMIT_EXPIRED  = "OB0011";
    String ACTIVATION_CODE_NOT_FOUND = "OB0012";

    String UNKNOWN_ERROR             = "UKN01000";

}

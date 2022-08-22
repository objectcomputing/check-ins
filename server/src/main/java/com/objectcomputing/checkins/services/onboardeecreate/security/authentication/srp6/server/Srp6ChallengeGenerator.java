package com.objectcomputing.checkins.services.onboardeecreate.security.authentication.srp6.server;

import com.objectcomputing.checkins.services.onboardeecreate.security.authentication.srp6.Srp6Challenge;

public interface Srp6ChallengeGenerator {
    Srp6Challenge generate();
}

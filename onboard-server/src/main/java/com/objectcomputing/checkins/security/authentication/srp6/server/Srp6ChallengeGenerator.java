package com.objectcomputing.checkins.security.authentication.srp6.server;

import com.objectcomputing.geoai.security.authentication.srp6.Srp6Challenge;

public interface Srp6ChallengeGenerator {
    Srp6Challenge generate();
}

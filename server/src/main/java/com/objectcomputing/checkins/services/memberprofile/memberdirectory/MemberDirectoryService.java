package com.objectcomputing.checkins.services.memberprofile.memberdirectory;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

public interface MemberDirectoryService {
    MemberProfile getByEmailAddress(String workEmail);
}

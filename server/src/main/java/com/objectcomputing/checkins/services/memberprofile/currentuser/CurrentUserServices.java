package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

public interface CurrentUserServices {

    MemberProfile findOrSaveUser(String name, String workEmail);
}

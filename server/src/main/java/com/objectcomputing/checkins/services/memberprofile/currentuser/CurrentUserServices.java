package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;

public interface CurrentUserServices {

    MemberProfileEntity findOrSaveUser(String name, String workEmail);
}

package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import java.io.IOException;

public interface MemberPhotoService {
    byte[] getImageByEmailAddress(String workEmail) throws IOException;
}

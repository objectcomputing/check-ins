package com.objectcomputing.checkins.services.memberprofile.memberdirectory;

import java.io.IOException;
import java.util.HashMap;

public interface MemberDirectoryService {
    HashMap<String, String> getImagesOfAllUsers();
    String getImageByEmailAddress(String workEmail);
    void setImagesOfAllUsers() throws IOException;
}

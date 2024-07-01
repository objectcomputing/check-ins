package com.objectcomputing.checkins.services.memberprofile.memberphoto;

/**
 * Interface to access Google Photo data, and allow for simpler mocking in tests
 */
interface GooglePhotoAccessor {

    byte[] getPhotoData(String workEmail);
}

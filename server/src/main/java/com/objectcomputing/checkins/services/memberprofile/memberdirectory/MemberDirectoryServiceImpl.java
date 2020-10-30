package com.objectcomputing.checkins.services.memberprofile.memberdirectory;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.UserPhoto;
import com.google.api.services.admin.directory.model.Users;
import com.objectcomputing.checkins.util.googleapiaccess.GoogleApiAccess;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

@Singleton
public class MemberDirectoryServiceImpl implements MemberDirectoryService{

    private final GoogleApiAccess googleApiAccess;

    public MemberDirectoryServiceImpl(GoogleApiAccess googleApiAccess) {
        this.googleApiAccess = googleApiAccess;
    }

    HashMap<String, String> googlePhotos = new HashMap<String, String>();

    @Override
    public HashMap<String, String> getImagesOfAllUsers() {
        return this.googlePhotos;
    }

    @Override
    public String getImageByEmailAddress(String workEmail) {
        return googlePhotos.get(workEmail);
    }

    @Override
    public void setImagesOfAllUsers() throws IOException {

        Directory directory = googleApiAccess.getDirectory();
        Users userDirectory = directory.users().list()
                                .setDomain("objectcomputing.com")
                                .setMaxResults(500)
                                .execute();

        for (User user : userDirectory.getUsers()) {
            String email = user.getPrimaryEmail();
            if(user.getThumbnailPhotoUrl() != null) {
                UserPhoto userPhoto = directory.users().photos().get(email).execute();
                String photoData = convertPhotoData(userPhoto.getPhotoData());
                googlePhotos.put(email, photoData);
            } else {
                googlePhotos.put(email, "");
            }
        }
    }

    private String convertPhotoData(String photoData) {
        // converts an encoded URL to String URL
        byte[] actualByte = Base64.getUrlDecoder().decode(photoData);
        byte[] encodedByte = Base64.getEncoder().encode(actualByte);
        return new String(encodedByte);
    }
}
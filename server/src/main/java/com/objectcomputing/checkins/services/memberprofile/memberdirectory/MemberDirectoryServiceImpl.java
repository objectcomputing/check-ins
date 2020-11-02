package com.objectcomputing.checkins.services.memberprofile.memberdirectory;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.UserPhoto;
import com.objectcomputing.checkins.util.googleapiaccess.GoogleApiAccess;
import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.CachePut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

@Singleton
@CacheConfig("photo-cache")
public class MemberDirectoryServiceImpl implements MemberDirectoryService {

    private final GoogleApiAccess googleApiAccess;
    private static final Logger LOG = LoggerFactory.getLogger(MemberDirectoryServiceImpl.class);

    public MemberDirectoryServiceImpl(GoogleApiAccess googleApiAccess) {
        this.googleApiAccess = googleApiAccess;
    }

    HashMap<String, String> googlePhotos = new HashMap<>();

    @Override
    @CachePut(parameters = {"workEmail"})
    public String getImageByEmailAddress(@NotNull String workEmail) {

        if (!googlePhotos.containsKey(workEmail)) {
            Directory directory = googleApiAccess.getDirectory();

            try {
                UserPhoto userPhoto = directory.users().photos().get(workEmail).execute();
                String photoData = convertPhotoData(userPhoto.getPhotoData());
                googlePhotos.put(workEmail, photoData);
            } catch (IOException e) {
                LOG.error("Error occurred while retrieving files from Google Directory API.", e);
                googlePhotos.put(workEmail, "");
            }
        }

        return googlePhotos.get(workEmail);
    }

    private String convertPhotoData(String photoData) {
        // converts an encoded URL to String URL
        byte[] actualByte = Base64.getUrlDecoder().decode(photoData);
        byte[] encodedByte = Base64.getEncoder().encode(actualByte);
        return new String(encodedByte);
    }
}

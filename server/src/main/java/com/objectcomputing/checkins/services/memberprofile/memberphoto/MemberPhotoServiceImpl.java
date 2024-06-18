package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.services.directory.Directory;
import com.google.api.services.directory.model.UserPhoto;
import com.objectcomputing.checkins.util.googleapiaccess.GoogleApiAccess;
import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.context.env.Environment;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;

@Singleton
@CacheConfig("photo-cache")
public class MemberPhotoServiceImpl implements MemberPhotoService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberPhotoServiceImpl.class);
    private final GoogleApiAccess googleApiAccess;
    private final byte[] defaultPhoto;

    public MemberPhotoServiceImpl(GoogleApiAccess googleApiAccess,
                                  Environment environment) {
        byte[] defaultPhoto = new byte[0];
        this.googleApiAccess = googleApiAccess;
        Optional<URL> resource = environment.getResource("public/default_profile.jpg");
        try {
            if(resource.isPresent()) {
                URL defaultImageUrl = resource.get();
                InputStream in = defaultImageUrl.openStream();
                defaultPhoto = IOUtils.toByteArray(in);
            }
        } catch (IOException e) {
            LOG.error("Error occurred while loading the default profile photo.", e);
        }
        this.defaultPhoto = defaultPhoto;
    }

    @Override
    @Cacheable
    public byte[] getImageByEmailAddress(@NotNull String workEmail) {

        byte[] photoData;

        Directory directory = googleApiAccess.getDirectory();
        try {
            UserPhoto userPhoto = directory.users().photos().get(workEmail).execute();
            photoData = Base64.getUrlDecoder().decode(userPhoto.getPhotoData());
            LOG.debug(String.format("Photo data successfully retrieved from Google Directory API for: %s", workEmail));
        } catch(GoogleJsonResponseException gjse) {
            if(gjse.getStatusCode() == HttpStatusCodes.STATUS_CODE_NOT_FOUND) {
                LOG.info(String.format("No photo was found for: %s", workEmail));
            } else {
                LOG.error(String.format("An unexpected error occurred while retrieving photo from Google Directory API for: %s", workEmail), gjse);
            }
            photoData = defaultPhoto;
        } catch (IOException e) {
            LOG.error(String.format("An unexpected error occurred while retrieving photo from Google Directory API for: %s", workEmail), e);
            photoData = defaultPhoto;
        }

        return photoData;
    }
}

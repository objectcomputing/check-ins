package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.UserPhoto;
import com.objectcomputing.checkins.util.googleapiaccess.GoogleApiAccess;
import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.context.env.Environment;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

@Singleton
@CacheConfig("photo-cache")
public class MemberPhotoServiceImpl implements MemberPhotoService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberPhotoServiceImpl.class);
    private final GoogleApiAccess googleApiAccess;
    private final Environment environment;

    public MemberPhotoServiceImpl(GoogleApiAccess googleApiAccess,
                                  Environment environment) {
        this.googleApiAccess = googleApiAccess;
        this.environment = environment;
    }

    @Override
    @Cacheable
    public byte[] getImageByEmailAddress(@NotNull String workEmail) throws IOException {

        byte[] photoData;
        Directory directory = googleApiAccess.getDirectory();

        try {
            UserPhoto userPhoto = directory.users().photos().get(workEmail).execute();
            photoData = Base64.getUrlDecoder().decode(userPhoto.getPhotoData());
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Directory API.", e);
            URL defaultImageUrl = environment.getResource("public/default_profile.jpg").get();
            InputStream in = defaultImageUrl.openStream();
            photoData = IOUtils.toByteArray(in);
        }

        return photoData;
    }
}

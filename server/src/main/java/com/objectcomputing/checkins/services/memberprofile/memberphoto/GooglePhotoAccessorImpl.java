package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.services.directory.Directory;
import com.google.api.services.directory.model.UserPhoto;
import com.objectcomputing.checkins.util.googleapiaccess.GoogleApiAccess;
import io.micronaut.context.env.Environment;
import jakarta.inject.Singleton;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;

@Singleton
// Public so that this class can be replaced during testing.
public class GooglePhotoAccessorImpl implements GooglePhotoAccessor {

    private static final Logger LOG = LoggerFactory.getLogger(GooglePhotoAccessorImpl.class);

    private final byte[] defaultPhoto;
    private final GoogleApiAccess googleApiAccess;

    GooglePhotoAccessorImpl(
            Environment environment,
            GoogleApiAccess googleApiAccess
    ) {
        this.googleApiAccess = googleApiAccess;
        byte[] localDefaultPhoto = new byte[0];
        Optional<URL> resource = environment.getResource("public/default_profile.jpg");
        try {
            if(resource.isPresent()) {
                URL defaultImageUrl = resource.get();
                InputStream in = defaultImageUrl.openStream();
                localDefaultPhoto = IOUtils.toByteArray(in);
            }
        } catch (IOException e) {
            LOG.error("Error occurred while loading the default profile photo.", e);
        }
        this.defaultPhoto = localDefaultPhoto;
    }

    @Override
    public byte[] getPhotoData(String workEmail) {
        Directory directory = googleApiAccess.getDirectory();
        try {
            UserPhoto userPhoto = directory.users().photos().get(workEmail).execute();
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Photo data successfully retrieved from Google Directory API for: %s", workEmail));
            }
            return Base64.getUrlDecoder().decode(userPhoto.getPhotoData());
        } catch (GoogleJsonResponseException gjse) {
            if (gjse.getStatusCode() == HttpStatusCodes.STATUS_CODE_NOT_FOUND) {
                LOG.info(String.format("No photo was found for: %s", workEmail));
            } else {
                LOG.error(String.format("An unexpected error occurred while retrieving photo from Google Directory API for: %s", workEmail), gjse);
            }
            return defaultPhoto;
        } catch (IOException e) {
            LOG.error(String.format("An unexpected error occurred while retrieving photo from Google Directory API for: %s", workEmail), e);
            return defaultPhoto;
        }
    }
}

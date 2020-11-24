package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.UserPhoto;
import com.objectcomputing.checkins.services.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.util.googleapiaccess.GoogleApiAccess;
import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Base64;
import java.util.Set;

@Singleton
@CacheConfig("photo-cache")
public class MemberPhotoServiceImpl implements MemberPhotoService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberPhotoServiceImpl.class);
    private final GoogleApiAccess googleApiAccess;
    private final MemberProfileServices memberProfileServices;

    public MemberPhotoServiceImpl(GoogleApiAccess googleApiAccess, MemberProfileServices memberProfileServices) {
        this.googleApiAccess = googleApiAccess;
        this.memberProfileServices = memberProfileServices;
    }

    @Override
    @Cacheable
    public byte[] getImageByEmailAddress(@NotNull String workEmail) {

        byte[] photoData = new byte[0];

        Set<MemberProfile> memberProfile = memberProfileServices.findByValues(null, null, null, workEmail, null);
        if(memberProfile.isEmpty()) {
            throw new NotFoundException(String.format("No member profile exists for the email %s", workEmail));
        }

        Directory directory = googleApiAccess.getDirectory();

        try {
            UserPhoto userPhoto = directory.users().photos().get(workEmail).execute();
            photoData = Base64.getUrlDecoder().decode(userPhoto.getPhotoData());
        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Directory API.", e);
        }

        return photoData;
    }
}

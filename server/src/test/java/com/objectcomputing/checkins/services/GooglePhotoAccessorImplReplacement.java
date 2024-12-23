package com.objectcomputing.checkins.services;

import com.google.api.services.directory.model.UserPhoto;
import com.objectcomputing.checkins.services.memberprofile.memberphoto.GooglePhotoAccessorImpl;
import com.objectcomputing.checkins.services.memberprofile.memberphoto.GooglePhotoAccessor;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Base64;

import jakarta.inject.Singleton;
import io.micronaut.core.util.StringUtils;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;

@Singleton
@Replaces(GooglePhotoAccessorImpl.class)
@Requires(property = "replace.googlephotoaccessorimpl", value = StringUtils.TRUE)
public class GooglePhotoAccessorImplReplacement implements GooglePhotoAccessor {
    Map<String, UserPhoto> photos = new HashMap<>();

    public void reset() {
      photos.clear();
    }

    public void setUserPhoto(String email, UserPhoto photo) {
        photos.put(email, photo);
    }

    @Override
    public byte[] getPhotoData(String workEmail) {
        UserPhoto photo = photos.get(workEmail);
        return photo == null
            ? new byte[0]
            : Base64.getUrlDecoder().decode(photo.getPhotoData().getBytes());
    }
}

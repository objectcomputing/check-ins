package com.objectcomputing.checkins.services.memberprofile.memberphoto;

import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.Cacheable;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

@Singleton
@CacheConfig("photo-cache")
public class MemberPhotoServiceImpl implements MemberPhotoService {

    private final GooglePhotoAccessor googlePhotoAccessor;

    MemberPhotoServiceImpl(
            GooglePhotoAccessor googlePhotoAccessor
    ) {
        this.googlePhotoAccessor = googlePhotoAccessor;
    }

    @Override
    @Cacheable
    public byte[] getImageByEmailAddress(@NotNull String workEmail) {
        return googlePhotoAccessor.getPhotoData(workEmail);
    }
}

package com.objectcomputing.checkins.util.googleapiaccess;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.drive.Drive;
import com.objectcomputing.checkins.Environments;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import io.micronaut.scheduling.annotation.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;

@Requires(notEnv = Environment.TEST)
@Requires(notEnv = Environments.INTEGRATION)
@Singleton

public class GoogleApiAccess implements ApplicationEventListener<ServerStartupEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleApiAccess.class);

    private final GoogleAccessor googleAccessor;

    public GoogleApiAccess(GoogleAccessor googleAccessor) {
        this.googleAccessor = googleAccessor;
    }

    private Drive drive;

    private Directory directory;

    public Drive getDrive() {
        return drive;
    }

    public Directory getDirectory() {
        return directory;
    }

    @Async
    @Override
    public void onApplicationEvent(ServerStartupEvent event) {
        try {
            this.drive = googleAccessor.accessGoogleDrive();
            this.directory = googleAccessor.accessGoogleDirectory();
        } catch (IOException e) {
            LOG.error("An error occurred while initializing Google Drive access.", e);
        }
    }
}

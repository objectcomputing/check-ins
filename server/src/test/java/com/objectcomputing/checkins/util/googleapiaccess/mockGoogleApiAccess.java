package com.objectcomputing.checkins.util.googleapiaccess;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.drive.Drive;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import org.mockito.Mock;

import javax.inject.Singleton;

@Singleton
@Replaces(GoogleApiAccess.class)
public class mockGoogleApiAccess implements ApplicationEventListener<ServerStartupEvent> {

    private Drive drive;
    private Directory directory;

    public Drive getDrive() {
        return drive;
    }

    public Directory getDirectory() {
        return directory;
    }

    @Override
    public void onApplicationEvent(ServerStartupEvent event) {
        this.drive = new Mock(Drive.class);
        this.directory = new Mock(Directory.class);
    }
}

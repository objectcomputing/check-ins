package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.nio.ByteBuffer;
import java.io.IOException;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
public class ReportDataServicesImpl extends TimerTask implements ReportDataServices {

    private class Stored {
      public Date timestamp;
      public Map<String, ByteBuffer> data;

      public Stored() {
          timestamp = new Date();
          data = new HashMap<String, ByteBuffer>();
      }
    }

    private static final Logger LOG = LoggerFactory.getLogger(ReportDataServicesImpl.class);

    private final CurrentUserServices currentUserServices;
    private final Map<UUID, Stored> storedUploads = new HashMap<UUID, Stored>();
    private final Timer timer = new Timer();
    private final long expireCheck = 10*60*1000;
    private final long expiration = 60*60*1000;

    public ReportDataServicesImpl(CurrentUserServices currentUserServices) {
        this.currentUserServices = currentUserServices;

        timer.scheduleAtFixedRate(this, new Date(), expireCheck);
    }


    // Synchronized since we can upload multiple files at one time and there
    // is an expiration timer modifying the map too.
    @Override
    public synchronized void store(DataType dataType, CompletedFileUpload file) throws IOException {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
        validate(!isAdmin, NOT_AUTHORIZED_MSG);

        // Get the map for the current user.
        Stored perUser;
        UUID id = currentUser.getId();
        if (storedUploads.containsKey(id)) {
            perUser = storedUploads.get(id);
        } else {
            perUser = new Stored();
            storedUploads.put(id, perUser);
        }

        // Update the timestamp to allow us to check later to see if we
        // need to remove this user's data.
        perUser.timestamp = new Date();

        // Store the user's data based on the determined data type.
        perUser.data.put(dataType.name(), file.getByteBuffer());
    }

    @Override
    public ByteBuffer get(DataType dataType) throws NotFoundException {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
        validate(!isAdmin, NOT_AUTHORIZED_MSG);

        UUID id = currentUser.getId();
        if (storedUploads.containsKey(id)) {
            Stored perUser = storedUploads.get(id);
            String name = dataType.name();
            if (perUser.data.containsKey(name)) {
                // Update the timestamp to allow us to check later to see if we
                // need to remove this user's data.
                perUser.timestamp = new Date();
                return perUser.data.get(name);
            }
        }
        throw new NotFoundException(dataType.toString() +
                                    " Document does not exist");
    }

    // Synchronized since we can upload multiple files at one time and data
    // could expire symultaneously as well.
    // Check periodically to see if any data has expired.  If it has, remove
    // it.
    @Override
    public synchronized void run() {
        long current = (new Date()).getTime();
        for (Map.Entry<UUID, Stored> entry : storedUploads.entrySet()) {
            Stored value = entry.getValue();
            if (current >= (value.timestamp.getTime() + expiration)) {
                LOG.warn("Removing stored data for " + entry.getKey().toString());
                storedUploads.remove(entry.getKey());
            }
        }
    }

    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }
}

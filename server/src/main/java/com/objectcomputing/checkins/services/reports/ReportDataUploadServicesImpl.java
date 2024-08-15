package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.memberprofile.memberphoto.MemberPhotoServiceImpl;
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
public class ReportDataUploadServicesImpl extends TimerTask implements ReportDataUploadServices {

    private class Stored {
      public Date timestamp;
      public Map<String, ByteBuffer> data;

      public Stored() {
          data = new HashMap<String, ByteBuffer>();
      }
    }

    private static final Logger LOG = LoggerFactory.getLogger(ReportDataUploadServicesImpl.class);

    private final CurrentUserServices currentUserServices;
    private final Map<UUID, Stored> storedUploads = new HashMap<UUID, Stored>();
    private final Timer timer = new Timer();
    private final long expireCheck = 10*60*1000;
    private final long expiration = 60*60*1000;

    public ReportDataUploadServicesImpl(
                                   CurrentUserServices currentUserServices) {
        this.currentUserServices = currentUserServices;

        timer.scheduleAtFixedRate(this, new Date(), expireCheck);
    }


    @Override
    public void store(UUID memberId, CompletedFileUpload file) throws IOException, BadArgException {
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

        // Translate the file name to a data type that we know about.
        String fileName = file.getName().toLowerCase();
        DataType dataType;
        if (fileName.contains("comp")) {
            dataType = DataType.compensationHistory;
        } else if (fileName.contains("position")) {
            dataType = DataType.positionHistory;
        } else if (fileName.contains("current") ||
                   fileName.contains("information")) {
            dataType = DataType.currentInformation;
        } else {
            throw new BadArgException("Unable to determine data type: " + fileName);
        }
        String name = getKeyName(memberId, dataType);

        // Update the timestamp to allow us to check later to see if we
        // need to remove this user's data.
        perUser.timestamp = new Date();
        perUser.data.put(name, file.getByteBuffer());
    }

    @Override
    public ByteBuffer get(UUID memberId, DataType dataType) throws NotFoundException {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
        validate(!isAdmin, NOT_AUTHORIZED_MSG);

        UUID id = currentUser.getId();
        if (storedUploads.containsKey(id)) {
            Stored perUser = storedUploads.get(id);
            String name = getKeyName(memberId, dataType);
            if (perUser.data.containsKey(name)) {
                // Update the timestamp to allow us to check later to see if we
                // need to remove this user's data.
                perUser.timestamp = new Date();
                return perUser.data.get(name);
            }
        }
        throw new NotFoundException("Document does not exist");
    }

    /// Check periodically to see if any data has expired.  If it has, remove
    /// it.
    @Override
    public void run() {
        long current = (new Date()).getTime();
        for (Map.Entry<UUID, Stored> entry : storedUploads.entrySet()) {
            Stored value = entry.getValue();
            if (current >= (value.timestamp.getTime() + expiration)) {
                storedUploads.remove(entry.getKey());
            }
        }
    }

    private String getKeyName(UUID memberId, DataType dataType) {
        return memberId.toString() + "-" + dataType.toString();
    }

    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }
}

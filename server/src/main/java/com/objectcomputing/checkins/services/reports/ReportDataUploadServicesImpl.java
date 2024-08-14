package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.exceptions.BadArgException;
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

    public ReportDataUploadServicesImpl(
                                   CurrentUserServices currentUserServices) {
        this.currentUserServices = currentUserServices;

        timer.scheduleAtFixedRate(this, new Date(), 10*60*1000);
    }


    @Override
    public void store(CompletedFileUpload file) throws IOException {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
        validate(!isAdmin, NOT_AUTHORIZED_MSG);

        Stored perUser;
        UUID id = currentUser.getId();
        if (storedUploads.containsKey(id)) {
            perUser = storedUploads.get(id);
        } else {
            perUser = new Stored();
            storedUploads.put(id, perUser);
        }
        perUser.timestamp = new Date();
        perUser.data.put(file.getName(), file.getByteBuffer());
    }

    public ByteBuffer get(String name) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
        validate(!isAdmin, NOT_AUTHORIZED_MSG);

        UUID id = currentUser.getId();
        if (storedUploads.containsKey(id)) {
            Stored perUser = storedUploads.get(id);
            if (perUser.data.containsKey(name)) {
                perUser.timestamp = new Date();
                return perUser.data.get(name);
            }
        }
        throw new BadArgException("Document does not exist");
    }

    @Override
    public void run() {
        long current = (new Date()).getTime();
        for (Map.Entry<UUID, Stored> entry : storedUploads.entrySet()) {
            Stored value = entry.getValue();
            if (current >= (value.timestamp.getTime() + 60*60*1000)) {
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

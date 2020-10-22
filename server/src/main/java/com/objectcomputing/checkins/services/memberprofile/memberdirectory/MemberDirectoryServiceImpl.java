package com.objectcomputing.checkins.services.memberprofile.memberdirectory;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.Users;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.util.googleapiaccess.GoogleApiAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Singleton
public class MemberDirectoryServiceImpl implements MemberDirectoryService{

    private static final Logger LOG = LoggerFactory.getLogger(MemberDirectoryServiceImpl.class);
    private final GoogleApiAccess googleApiAccess;

    public MemberDirectoryServiceImpl(GoogleApiAccess googleApiAccess) {
        this.googleApiAccess = googleApiAccess;
    }

    @Override
    public MemberProfile getByEmailAddress(String workEmail) {
        try {
            Directory directory = googleApiAccess.getDirectory();
            Users result = directory.users().list()
                            .setDomain("objectcomputing.com")
                            .setMaxResults(10)
                            .setOrderBy("email")
                            .execute();
            List<User> users = result.getUsers();
            if (users == null || users.size() == 0) {
                System.out.println("No users found.");
            } else {
                System.out.println("Users:");
                for (User user : users) {
                    System.out.println(user.getName().getFullName());
                }
            }

        } catch (IOException e) {
            LOG.error("Error occurred while retrieving files from Google Directory API", e);
        }

        return null;
    }
}
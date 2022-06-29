package com.objectcomputing.checkins.services.email;

import java.util.List;

public interface EmailServices {

    List<Email> sendAndSaveEmail(String subject, String content, boolean html, String... recipients);

}

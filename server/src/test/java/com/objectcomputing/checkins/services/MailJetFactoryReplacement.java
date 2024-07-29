package com.objectcomputing.checkins.services;

import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is a replacement for the MailJetFactory class. It provides a mock implementation of the EmailSender.
 * The mock implementation records the events that occur when the sendEmail, sendEmailReceivesStatus, and setEmailFormat
 * methods are called.
 * <p>
 * The mock implementation is used when the "replace.mailjet.factory" property is set to true.
 * <p>
 * This is used instead of Mockito so that it works when running nativeTest under GraalVM.
 */
@Factory
@Replaces(MailJetFactory.class)
@Requires(property = "replace.mailjet.factory", value = StringUtils.TRUE)
public class MailJetFactoryReplacement {

    @Singleton
    @Named(MailJetFactory.HTML_FORMAT)
    @Replaces(value = EmailSender.class, named = MailJetFactory.HTML_FORMAT)
    MockEmailSender getHtmlSender() {
        return new MockEmailSender();
    }

    @Singleton
    @Named(MailJetFactory.TEXT_FORMAT)
    @Replaces(value = EmailSender.class, named = MailJetFactory.TEXT_FORMAT)
    MockEmailSender getTextSender() {
        return new MockEmailSender();
    }

    public static class MockEmailSender implements EmailSender {

        public final List<List<String>> events = new ArrayList<>();
        private RuntimeException exception;

        static String nullSafe(String s) {
            return s == null ? "null" : s;
        }

        public void reset() {
            exception = null;
            events.clear();
        }

        public void setException(RuntimeException exception) {
            this.exception = exception;
        }

        private void maybeThrow() {
            if (exception != null) {
                throw exception;
            }
        }

        @Override
        public void sendEmail(String fromName, String fromAddress, String subject, String content, String... recipients) {
            events.add(List.of(
                    "SEND_EMAIL",
                    nullSafe(fromName),
                    nullSafe(fromAddress),
                    nullSafe(subject),
                    nullSafe(content),
                    Arrays.stream(recipients).map(MockEmailSender::nullSafe).collect(Collectors.joining(","))
            ));
            maybeThrow();
        }

        @Override
        public boolean sendEmailReceivesStatus(String fromName, String fromAddress, String subject, String content, String... recipients) {
            events.add(List.of(
                    "SEND_EMAIL_RECEIVES_STATUS",
                    nullSafe(fromName),
                    nullSafe(fromAddress),
                    nullSafe(subject),
                    nullSafe(content),
                    Arrays.stream(recipients).map(MockEmailSender::nullSafe).collect(Collectors.joining(","))
            ));
            maybeThrow();
            return true;
        }

        @Override
        public void setEmailFormat(String format) {
            events.add(List.of(
                    "SET_EMAIL_FORMAT",
                    nullSafe(format)
            ));
            maybeThrow();
        }
    }
}

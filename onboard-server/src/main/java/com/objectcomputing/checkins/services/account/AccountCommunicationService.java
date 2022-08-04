package com.objectcomputing.checkins.services.account;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;

@Singleton
public class AccountCommunicationService {
    Logger LOG = LoggerFactory.getLogger(AccountCommunicationService.class.getSimpleName());

    public Mono<Object> sendEmail(String subject, List<String> recipients, String message) {
        LOG.info("Email {} To: {}", subject, recipients.get(0));
        LOG.info("Email Message: {}", message);
        return Mono.just(new Object());
    }
}
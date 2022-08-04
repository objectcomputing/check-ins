package com.objectcomputing.checkins.services.account;

import com.objectcomputing.checkins.services.model.LoginAccount;
import com.objectcomputing.checkins.services.account.dependencies.AccountDriveService;
import com.objectcomputing.checkins.services.account.dependencies.AccountProfileService;
import com.objectcomputing.checkins.services.account.endpoint.UserAccountConfig;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Singleton
public class AccountInitializationServices {

    private final AccountProfileService accountProfileService;
    private final AccountDriveService accountDriveService;

    public AccountInitializationServices(AccountProfileService accountProfileService,
                                         AccountDriveService accountDriveService) {
        this.accountProfileService = accountProfileService;
        this.accountDriveService = accountDriveService;
    }

    public Mono<Object> initializeAccount(LoginAccount userAccount, UserAccountConfig userAccountConfig) {
        return accountProfileService.createProfile(userAccount, userAccountConfig)
                .flatMap(success -> accountDriveService.initialize(userAccount));
    }

}

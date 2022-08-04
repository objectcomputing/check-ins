package com.objectcomputing.checkins.services.account;

import com.objectcomputing.geoai.platform.account.commons.AuthorizedProduct;
import com.objectcomputing.geoai.platform.account.model.*;
import com.objectcomputing.geoai.platform.product.model.ProductOffering;
import com.objectcomputing.geoai.platform.product.model.ProductOfferingRepository;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Singleton
public class AuthorizedProductService {
    final List<String> defaultProducts = Arrays.asList("Account", "Layers", "Notebook");

    private final ProductOfferingRepository productOfferingRepository;
    private final AuthorizedProductOfferingRepository authorizedProductOfferingRepository;

    public AuthorizedProductService(ProductOfferingRepository productOfferingRepository, AuthorizedProductOfferingRepository authorizedProductOfferingRepository) {
        this.productOfferingRepository = productOfferingRepository;
        this.authorizedProductOfferingRepository = authorizedProductOfferingRepository;
    }

    public Mono<Void> addDefaultAuthorizedProducts(Organization organization) {
        return Flux.fromIterable(defaultProducts)
                .flatMap(productOfferingRepository::findByProductName)
                .flatMap(productOffering -> authorizedProductOfferingRepository.save(
                        new AuthorizedProductOffering(
                                productOffering,
                                organization.getId(),
                                CustomerType.Organization)))
                .then();
    }

    public Flux<AuthorizedProduct> getAuthorizedProducts(UserAccount userAccount) {
        Flux<AuthorizedProductOffering> individualProductOfferings =
                authorizedProductOfferingRepository.findByCustomerIdAndCustomerType(userAccount.getId(), CustomerType.Individual);

        Flux<AuthorizedProductOffering> organizationalProductOfferings =
                authorizedProductOfferingRepository.findByCustomerIdAndCustomerType(userAccount.getOrganization().getId(), CustomerType.Organization);

        return Flux.merge(individualProductOfferings, organizationalProductOfferings)
                .flatMap(this::convert)
                .distinct();
    }

    private Mono<AuthorizedProduct> convert(AuthorizedProductOffering authorizedProductOffering) {
        return Mono.just(new AuthorizedProduct(
                authorizedProductOffering.getProductOffering().getProduct().getId(),
                authorizedProductOffering.getProductOffering().getProduct().getName(),
                authorizedProductOffering.getProductOffering().getProduct().getUrl(),
                authorizedProductOffering.getProductOffering().getState(),
                authorizedProductOffering.getProductOffering().getTier()));
    }
}

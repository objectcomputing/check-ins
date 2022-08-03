package com.objectcomputing.checkins.security.authentication.token.model;

import io.micronaut.data.annotation.*;
import lombok.Data;

import javax.persistence.Column;
import java.util.UUID;

import static io.micronaut.data.annotation.Relation.Kind.MANY_TO_ONE;

@Data
@MappedEntity("token_policy")
public class TokenPolicy {
    @Id
    @Column(name="token_policy_id")
    @AutoPopulated
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private UUID id;

    @Relation(MANY_TO_ONE)
    @Column(name = "token_id")
    private Token token;

    @Column(name="name")
    private String name;

    public TokenPolicy() {
    }

    public TokenPolicy(Token token, String name) {
        this();

        this.token = token;
        this.name = name;
    }
}
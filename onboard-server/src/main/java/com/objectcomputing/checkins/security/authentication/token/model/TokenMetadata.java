package com.objectcomputing.checkins.security.authentication.token.model;

import io.micronaut.data.annotation.*;
import lombok.Data;

import javax.persistence.Column;
import java.util.UUID;

import static io.micronaut.data.annotation.Relation.Kind.MANY_TO_ONE;

@Data
@MappedEntity("token_meta")
public class TokenMetadata {
    @Id
    @Column(name="token_meta_id")
    @AutoPopulated
    @GeneratedValue(GeneratedValue.Type.AUTO)
    private UUID id;

    @Relation(MANY_TO_ONE)
    @Column(name = "token_id")
    private Token token;

    @Column(name="key")
    private String key;

    @Column(name="value")
    private String value;

    public TokenMetadata() {
    }

    public TokenMetadata(Token token, String key, String value) {
        this();

        this.token = token;
        this.key = key;
        this.value = value;
    }

    public TokenMetadata(String key, String value) {
        this(null, key, value);
    }
}
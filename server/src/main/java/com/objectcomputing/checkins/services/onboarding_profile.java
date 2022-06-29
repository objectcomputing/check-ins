package com.objectcomputing.checkins.services;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity //specifies that the class is an entity and is mapped to a database table
@Introspected //indicates a type should produce a BeanIntrospection
@Table(name="onboard_profile") //specifies the name of the database table to be used for mappe
//see the file path ...src/resources/db/common to create the table schema from above with the name from above
public class onboarding_profile {

    @Id // indicates this member field below is the primary key of the current entity
    @Column(name="id") //indicates this value is stored under a column in the database with the name "id"
    @AutoPopulated //Micronaut will autopopulate a user id for each onboardee's profile automatically
    @TypeDef( type= DataType.STRING) //indicates what type of data will be stored in the database
    @Schema(description = "id of the new employee profile this entry is associated with", required = true)
    private UUID id;

    @NotBlank //the below field,firstName, is not allowed to be blank on submission
    @Column (name = "firstname")
    @ColumnTransformer (
            read =  "pgp_sym_decrypt(firstName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    ) // @columnTransformer and the code that follows allows the firstname field to be stored as encrypted in the database and then decrypted if you want to read it.
    @Schema (description = "first name of the new employee")
    private String firstName;

    @Nullable //indicates that it is ok if this field is blank
    @Column (name = "middlename")
    @ColumnTransformer (
            read =  "pgp_sym_decrypt(middleName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    ) // @columnTransformer and the code that follows allows the firstname field to be stored as encrypted in the database and then decrypted if you want to read it.
    @Schema (description = "middle name of the new employee")
    private String middleName;

    @NotBlank //the below field, lastName, is not allowed to be blank on submission
    @Column (name = "lastname")
    @ColumnTransformer (
            read =  "pgp_sym_decrypt(lastName::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    ) // @columnTransformer and the code that follows allows the firstname field to be stored as encrypted in the database and then decrypted if you want to read it.
    @Schema (description = "last name of the new employee")
    private String lastName;

}
